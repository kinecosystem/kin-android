package org.kin.sdk.base.models.solana

import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.tools.printHexString
import org.kin.sdk.base.tools.subByteArray
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.codec.Hex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionTest {

    companion object {
        // Taken from: https://github.com/solana-labs/solana/blob/14339dec0a960e8161d1165b6a8e5cfb73e78f23/sdk/src/transaction.rs#L523
        val rustGenerated = "AUc7Cbu+gZalFSGeSFdukHhP7oSGaSdmdNEd5ZokaSysdoMWfI" +
                "OzjrAbdaBZZuDMAfyNAogAJdrhgVya+jthsgoBAAEDnON0wdcmjhYIDuXvd10F2qEjA" +
                "yEAJGSe/CGhYbk+WWMBAQEEBQYHCAkJCQkJCQkJCQkJCQkJCQkIBwYFBAEBAQICAgQF" +
                "BgcICQEBAQEBAQEBAQEBAQEBCQgHBgUEAgICAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAABAgIAAQMBAgM="

        // The above example does not have the correct public key encoded in the keypair.
        // This is the above example with the correctly generated keypair.
        val rustGeneratedAdjusted =
            "ATMfBMZ8phHEheLph8K9TJhRKhnE4qNZvWiXdUdJRmlTCRsQjWmW2CkQJeRHBCcsqFm" +
                    "2gynjL40M9mTe0Dxp4QIBAAEDfEya6wnC7f3Cv53qnOEywwIJ928rIdqAlfXYI1adXroBAQEEBQYHCA" +
                    "kJCQkJCQkJCQkJCQkJCQkIBwYFBAEBAQICAgQFBgcICQEBAQEBAQEBAQEBAQEBCQgHBgUEAgICAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAgIAAQMBAgM"
    }

    @Test
    fun TestTransaction_CrossImpl() {
        val signerKeyBytes = arrayOf(
            48, 83, 2, 1, 1, 48, 5, 6, 3, 43, 101, 112, 4, 34, 4, 32, 255, 101, 36, 24, 124, 23,
            167, 21, 132, 204, 155, 5, 185, 58, 121, 75, 156, 227, 116, 193, 215, 38, 142, 22, 8,
            14, 229, 239, 119, 93, 5, 218, 161, 35, 3, 33, 0, 36, 100, 158, 252, 33, 161, 97, 185,
            62, 89, 99
        ).map { it.toByte() }.toByteArray()
        val keypair = KeyPair.fromSecretSeed(
            signerKeyBytes.subByteArray(0, 32)
        )

        val programID = Key.PublicKey(
            arrayOf(
                2, 2, 2, 4, 5, 6, 7, 8, 9, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 8, 7, 6, 5, 4, 2, 2, 2
            ).map { it.toByte() }.toByteArray()
        )

        val to = Key.PublicKey(
            arrayOf(
                1, 1, 1, 4, 5, 6, 7, 8, 9, 9, 9, 9,
                9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 7, 6, 5, 4, 1, 1, 1
            ).map { it.toByte() }.toByteArray()
        )

        val data = arrayOf(1, 2, 3).map { it.toByte() }.toByteArray()

        val tx = Transaction.newTransaction(
            keypair.asPublicKey(),
            Instruction.newInstruction(
                programID,
                data,
                AccountMeta.newAccountMeta(keypair.asPublicKey(), true),
                AccountMeta.newAccountMeta(to, false),
            ),
        )

        keypair.publicKey.printHexString("keypair.publicKey:")
        programID.value.printHexString("programID.publicKey:")
        to.value.printHexString("to.publicKey:")
        data.printHexString("data:")

        signerKeyBytes.subByteArray(32, 32).printHexString()
        KeyPair.fromSecretSeed(
            signerKeyBytes.subByteArray(0, 32)
        ).publicKey.printHexString()

        println("rustMessageOnly:\n 010001039ce374c1d7268e16080ee5ef775d05daa12303210024649efc21a161b93e5963010101040506070809090909090909090909090909090909080706050401010102020204050607080901010101010101010101010101010908070605040202020000000000000000000000000000000000000000000000000000000000000000010202000103010203")
        tx.marshal().printHexString("unsignedTransaction:\n")

        val signedTransaction = tx.copyAndSign(keypair.asPrivateKey())
        val rustGeneratedBytes = Base64.decodeBase64(rustGeneratedAdjusted)!!

        rustGeneratedBytes.printHexString("rustGeneratedBytes:\n")
        signedTransaction.marshal().printHexString("signedTransaction:\n")

        assertEquals(
            Hex.encodeHexString(rustGeneratedBytes),
            Hex.encodeHexString(signedTransaction.marshal())
        )
    }

    @Test(expected = RuntimeException::class)
    fun TestTransaction_InvalidAccounts() {
        val keys = generateKeys(2)
        var tx = Transaction.newTransaction(
            keys[0].asPublicKey(),
            Instruction.newInstruction(
                keys[1].asPublicKey(),
                ByteArray(0),
                AccountMeta.newAccountMeta(keys[0].asPublicKey(), true, isProgram = true),
            )
        )
        tx = with(tx) {
            copy(
                message = with(this.message) {
                    copy(
                        instructions = instructions.toMutableList().apply {
                            this[0] = this[0].copy(programIndex = 2)
                        }
                    )
                }
            )
        }
        val marshledTx = tx.marshal()
        val unmarshaledTx = Transaction.unmarshal(marshledTx) // Should fail
    }

    @Test
    fun TestTransaction_SingleInstruction() {
        var keys = generateKeys(2)
        val payer = keys[0]
        val program = keys[1]

        keys = generateKeys(4)
        val data = byteArrayOf(1, 2, 3)

        var tx = Transaction.newTransaction(
            payer.asPublicKey(),
            Instruction.newInstruction(
                program.asPublicKey(),
                data,
                AccountMeta.newReadonlyAccountMeta(keys[0].asPublicKey(), true),
                AccountMeta.newReadonlyAccountMeta(keys[1].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[2].asPublicKey(), false),
                AccountMeta.newAccountMeta(
                    keys[3].asPublicKey(), true
                ),
            )
        )

        // Intentionally sign out of order to ensure ordering is fixed.
        tx = tx.copyAndSign(keys[0], keys[3], payer)

        assertEquals(tx.signatures.size, 3)
        assertEquals(tx.message.accounts.size, 6)
        assertEquals(3, tx.message.header.numSignatures)
        assertEquals(1, tx.message.header.numReadOnlySigned)
        assertEquals(2, tx.message.header.numReadOnly)

        val message = tx.message.marshal()

        assertTrue(payer.asPublicKey().verify(message, tx.signatures[0].value.byteArray))
        assertTrue(keys[3].asPublicKey().verify(message, tx.signatures[1].value.byteArray))
        assertTrue(keys[0].asPublicKey().verify(message, tx.signatures[2].value.byteArray))

        assertEquals(payer.asPublicKey(), tx.message.accounts[0])
        assertEquals(keys[3].asPublicKey(), tx.message.accounts[1])
        assertEquals(keys[0].asPublicKey(), tx.message.accounts[2])
        assertEquals(keys[2].asPublicKey(), tx.message.accounts[3])
        assertEquals(keys[1].asPublicKey(), tx.message.accounts[4])
        assertEquals(program.asPublicKey(), tx.message.accounts[5])

        assertEquals(5.toByte(), tx.message.instructions[0].programIndex)
        assertEquals(data, tx.message.instructions[0].data)

        tx.message.instructions[0].accounts.printHexString()
        assertTrue(
            arrayOf(2, 4, 3, 1).map { it.toByte() }.toByteArray().contentEquals(
                tx.message.instructions[0].accounts
            )
        )
    }

    @Test
    fun TestTransaction_DuplicateKeys() {
        var keys = generateKeys(2)
        val payer = keys[0]
        val program = keys[1]

        keys = generateKeys(4)
        val data = arrayOf(1, 2, 3).map { it.toByte() }.toByteArray()

        // Key[0]: ReadOnlySigner -> WritableSigner
        // Key[1]: ReadOnly       -> ReadOnlySigner
        // Key[2]: Writable       -> Writable       (ReadOnly,noop)
        // Key[3]: WritableSigner -> WritableSignera (ReadOnly,noop)

        var tx = Transaction.newTransaction(
            payer.asPublicKey(),
            Instruction.newInstruction(
                program.asPublicKey(),
                data,
                AccountMeta.newReadonlyAccountMeta(keys[0].asPublicKey(), true),
                AccountMeta.newReadonlyAccountMeta(keys[1].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[2].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[3].asPublicKey(), true),
                // Upgrade keys [0] and [1]
                AccountMeta.newAccountMeta(keys[0].asPublicKey(), false),
                AccountMeta.newReadonlyAccountMeta(keys[1].asPublicKey(), true),
                // 'Downgrade' keys [2] and [3] (noop)
                AccountMeta.newReadonlyAccountMeta(keys[2].asPublicKey(), false),
                AccountMeta.newReadonlyAccountMeta(keys[3].asPublicKey(), false),
            ),
        )

        // Intentionally sign out of order to ensure ordering is fixed.
        tx = tx.copyAndSign(keys[0], keys[1], keys[3], payer)

        assertEquals(tx.signatures.size, 4)
        assertEquals(tx.message.accounts.size, 6)
        assertEquals(4, tx.message.header.numSignatures)
        assertEquals(1, tx.message.header.numReadOnlySigned)
        assertEquals(1, tx.message.header.numReadOnly)

        val message = tx.message.marshal()

        assertTrue(payer.asPublicKey().verify(message, tx.signatures[0].value.byteArray))
        assertTrue(keys[3].asPublicKey().verify(message, tx.signatures[1].value.byteArray))
        assertTrue(keys[0].asPublicKey().verify(message, tx.signatures[2].value.byteArray))
        assertTrue(keys[1].asPublicKey().verify(message, tx.signatures[3].value.byteArray))

        assertEquals(payer.asPublicKey(), tx.message.accounts[0])
        assertEquals(keys[3].asPublicKey(), tx.message.accounts[1])
        assertEquals(keys[0].asPublicKey(), tx.message.accounts[2])
        assertEquals(keys[1].asPublicKey(), tx.message.accounts[3])
        assertEquals(keys[2].asPublicKey(), tx.message.accounts[4])
        assertEquals(program.asPublicKey(), tx.message.accounts[5])

        assertEquals(5.toByte(), tx.message.instructions[0].programIndex)
        assertEquals(data, tx.message.instructions[0].data)
        println(tx.message.instructions[0].accounts.map { it.toInt() })
        assertTrue(
            arrayOf(2, 3, 4, 1, 2, 3, 4, 1).map { it.toByte() }.toByteArray()
                .contentEquals(
                    tx.message.instructions[0].accounts
                )
        )
    }

    @Test
    fun TestTransaction_MultiInstruction() {
        var keys = generateKeys(3)
        val payer = keys[0]
        val program = keys[1]
        val program2 = keys[2]

        keys = generateKeys(6)

        println("payer: ${payer.asPublicKey()}")
        println("program: ${program.asPublicKey()}")
        println("program2: ${program2.asPublicKey()}")
        println("keys[0]: ${keys[0].asPublicKey()}")
        println("keys[1]: ${keys[1].asPublicKey()}")
        println("keys[2]: ${keys[2].asPublicKey()}")
        println("keys[3]: ${keys[3].asPublicKey()}")
        println("keys[4]: ${keys[4].asPublicKey()}")
        println("keys[5]: ${keys[5].asPublicKey()}")

        val data = arrayOf(1, 2, 3).map { it.toByte() }.toByteArray()
        val data2 = arrayOf(3, 4, 5).map { it.toByte() }.toByteArray()

        // Key[0]: ReadOnlySigner -> WritableSigner
        // Key[1]: ReadOnly       -> WritableSigner
        // Key[2]: Writable       -> Writable       (ReadOnly,noop)
        // Key[3]: WritableSigner -> WritableSigner (ReadOnly,noop)
        // Key[4]: n/a            -> WritableSigner
        // Key[5]: n/a            -> ReadOnly

        var tx = Transaction.newTransaction(
            payer.asPublicKey(),
            Instruction.newInstruction(
                program.asPublicKey(),
                data,
                AccountMeta.newReadonlyAccountMeta(keys[0].asPublicKey(), true),
                AccountMeta.newReadonlyAccountMeta(keys[1].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[2].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[3].asPublicKey(), true),
            ),
            Instruction.newInstruction(
                program2.asPublicKey(),
                data2,
                // Ensure that keys don't get downgraded in permissions
                AccountMeta.newReadonlyAccountMeta(keys[3].asPublicKey(), false),
                AccountMeta.newReadonlyAccountMeta(keys[2].asPublicKey(), false),
                // Ensure we can upgrade upgrading works
                AccountMeta.newAccountMeta(keys[0].asPublicKey(), false),
                AccountMeta.newAccountMeta(keys[1].asPublicKey(), true),
                // Ensure accounts get added
                AccountMeta.newAccountMeta(keys[4].asPublicKey(), true),
                AccountMeta.newReadonlyAccountMeta(keys[5].asPublicKey(), false),
            ),
        )

        println(tx)

        tx = tx.copyAndSign(payer, keys[0], keys[1], keys[3], keys[4])

        assertEquals(tx.signatures.size, 5)
        assertEquals(tx.message.accounts.size, 9)

        assertEquals(5, tx.message.header.numSignatures)
        assertEquals(0, tx.message.header.numReadOnlySigned)
        assertEquals(3, tx.message.header.numReadOnly)

        val message = tx.message.marshal()

        assertTrue(payer.asPublicKey().verify(message, tx.signatures[0].value.byteArray))
        assertTrue(keys[4].asPublicKey().verify(message, tx.signatures[1].value.byteArray))
        assertTrue(keys[0].asPublicKey().verify(message, tx.signatures[2].value.byteArray))
        assertTrue(keys[1].asPublicKey().verify(message, tx.signatures[3].value.byteArray))
        assertTrue(keys[3].asPublicKey().verify(message, tx.signatures[4].value.byteArray))

        assertEquals(payer.asPublicKey(), tx.message.accounts[0])
        assertEquals(keys[4].asPublicKey(), tx.message.accounts[1])
        assertEquals(keys[0].asPublicKey(), tx.message.accounts[2])
        assertEquals(keys[1].asPublicKey(), tx.message.accounts[3])
        assertEquals(keys[3].asPublicKey(), tx.message.accounts[4])
        assertEquals(keys[2].asPublicKey(), tx.message.accounts[5])
        assertEquals(keys[5].asPublicKey(), tx.message.accounts[6])
        assertEquals(program2.asPublicKey(), tx.message.accounts[7])
        assertEquals(program.asPublicKey(), tx.message.accounts[8])

        assertEquals(8.toByte(), tx.message.instructions[0].programIndex)
        assertEquals(data, tx.message.instructions[0].data)
        assertTrue(
            arrayOf(2, 3, 5, 4).map { it.toByte() }.toByteArray()
                .contentEquals(
                    tx.message.instructions[0].accounts
                )
        )

        assertEquals(7.toByte(), tx.message.instructions[1].programIndex)
        assertEquals(data2, tx.message.instructions[1].data)
        assertTrue(
            arrayOf(4, 5, 2, 3, 1, 6).map { it.toByte() }.toByteArray()
                .contentEquals(
            tx.message.instructions[1].accounts)
        )
    }

    fun generateKeys(amount: Int): List<Key.PrivateKey> {
        val keys = mutableListOf<Key.PrivateKey>()
        for (i in 0..amount) {
            keys.add(Key.PrivateKey.random())
        }
        return keys
    }
}
