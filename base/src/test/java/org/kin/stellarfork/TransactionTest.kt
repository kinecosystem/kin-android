package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.KeyPair.Companion.fromAccountId
import org.kin.stellarfork.KeyPair.Companion.fromSecretSeed
import org.kin.stellarfork.Memo.Companion.none
import org.kin.stellarfork.Memo.Companion.text
import org.kin.stellarfork.Network.Companion.publicNetwork
import org.kin.stellarfork.Network.Companion.testNetwork
import org.kin.stellarfork.Transaction.Companion.fromEnvelopeXdr
import org.kin.stellarfork.codec.Base64.Companion.decodeBase64
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.SecureRandom
import java.util.Arrays

class TransactionTest {
    @Test
    @Throws(FormatException::class)
    fun testBuilderSuccessTestnet() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val sequenceNumber = 2908908335136768L
        val account =
            Account(source, sequenceNumber)
        val transaction = Transaction.Builder(
            account,
            testNetwork
        )
            .addOperation(
                CreateAccountOperation.Builder(
                    destination,
                    "2000"
                ).build()
            )
            .addFee(100)
            .build()
        transaction.sign(source)
        Assert.assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAZAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAAC+vCAAAAAAAAAAABtoeVYgAAAEDJH/+1omziHrtLcD1EBaLMgVpvjg8d+ivw5jNwtzJVyILNJ29LWee7bJalNPrV3awMG68aCqbrOfPKf1SSHxkE",
            transaction.toEnvelopeXdrBase64()
        )
        Assert.assertEquals(transaction.sourceAccount, source)
        Assert.assertEquals(transaction.sequenceNumber, sequenceNumber + 1)
        Assert.assertEquals(transaction.fee.toLong(), 100)
        val transaction2 =
            fromEnvelopeXdr(
                transaction.toEnvelopeXdr(),
                testNetwork
            )
        Assert.assertEquals(
            transaction.sourceAccount.accountId,
            transaction2.sourceAccount.accountId
        )
        Assert.assertEquals(transaction.sequenceNumber, transaction2.sequenceNumber)
        Assert.assertEquals(transaction.fee.toLong(), transaction2.fee.toLong())
        Assert.assertEquals(
            (transaction.operations[0] as CreateAccountOperation).startingBalance,
            (transaction2.operations[0] as CreateAccountOperation).startingBalance
        )
        Assert.assertEquals(transaction.signatures, transaction2.signatures)
    }

    @Test
    @Throws(FormatException::class)
    fun testBuilderMemoText() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val account =
            Account(source, 2908908335136768L)
        val transaction = Transaction.Builder(
            account,
            testNetwork
        )
            .addOperation(
                CreateAccountOperation.Builder(
                    destination,
                    "2000"
                ).build()
            )
            .addMemo(text("Hello world!"))
            .build()
        transaction.sign(source)
        Assert.assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAAAAKVaMAAAABAAAAAAAAAAEAAAAMSGVsbG8gd29ybGQhAAAAAQAAAAAAAAAAAAAAAO3gUmG83C+VCqO6FztuMtXJF/l7grZA7MjRzqdZ9W8QAAAAAAvrwgAAAAAAAAAAAbaHlWIAAABATTu413J3cPYDDRQjjShfvqYzfR+KT9nhgktZeDkHjWz2T3TkTXDO893bRfEdpO/ltSvKxdnD6IjQsVjXTaJbAw==",
            transaction.toEnvelopeXdrBase64()
        )
        val transaction2 =
            fromEnvelopeXdr(
                transaction.toEnvelopeXdr(),
                testNetwork
            )
        Assert.assertEquals(
            transaction.sourceAccount.accountId,
            transaction2.sourceAccount.accountId
        )
        Assert.assertEquals(transaction.sequenceNumber, transaction2.sequenceNumber)
        Assert.assertEquals(transaction.memo, transaction2.memo)
        Assert.assertEquals(transaction.fee.toLong(), transaction2.fee.toLong())
        Assert.assertEquals(
            (transaction.operations[0] as CreateAccountOperation).startingBalance,
            (transaction2.operations[0] as CreateAccountOperation).startingBalance
        )
    }

    @Test
    @Throws(FormatException::class, IOException::class)
    fun testBuilderTimeBounds() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val account =
            Account(source, 2908908335136768L)
        val transaction = Transaction.Builder(
            account,
            testNetwork
        )
            .addOperation(
                CreateAccountOperation.Builder(
                    destination,
                    "2000"
                ).build()
            )
            .addTimeBounds(TimeBounds(42, 1337))
            .build()
        transaction.sign(source)
        // Convert transaction to binary XDR and back again to make sure timebounds are correctly de/serialized.
        val `is` = XdrDataInputStream(
            ByteArrayInputStream(decodeBase64(transaction.toEnvelopeXdrBase64().toByteArray()))
        )
        val decodedTransaction =
            org.kin.stellarfork.xdr.Transaction.decode(`is`)
        Assert.assertEquals(decodedTransaction.timeBounds!!.minTime!!.uint64!!.toLong(), 42)
        Assert.assertEquals(decodedTransaction.timeBounds!!.maxTime!!.uint64!!.toLong(), 1337)
        val transaction2 =
            fromEnvelopeXdr(
                transaction.toEnvelopeXdr(),
                testNetwork
            )
        Assert.assertEquals(
            transaction.sourceAccount.accountId,
            transaction2.sourceAccount.accountId
        )
        Assert.assertEquals(transaction.sequenceNumber, transaction2.sequenceNumber)
        Assert.assertEquals(transaction.timeBounds, transaction2.timeBounds)
        Assert.assertEquals(transaction.fee.toLong(), transaction2.fee.toLong())
        Assert.assertEquals(
            (transaction.operations[0] as CreateAccountOperation).startingBalance,
            (transaction2.operations[0] as CreateAccountOperation).startingBalance
        )
    }

    @Test
    @Throws(FormatException::class)
    fun testBuilderSuccessPublic() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val account =
            Account(source, 2908908335136768L)
        val transaction = Transaction.Builder(
            account,
            publicNetwork
        )
            .addOperation(
                CreateAccountOperation.Builder(
                    destination,
                    "2000"
                ).build()
            )
            .build()
        transaction.sign(source)
        Assert.assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAAAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAAC+vCAAAAAAAAAAABtoeVYgAAAED75/T95Vj+Z7UjuEqqS1FD1IVpfu4X8BvBZ3aYksT6gi+spMP+vygzaG3a6wSUX4wLMa8rULIgmt5OlH/T2hMC",
            transaction.toEnvelopeXdrBase64()
        )
    }

    @Test
    @Throws(FormatException::class)
    fun testSha256HashSigning() {
        val source =
            fromAccountId("GBBM6BKZPEHWYO3E3YKREDPQXMS4VK35YLNU7NFBRI26RAN7GI5POFBB")
        val destination =
            fromAccountId("GDJJRRMBK4IWLEPJGIE6SXD2LP7REGZODU7WDC3I2D6MR37F4XSHBKX2")
        val account = Account(source, 0L)
        val transaction = Transaction.Builder(
            account,
            publicNetwork
        )
            .addOperation(
                PaymentOperation.Builder(
                    destination,
                    AssetTypeNative,
                    "2000"
                ).build()
            )
            .build()
        val preimage = ByteArray(64)
        SecureRandom().nextBytes(preimage)
        val hash = Util.hash(preimage)
        transaction.sign(preimage)
        Assert.assertTrue(
            Arrays.equals(
                transaction.signatures[0].signature!!.signature,
                preimage
            )
        )
        Assert.assertTrue(
            Arrays.equals(
                transaction.signatures[0].hint!!.signatureHint,
                Arrays.copyOfRange(hash, hash.size - 4, hash.size)
            )
        )
    }

    @Test
    @Throws(FormatException::class, IOException::class)
    fun testToBase64EnvelopeXdrBuilderNoSignatures() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val account =
            Account(source, 2908908335136768L)
        val transaction = Transaction.Builder(
            account,
            testNetwork
        )
            .addOperation(
                CreateAccountOperation.Builder(
                    destination,
                    "2000"
                ).build()
            )
            .build()
        try {
            transaction.toEnvelopeXdrBase64()
            Assert.fail()
        } catch (exception: RuntimeException) {
            Assert.assertTrue(exception.message!!.contains("Transaction must be signed by at least one signer."))
        }
    }

    @Test
    @Throws(FormatException::class, IOException::class)
    fun testNoOperations() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val account =
            Account(source, 2908908335136768L)
        try {
            Transaction.Builder(
                account,
                testNetwork
            ).build()
            Assert.fail()
        } catch (exception: RuntimeException) {
            Assert.assertTrue(exception.message!!.contains("At least one operation required"))
            Assert.assertEquals(2908908335136768L, account.sequenceNumber)
        }
    }

    @Test
    @Throws(FormatException::class, IOException::class)
    fun testTryingToAddMemoTwice() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        try {
            val account =
                Account(source, 2908908335136768L)
            Transaction.Builder(
                account,
                testNetwork
            )
                .addOperation(
                    CreateAccountOperation.Builder(
                        destination,
                        "2000"
                    ).build()
                )
                .addMemo(none())
                .addMemo(none())
            Assert.fail()
        } catch (exception: RuntimeException) {
            Assert.assertTrue(exception.message!!.contains("Memo has been already added."))
        }
    }
}
