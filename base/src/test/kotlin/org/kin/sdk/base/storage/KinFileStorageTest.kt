package org.kin.sdk.base.storage

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import org.kin.stellarfork.KeyPair
import java.time.Instant
import java.util.Base64
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class KinFileStorageTest {

    companion object {
        val networkEnvironment = NetworkEnvironment.KinStellarTestNet
    }
    lateinit var sut: Storage

    @Rule
    @JvmField
    public var tempFolder = TemporaryFolder()

    @Before
    fun setUp() {
        sut = KinFileStorage(tempFolder.root.invariantSeparatorsPath, NetworkEnvironment.KinStellarTestNet, ExecutorServices())
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
            assertEquals(value, listOf(historicalTransaction, historicalTransaction2, inFlightTransaction))
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
            assertEquals(value, listOf(inFlightTransaction, historicalTransaction, historicalTransaction2))
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
            newAccount.merge(KinAccount(key = newAccount.key, balance = KinBalance(KinAmount(1234L))))

        sut.addAccount(newAccount)

        sut.updateAccountInStorage(updatedAccountWithNewBalance).test {
            assertEquals(value, updatedAccountWithNewBalance)
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
    fun testDeleteAllStorage() {
        val newAccount: KinAccount = TestUtils.newKinAccount()

        sut.addAccount(newAccount)
        sut.deleteAllStorage(newAccount.id).test {
            assertTrue(value!!)
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

        return KinTransaction(transactionXdrBytes, recordType, networkEnvironment)
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

        return KinTransaction(transactionXdrBytes, recordType, networkEnvironment)
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

        return KinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }

    private fun sampleInFlightTransaction(): KinTransaction {
        // https://kinexplorer.com/tx/54a11a9714ac91c0c50825322970b33383adeb3b402b958a4cfbb55a4e4087ea
        val sampleTransactionXdr =
            "AAAAALpnIKUuNzuqqbu+uUoR3rmfQI/AfSW2hMINBQlhuXgAAAAAZAAi8U0AATgmAAAAAAAAAAEAAAAGMS1raWstAAAAAAABAAAAAQAAAADamKO7GB3tpvAmhOfQZNh2H1emMhBidgTlcwz2WiHudgAAAAAAAAAAtDbTA83OgRgzLn3lwzxnbkTbYsW+gti8Rrp7CWY/jwMAAAAAAAAAAAAAAAAAAAACYbl4AAAAAEC401oiDZ71N+Ef8Ko6EiTyCAZEH0AQPaaqswUnuLllkl05UviixciecIi1fB1Z4pm0ct0IfHl+LHAje8r2VWwKWiHudgAAAEAtHw1lQ6zUOrFql/sgxngV5n9SaUWtsVNx6LdiBzRbg6fIaA7AfMlvXy0eh9iJwZfX//reNJd9LGCOKX+34tkF"
        val instant = Instant.parse("2019-12-13T16:55:45Z")

        val transactionXdrBytes = Base64.getDecoder().decode(sampleTransactionXdr)
        val recordType = KinTransaction.RecordType.InFlight(instant.epochSecond)

        return KinTransaction(transactionXdrBytes, recordType, networkEnvironment)
    }
}
