package org.kin.sdk.base.storage

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.SHA224Hash
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import org.kin.stellarfork.KeyPair
import java.time.Instant
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class KinFileStorageTest {

    companion object {
        val networkEnvironment = NetworkEnvironment.KinStellarTestNetKin3
    }

    lateinit var sut: Storage

    @Rule
    @JvmField
    public var tempFolder = TemporaryFolder()

    @Before
    fun setUp() {
        sut = KinFileStorage(
            tempFolder.root.invariantSeparatorsPath,
            NetworkEnvironment.KinStellarTestNetKin3,
            ExecutorServices()
        )
    }

    @After
    fun cleanup() {
        sut.deleteAllStorage()
    }

    @Test
    fun testCreateAndGetAccount() {
        val expectAccount = KinAccount(KeyPair.random().asPrivateKey())

        assertTrue(sut.addAccount(expectAccount), "account should be added")

        val actualAccount = sut.getAccount(expectAccount.id)

        assertEquals(expectAccount, actualAccount, "stored and retrieved accounts should equal")
    }

    @Test
    fun testCreateAndUpdateAccount() {
        val privateKey = KeyPair.random().asPrivateKey()
        val newAccount = KinAccount(privateKey)
        assertTrue(sut.addAccount(newAccount), "account should be added")

        val newBalance = KinBalance(KinAmount(100))
        val newStatus = KinAccount.Status.Registered(1)
        val expectUpdateAccount =
            KinAccount(key = privateKey, balance = newBalance, status = newStatus)

        sut.updateAccount(expectUpdateAccount)

        val actualAccount = sut.getAccount(expectUpdateAccount.id)
        assertEquals(actualAccount!!.balance, newBalance, "balance should be updated")
        assertEquals(actualAccount.status, newStatus, "status should be updated")
    }

    @Test
    fun testUpdateAccountAndKeepExistingPrivateKey() {
        val privateKey = KeyPair.random().asPrivateKey()
        val newAccount = KinAccount(privateKey)
        assertTrue(sut.addAccount(newAccount), "account should be added")

        val newBalance = KinBalance(KinAmount(100))
        val newStatus = KinAccount.Status.Registered(1)
        val accountToUpdate =
            KinAccount(key = privateKey.asPublicKey(), balance = newBalance, status = newStatus)

        sut.updateAccount(accountToUpdate)

        val actualAccount = sut.getAccount(accountToUpdate.id)
        assertTrue(
            actualAccount!!.key is Key.PrivateKey,
            "updating account with public key shouldn't erase existing private key"
        )
        assertEquals(actualAccount.balance, newBalance, "balance should be updated")
        assertEquals(actualAccount.status, newStatus, "status should be updated")
    }

    @Test
    fun testUpdateAccountAndFillPrivateKey() {
        val privateKey = KeyPair.random().asPrivateKey()
        val publicKey = privateKey.asPublicKey()
        val newAccount = KinAccount(publicKey)
        assertTrue(sut.addAccount(newAccount), "account with only the public key should be added")

        val accountWithPrivateKey = KinAccount(privateKey)

        sut.updateAccount(accountWithPrivateKey)

        val actualAccount = sut.getAccount(accountWithPrivateKey.id)
        assertTrue(actualAccount!!.key is Key.PrivateKey, "updated account should have private key")
    }

    @Test
    fun testRemoveAccount() {
        val privateKey = KeyPair.random().asPrivateKey()
        val newAccount = KinAccount(privateKey)
        assertTrue(sut.addAccount(newAccount), "account should be added")

        assertTrue(sut.removeAccount(newAccount.id), "account should be removed");

        assertNull(sut.getAccount(newAccount.id), "no account should be retrieved");
    }

    @Test
    fun testAdvanceSequence() {
        val privateKey = KeyPair.random().asPrivateKey()
        val status = KinAccount.Status.Registered(1)
        val newAccount = KinAccount(key = privateKey, status = status)
        assertTrue(sut.addAccount(newAccount), "account should be added")

        val updatedAccount = sut.advanceSequence(newAccount.id)
        assertNotNull(updatedAccount, "updated account should not be null")
        assertEquals(
            (updatedAccount.status as KinAccount.Status.Registered).sequence,
            2,
            "sequence should be incremented by 1"
        )

        val retrievedAccount = sut.getAccount(newAccount.id)
        assertNotNull(
            retrievedAccount,
            "should be able to retrieve account after advancing sequence"
        )
        assertEquals(
            (updatedAccount.status as KinAccount.Status.Registered).sequence,
            2,
            "sequence change should persist"
        )
    }

    @Test
    fun testGetAllAccountIds() {
        var setOfIds = emptySet<KinAccount.Id>()

        for (i in 1..5) {
            val privateKey = KeyPair.random().asPrivateKey()
            val newAccount = KinAccount(privateKey)
            assertTrue(sut.addAccount(newAccount), "account should be added")
            setOfIds = setOfIds.plus(newAccount.id)
        }

        var retrievedIds = sut.getAllAccountIds().toSet()

        assertEquals(setOfIds, retrievedIds, "stored and retrieved ids should equal")
    }

    @Test
    fun testPutAndGetTransactions() {
        val newAccount = TestUtils.newKinAccount()

        val historicalTransaction = sampleHistoricalTransaction()
        val acknowledgedTransaction = sampleAcknowledgedTransaction()
        val inFlightTransaction = sampleInFlightTransaction()

        val transactionList = listOf(
            historicalTransaction,
            acknowledgedTransaction,
            inFlightTransaction
        )

        sut.putTransactions(
            newAccount.id,
            KinTransactions(
                transactionList,
                KinTransaction.PagingToken("token"),
                KinTransaction.PagingToken("token")
            )
        )

        val retrievedTransactions = sut.getTransactions(newAccount.id)
        assertEquals(
            3,
            retrievedTransactions?.items?.size,
            "number of transactions retrieved should equal to number stored"
        )

        val items = retrievedTransactions?.items ?: emptyList()
        assertTrue(items.contains(historicalTransaction))
        assertTrue(items.contains(acknowledgedTransaction))
        assertTrue(items.contains(inFlightTransaction))
    }

    @Test
    fun testRemoveTransactions() {
        val newAccount = TestUtils.newKinAccount()

        val historicalTransaction = sampleHistoricalTransaction()
        val acknowledgedTransaction = sampleAcknowledgedTransaction()
        val inFlightTransaction = sampleInFlightTransaction()

        val transactionList = listOf(
            historicalTransaction,
            acknowledgedTransaction,
            inFlightTransaction
        )

        sut.putTransactions(
            newAccount.id,
            KinTransactions(
                transactionList,
                KinTransaction.PagingToken("token"),
                KinTransaction.PagingToken("token")
            )
        )

        assertTrue(sut.removeAllTransactions(newAccount.id))

        assertEquals(0, sut.getTransactions(newAccount.id)?.items?.size ?: 0)
    }

    @Test
    fun testStoreTransactions() {
        val newAccount = TestUtils.newKinAccount()

        val historicalTransaction = sampleHistoricalTransaction()
        val acknowledgedTransaction = sampleAcknowledgedTransaction()
        val inFlightTransaction = sampleInFlightTransaction()

        val transactionList = listOf(
            historicalTransaction,
            acknowledgedTransaction,
            inFlightTransaction
        )

        sut.storeTransactions(
            newAccount.id,
            transactionList
        ).test {
            assertEquals(value, transactionList)
            assertNull(error)
        }
    }

    @Test
    fun testUpsertNewTransactionsInStorage_noStoredHistorical() {
        val newAccount = TestUtils.newKinAccount()

        val historicalTransaction = sampleHistoricalTransaction()
        val historicalTransaction2 = sampleHistoricalTransaction2()

        val acknowledgedTransaction = sampleAcknowledgedTransaction()
        val inFlightTransaction = sampleInFlightTransaction()

        val existingStoredTransactionList = listOf(
            acknowledgedTransaction,
            inFlightTransaction
        )

        val newHistoricalTransactions = listOf(historicalTransaction, historicalTransaction2)

        sut.storeTransactions(newAccount.id, existingStoredTransactionList).test {
            assertEquals(value, existingStoredTransactionList)
        }

        sut.upsertNewTransactionsInStorage(
            newAccount.id,
            newHistoricalTransactions
        ).test {
            assertEquals(
                value,
                listOf(historicalTransaction, historicalTransaction2, inFlightTransaction)
            )
        }
    }

    @Test
    fun testUpsertNewTransactionsInStorage_hasStoredHistorical() {
        // TODO
    }

    @Test
    fun testUpsertOldTransactionsInStorage_noStoredHistorical() {
        val newAccount = TestUtils.newKinAccount()

        val historicalTransaction = sampleHistoricalTransaction()
        val historicalTransaction2 = sampleHistoricalTransaction2()

        val acknowledgedTransaction = sampleAcknowledgedTransaction()
        val inFlightTransaction = sampleInFlightTransaction()

        val existingStoredTransactionList = listOf(
            acknowledgedTransaction,
            inFlightTransaction
        )

        val newHistoricalTransactions = listOf(historicalTransaction, historicalTransaction2)

        sut.storeTransactions(newAccount.id, existingStoredTransactionList).test {
            assertEquals(value, existingStoredTransactionList)
        }

        sut.upsertOldTransactionsInStorage(
            newAccount.id,
            newHistoricalTransactions
        ).test {
            assertEquals(
                value,
                listOf(inFlightTransaction, historicalTransaction, historicalTransaction2)
            )
        }
    }

    @Test
    fun testUpsertOldTransactionsInStorage_hasStoredHistorical() {
        // TODO
    }

    @Test
    fun testInsertNewTransactionInStorage() {
        val newAccount = TestUtils.newKinAccount()

        val inFlightTransaction = sampleInFlightTransaction()
        val newHistoricalTransactions =
            listOf(sampleHistoricalTransaction(), sampleHistoricalTransaction2())

        sut.storeTransactions(newAccount.id, newHistoricalTransactions).test {
            assertEquals(value, newHistoricalTransactions)
        }

        sut.insertNewTransactionInStorage(newAccount.id, inFlightTransaction).test {
            assertEquals(
                value,
                listOf(
                    inFlightTransaction,
                    sampleHistoricalTransaction(),
                    sampleHistoricalTransaction2()
                )
            )
        }
    }

    @Test
    fun testGetStoredAccount() {
        val newAccount: KinAccount = TestUtils.newKinAccount()

        sut.addAccount(newAccount)

        sut.getStoredAccount(newAccount.id).test {
            assertEquals(value?.get(), newAccount)
        }
    }

    @Test
    fun testUpdateAccountInStorage() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val updatedAccountWithNewBalance: KinAccount =
            newAccount.merge(
                KinAccount(
                    key = newAccount.key,
                    balance = KinBalance(KinAmount(1234L))
                )
            )

        sut.addAccount(newAccount)

        sut.updateAccountInStorage(updatedAccountWithNewBalance).test {
            assertEquals(value, updatedAccountWithNewBalance)
        }
    }

    @Test
    fun testUpdateAccountInStorage_newAccounts() {
        val newAccounts = listOf(TestUtils.newPublicKey(), TestUtils.newPublicKey(), TestUtils.newPublicKey())
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val updatedAccountWithNewAccounts: KinAccount =
            newAccount.merge(
                KinAccount(
                    key = newAccount.key,
                    tokenAccounts = newAccounts
                )
            )

        sut.addAccount(newAccount)

        sut.updateAccountInStorage(updatedAccountWithNewAccounts).test {
            assertTrue(value?.tokenAccounts?.containsAll(newAccounts) ?: false)
        }

        sut.getStoredAccount(newAccount.id).test {
            assertTrue(value?.map { it.tokenAccounts.containsAll(newAccounts) }?.orElse(false) ?: false)
        }
    }

    @Test
    fun testUpdatetBalanceFromAccount() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val updatedAccountWithBalance: KinAccount =
            newAccount.merge(KinAccount(key = newAccount.key, balance = KinBalance(KinAmount(100))))
        val expectedNewAccountBalance: KinAccount =
            newAccount.merge(KinAccount(key = newAccount.key, balance = KinBalance(KinAmount(30))))

        sut.addAccount(updatedAccountWithBalance)

        sut.updateAccountBalance(newAccount.id, KinBalance(KinAmount(100) - KinAmount(70))).test {
            assertEquals(value?.get(), expectedNewAccountBalance)
        }
    }

    @Test
    fun testDeleteAllStorageForAccount() {
        val newAccount: KinAccount = TestUtils.newKinAccount()

        sut.addAccount(newAccount)
        sut.deleteAllStorage(newAccount.id).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testDeleteAllStorage() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val newAccount2: KinAccount = TestUtils.newKinAccount()

        sut.addAccount(newAccount)
        sut.addAccount(newAccount2)

        sut.deleteAllStorage().test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testSetMinApiVersion() {
        sut.getMinApiVersion().test {
            assertEquals(Optional.empty(), value)
        }

        sut.setMinApiVersion(3).test {
            assertEquals(3, value)
        }
        sut.getMinApiVersion().test {
            assertEquals(Optional.of(3), value)
        }

        sut.setMinApiVersion(4).test {
            assertEquals(4, value)
        }
        sut.getMinApiVersion().test {
            assertEquals(Optional.of(4), value)
        }
    }

    @Test
    fun testGetMinFee() {
        sut.getMinFee().test {
            assertFalse(value!!.isPresent)
        }
    }

    @Test
    fun testAddGetMinFee_andGet() {

        sut.setMinFee(QuarkAmount(100)).test {
            assertEquals(QuarkAmount(100), value!!.get()!!)
        }
        sut.getMinFee().test {
            assertEquals(QuarkAmount(100), value!!.get()!!)
        }
    }

    @Test
    fun testInvoices() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val invoiceList = InvoiceList.Builder()
            .addInvoice(
                Invoice.Builder()
                    .addLineItems(
                        listOf(
                            LineItem.Builder("title2", KinAmount(10)).build(),
                            LineItem.Builder("title2", KinAmount(20)).build(),
                            LineItem.Builder("title3", KinAmount(30)).build()
                        )
                    )
                    .build()
            )
            .build()

        sut.addInvoiceLists(newAccount.id, invoiceLists = listOf(invoiceList))
            .test {
                assertTrue { value!!.contains(invoiceList) }
            }

        val newInvoiceList = InvoiceList.Builder()
            .addInvoice(
                Invoice.Builder()
                    .addLineItems(
                        listOf(
                            LineItem.Builder("title4", KinAmount(40)).build(),
                            LineItem.Builder("title5", KinAmount(50)).build()
                        )
                    )
                    .build()
            )
            .build()

        sut.addInvoiceLists(newAccount.id, invoiceLists = listOf(newInvoiceList))
            .test {
                assertEquals(listOf(invoiceList, newInvoiceList), value!!)
            }

        sut.getInvoiceListsMapForAccountId(newAccount.id)
            .map { it.values.toList() }
            .test {
                assertEquals(listOf(invoiceList, newInvoiceList), value!!)
            }

        sut.removeAllInvoices(newAccount.id)

        sut.getInvoiceListsMapForAccountId(newAccount.id)
            .map { it.values.toList() }
            .test {
                assertEquals(emptyList(), value!!)
            }
    }

    @Test
    fun testAddingTransactionsWithInvoices() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val invoiceList = InvoiceList.Builder().addInvoice(
            Invoice.Builder()
                .addLineItem(
                    LineItem.Builder("thing1", KinAmount(123))
                        .build()
                )
                .build()
        ).build()

        val transaction = sampleAcknowledgedTransactionWithInvoice()
        println(transaction.memo.getAgoraMemo()?.foreignKey)
        println(InvoiceList.Id(SHA224Hash.just(transaction.memo.getAgoraMemo()?.foreignKeyBytes!!)))

        var kinTransactions: KinTransactions? = null
        val latch = CountDownLatch(1)
        sut.storeTransactions(newAccount.id, listOf(transaction))
            .flatMap { sut.getStoredTransactions(newAccount.id) }
            .then {
                kinTransactions = it
                latch.countDown()
            }

        latch.await(10, TimeUnit.SECONDS)
        println(kinTransactions?.items?.first()?.memo?.getAgoraMemo()?.foreignKey)
        println(InvoiceList.Id(SHA224Hash.just(kinTransactions?.items?.first()?.memo?.getAgoraMemo()?.foreignKeyBytes!!)))
        assertNotNull(kinTransactions)
        assertEquals(invoiceList, kinTransactions!!.items.first().invoiceList)
    }

    @Test
    fun testAddingTransactionsWithInvoices_Solana() {
        val newAccount: KinAccount = TestUtils.newKinAccount()
        val invoiceList = InvoiceList.Builder().addInvoice(
            Invoice.Builder()
                .addLineItem(
                    LineItem.Builder("thing1", KinAmount(123))
                        .build()
                )
                .build()
        ).build()

        val transaction = sampleSolanaAcknowledgedTransactionWithInvoice()
        val transaction2 = sampleAcknowledgedTransactionWithInvoice()
        println(transaction.memo.getAgoraMemo()?.foreignKey)
        println(InvoiceList.Id(SHA224Hash.just(transaction.memo.getAgoraMemo()?.foreignKeyBytes!!)))

        var kinTransactions: KinTransactions? = null
        val latch = CountDownLatch(1)
        sut.storeTransactions(newAccount.id, listOf(transaction, transaction2))
            .flatMap { sut.getStoredTransactions(newAccount.id) }
            .then {
                kinTransactions = it
                latch.countDown()
            }

        latch.await(10, TimeUnit.SECONDS)

        println(kinTransactions?.items?.first()?.memo?.getAgoraMemo()?.foreignKey)
        println(InvoiceList.Id(SHA224Hash.just(kinTransactions?.items?.first()?.memo?.getAgoraMemo()?.foreignKeyBytes!!)))
        assertTrue(transaction.bytesValue.contentEquals(kinTransactions?.items?.get(0)?.bytesValue))

        assertNotNull(kinTransactions)
        assertEquals(invoiceList, kinTransactions!!.items.first().invoiceList)

        println(kinTransactions?.items?.get(1)?.memo?.getAgoraMemo()?.foreignKey)
        println(InvoiceList.Id(SHA224Hash.just(kinTransactions?.items?.get(1)?.memo?.getAgoraMemo()?.foreignKeyBytes!!)))
        assertEquals(invoiceList, kinTransactions!!.items.get(1).invoiceList)
        assertTrue(transaction2.bytesValue.contentEquals(kinTransactions?.items?.get(1)?.bytesValue))
    }

    @Test
    fun testGetOrCreateCID() {
        val cid1 = sut.getOrCreateCID()
        val cid2 = sut.getOrCreateCID()
        assertEquals(cid1, cid2)
    }

    private fun sampleHistoricalTransaction(): KinTransaction {
        // https://kinexplorer.com/tx/08b6da9f6d88ffe8a5d49d147c730a13f7a86cf43b4d5154e4782e865064ece8
        val sampleTransactionXdr =
            "AAAAAM0w5OyYgk/9XELLfs4xeZ8b5nC8+CI7mgojjaX0weftAAAAZAAi8BEAATqEAAAAAAAAAAEAAAAbMS1raWstVDlKMjlldWdKS0dxY0Znb3NadWozAAAAAAEAAAABAAAAANqYo7sYHe2m8CaE59Bk2HYfV6YyEGJ2BOVzDPZaIe52AAAAAQAAAABXK49bfqCCGtT+JXLre3m/oe/zTtRDiK5w6mpm0LpqDAAAAAAAAAAAAA9CQAAAAAAAAAAC9MHn7QAAAEDyK9vVadOeiAT+cXHGi6tgi/UoBSPsFJIxLuSZOrQBZxADvbalFHevHIRpDQFwav/V940nLBb4BXnZVYJ0VtcGWiHudgAAAECBPfWZ74UPg8VXyFncKVmaB35FtfK1eZ8EALWGDCVR5ryQcRDhFQibGu255VgIbWNbU5piKUJQmWcFiuVkOCIB"
        val sampleResultXdr = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="
        val pagingToken = "24664322143838208"
        val instant = Instant.parse("2019-12-13T15:33:14Z")

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val resultXdrBytes = Base64.getDecoder().decode(sampleResultXdr)
        val recordType = KinTransaction.RecordType.Historical(
            instant.epochSecond,
            resultXdrBytes,
            KinTransaction.PagingToken(pagingToken)
        )

        return StellarKinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }

    private fun sampleHistoricalTransaction2(): KinTransaction {
        // https://kinexplorer.com/tx/7512441be0d02d5006fe9f16d1483b826c2b11391375f4038e1f1dfef1c73203
        val sampleTransactionXdr =
            "AAAAAIlNwPv6DyvbLciMMPQDChCrd5zd3jilzPQpqEjWk2XVAAAAZAAi8AgAATsnAAAAAAAAAAEAAAAbMS1raWstVE1RTU1HejQ5WDVpdkRZRlNwam44AAAAAAEAAAABAAAAANqYo7sYHe2m8CaE59Bk2HYfV6YyEGJ2BOVzDPZaIe52AAAAAQAAAAA/Rqo3OyKoPmfduOwmCggmNVwBbclzTpx1jG4YBEaqRAAAAAAAAAAAAA9CQAAAAAAAAAAC1pNl1QAAAECWM3LVoTY423wXXkDwEdhKQl6fpbzNG3YprofaUfQI3sU6+1sGChS7Q/t16jCo+3Tqz48IKtk4Sq6tBL6Cy/4MWiHudgAAAEDWBqoNW2BwjiLrockaricMP36aQO2GOU93m1RjDOoBJ7tuNklwmRlQN8gxbGE+Et+CoCOdKfdW0bWZ2C5wgOMA"
        val sampleResultXdr = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="
        val instant = Instant.parse("2019-12-13T16:49:59Z")
        val pagingToken = "44664322143838208"

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val resultXdrBytes = Base64.getDecoder().decode(sampleResultXdr)
        val recordType = KinTransaction.RecordType.Historical(
            instant.epochSecond,
            resultXdrBytes,
            KinTransaction.PagingToken(pagingToken)
        )

        return StellarKinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }

    private fun sampleAcknowledgedTransaction(): KinTransaction {
        // https://kinexplorer.com/tx/7512441be0d02d5006fe9f16d1483b826c2b11391375f4038e1f1dfef1c73203
        val sampleTransactionXdr =
            "AAAAAIlNwPv6DyvbLciMMPQDChCrd5zd3jilzPQpqEjWk2XVAAAAZAAi8AgAATsnAAAAAAAAAAEAAAAbMS1raWstVE1RTU1HejQ5WDVpdkRZRlNwam44AAAAAAEAAAABAAAAANqYo7sYHe2m8CaE59Bk2HYfV6YyEGJ2BOVzDPZaIe52AAAAAQAAAAA/Rqo3OyKoPmfduOwmCggmNVwBbclzTpx1jG4YBEaqRAAAAAAAAAAAAA9CQAAAAAAAAAAC1pNl1QAAAECWM3LVoTY423wXXkDwEdhKQl6fpbzNG3YprofaUfQI3sU6+1sGChS7Q/t16jCo+3Tqz48IKtk4Sq6tBL6Cy/4MWiHudgAAAEDWBqoNW2BwjiLrockaricMP36aQO2GOU93m1RjDOoBJ7tuNklwmRlQN8gxbGE+Et+CoCOdKfdW0bWZ2C5wgOMA"
        val sampleResultXdr = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="
        val instant = Instant.parse("2019-12-13T16:49:59Z")

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val resultXdrBytes = Base64.getDecoder().decode(sampleResultXdr)
        val recordType = KinTransaction.RecordType.Acknowledged(instant.epochSecond, resultXdrBytes)

        return StellarKinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }

    private fun sampleAcknowledgedTransactionWithInvoice(): KinTransaction {
        // https://kinexplorer.com/tx/7512441be0d02d5006fe9f16d1483b826c2b11391375f4038e1f1dfef1c73203
        val sampleTransactionXdr =
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAANBAAC08lt5rUCyTjNL4kd1zYFXOcVi97U0BmyEaLU/AgAAAAEAAAAAAAAAAQAAAAAhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwAAAAAAAAAAALuu4AAAAAAAAAABwhjv+wAAAEBXRoEHUyFR6Y3nbQKoNghXWgFp1a3YVjNXdMJRlF+C7wRjiIcG1UJS7t/ccR8jndV1+P8/kqbcGvosD/lQrDcO"
        val sampleResultXdr = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="
        val instant = Instant.parse("2019-12-13T16:49:59Z")

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val resultXdrBytes = Base64.getDecoder().decode(sampleResultXdr)
        val recordType = KinTransaction.RecordType.Acknowledged(instant.epochSecond, resultXdrBytes)

        val invoice = Invoice.Builder()
            .addLineItem(
                LineItem.Builder("thing1", KinAmount(123))
                    .build()
            )
            .build()
        val invoiceList = InvoiceList.Builder().addInvoice(invoice).build()

        return StellarKinTransaction(transactionXdrBytes, recordType, networkEnvironment, invoiceList)
    }

    private fun sampleSolanaAcknowledgedTransactionWithInvoice(): KinTransaction {
        val sampleResultXdr = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="
        val instant = Instant.parse("2019-12-13T16:49:59Z")

        val resultXdrBytes = Base64.getDecoder().decode(sampleResultXdr)

        val invoice = Invoice.Builder()
            .addLineItem(
                LineItem.Builder("thing1", KinAmount(123))
                    .build()
            )
            .build()
        val invoiceList = InvoiceList.Builder().addInvoice(invoice).build()

        return TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADBhKbplKYVFe1zp0Qbm0sgmfDJ/4PaKI6sdhW5K2hYa3yTxBa4fJz/KclOzYQnutToS8NCcgtE1Zm43VjEEo8LAgACBe8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpBUpTUPhdyILWFKVWcniKKW3fHqur0KYGeIhJMvTu9qAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIEACxRUUFBdFBKYmVhMUFzazR6UytKSGRjMkJWem5GWXZlMU5BWnNoR2kxUHdJPQMDAQIBCQPgrrsAAAAAAA==",
            KinTransaction.RecordType.Acknowledged(instant.epochSecond, resultXdrBytes),
            networkEnvironment,
            invoiceList = invoiceList
        )
    }

    private fun sampleInFlightTransaction(): KinTransaction {
        // https://kinexplorer.com/tx/54a11a9714ac91c0c50825322970b33383adeb3b402b958a4cfbb55a4e4087ea
        val sampleTransactionXdr =
            "AAAAALpnIKUuNzuqqbu+uUoR3rmfQI/AfSW2hMINBQlhuXgAAAAAZAAi8U0AATgmAAAAAAAAAAEAAAAGMS1raWstAAAAAAABAAAAAQAAAADamKO7GB3tpvAmhOfQZNh2H1emMhBidgTlcwz2WiHudgAAAAAAAAAAtDbTA83OgRgzLn3lwzxnbkTbYsW+gti8Rrp7CWY/jwMAAAAAAAAAAAAAAAAAAAACYbl4AAAAAEC401oiDZ71N+Ef8Ko6EiTyCAZEH0AQPaaqswUnuLllkl05UviixciecIi1fB1Z4pm0ct0IfHl+LHAje8r2VWwKWiHudgAAAEAtHw1lQ6zUOrFql/sgxngV5n9SaUWtsVNx6LdiBzRbg6fIaA7AfMlvXy0eh9iJwZfX//reNJd9LGCOKX+34tkF"
        val instant = Instant.parse("2019-12-13T16:55:45Z")

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val recordType = KinTransaction.RecordType.InFlight(instant.epochSecond)

        return StellarKinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }
}
