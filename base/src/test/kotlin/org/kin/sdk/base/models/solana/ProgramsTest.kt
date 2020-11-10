package org.kin.sdk.base.models.solana

import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.AppId
import org.kin.sdk.base.models.ClassicKinMemo
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.MemoSuffix
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinMemo
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.stellar.models.memo
import org.kin.sdk.base.tools.longToByteArray
import org.kin.sdk.base.tools.printHexString
import org.kin.sdk.base.tools.subByteArray
import org.kin.sdk.base.tools.toByteArray
import org.kin.sdk.base.tools.toHexString
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProgramsTest {

    @Before
    fun setUp() {
    }

    @Test
    fun testCreateAccount() {
        val keys = generateKeys(3)

        val instruction = SystemProgram.CreateAccount(
            keys[0].asPublicKey(),
            keys[1].asPublicKey(),
            keys[2].asPublicKey(),
            12345,
            67890
        ).instruction

        val command = ByteArray(4)
        val lamports = with(ByteArrayOutputStream()) {
            write(12345L.longToByteArray())
            toByteArray()
        }
        val size = with(ByteArrayOutputStream()) {
            write(67890L.longToByteArray())
            toByteArray()
        }

        assertTrue(command.contentEquals(instruction.data.subByteArray(0, 4)))
        assertTrue(lamports.contentEquals(instruction.data.subByteArray(4, 8)))
        assertTrue(size.contentEquals(instruction.data.subByteArray(12, 8)))
        assertTrue(keys[2].asPublicKey().value.contentEquals(instruction.data.subByteArray(20, 32)))

        var tx = Transaction.unmarshal(
            Transaction.newTransaction(keys[0].asPublicKey(), instruction).marshal()
        )

//        decompiled, err : = DecompileCreateAccount(tx.Message, 0)
//        require.NoError(t, err)
//        assertEquals(t, decompiled.Funder, keys[0])
//        assertEquals(t, decompiled.Address, keys[1])
//        assertEquals(t, decompiled.Owner, keys[2])
//        assertEqualsValues(t, decompiled.Lamports, 12345)
//        assertEqualsValues(t, decompiled.Size, 67890)
    }

    @Test
    fun testInitializeAccount() {
        val keys = generateKeys(3)

        val instruction = TokenProgram.InitializeAccount(
            keys[0].asPublicKey(),
            keys[1].asPublicKey(),
            keys[2].asPublicKey(),
            TokenProgram.PROGRAM_KEY
        ).instruction

        assertTrue(1.toByte().compareTo(instruction.data.first()) == 0)
        assertTrue(instruction.accounts[0].isSigner)
        assertTrue(instruction.accounts[0].isWritable)
        for (i in (1 until 4)) {
            assertFalse(instruction.accounts[i].isSigner)
            assertFalse(instruction.accounts[i].isWritable)
        }
    }

    @Test
    fun testTransfer() {
        val keys = generateKeys(3)

        val instruction = TokenProgram.Transfer(
            keys[0].asPublicKey(),
            keys[1].asPublicKey(),
            keys[2].asPublicKey(),
            QuarkAmount(123456789L).toKin(),
            TokenProgram.PROGRAM_KEY
        ).instruction

        val expectedAmount = 123456789L.longToByteArray()

        assertEquals(3.toByte(), instruction.data[0])
        assertTrue(expectedAmount.contentEquals(instruction.data.subByteArray(1, instruction.data.size-1)))

        assertFalse(instruction.accounts[0].isSigner)
        assertTrue(instruction.accounts[0].isWritable)
        assertFalse(instruction.accounts[0].isSigner)
        assertTrue(instruction.accounts[0].isWritable)

        assertTrue(instruction.accounts[2].isSigner)
        assertTrue(instruction.accounts[2].isWritable)
    }

    fun generateKeys(amount: Int): List<Key.PrivateKey> {
        val keys = mutableListOf<Key.PrivateKey>()
        for (i in 0..amount) {
            keys.add(Key.PrivateKey.random())
        }
        return keys
    }

    @Test
    fun testSetAuthority() {
        val keys = generateKeys(3)

        val instruction = TokenProgram.SetAuthority(
            keys[0].asPublicKey(),
            keys[1].asPublicKey(),
            keys[2].asPublicKey(),
            TokenProgram.AuthorityType.AuthorityCloseAccount,
            TokenProgram.PROGRAM_KEY
        ).instruction

        assertEquals(6, instruction.data[0])
        assertEquals(TokenProgram.AuthorityType.AuthorityCloseAccount.value.toByte(), instruction.data[1])

        assertFalse(instruction.accounts[0].isSigner)
        assertTrue(instruction.accounts[0].isWritable)

        assertTrue(instruction.accounts[1].isSigner)
        assertFalse(instruction.accounts[1].isWritable)
    }

    @Test
    fun testSetAuthority_NoNewAuthority() {
        val keys = generateKeys(3)

        val instruction = TokenProgram.SetAuthority(
            keys[0].asPublicKey(),
            keys[1].asPublicKey(),
            null,
            TokenProgram.AuthorityType.AuthorityCloseAccount,
            TokenProgram.PROGRAM_KEY
        ).instruction

        assertEquals(
            byteArrayOf(6.toByte(), TokenProgram.AuthorityType.AuthorityCloseAccount.value.toByte(), 0.toByte()).toHexString(),
            instruction.data.toHexString()
        )

        assertFalse(instruction.accounts[0].isSigner)
        assertTrue(instruction.accounts[0].isWritable)

        assertTrue(instruction.accounts[1].isSigner)
        assertFalse(instruction.accounts[1].isWritable)
    }

    @Test
    fun memoTests() {
        val keys = generateKeys(3)

        val textMemo = ClassicKinMemo(1, AppId("kek"), MemoSuffix("suffix"))
        val binaryMemo = KinBinaryMemo.Builder(10, 1, 2)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray())
            .build()


        val instructionWithTextMemo = MemoProgram.RawMemo(textMemo.asKinMemo().rawValue).instruction
        val instructionWithBinaryMemo = MemoProgram.Base64EncodedMemo.fromBytes(binaryMemo.toKinMemo().rawValue).instruction

        val txTextMemo = Transaction.newTransaction(
            keys[0].asPublicKey(),
            instructionWithTextMemo
        )

        assertEquals("1-kek-suffix", txTextMemo.memo.toString())

        val txBinaryMemo = Transaction.newTransaction(
            keys[0].asPublicKey(),
            instructionWithBinaryMemo
        )

        assertEquals(binaryMemo, txBinaryMemo.memo.getAgoraMemo())
    }

    @Test
    fun commandValues() {

        // SystemProgram
        assertEquals(0, SystemProgram.Command.CreateAccount.value)
        assertEquals(1, SystemProgram.Command.Assign.value)
        assertEquals(2, SystemProgram.Command.Transfer.value)
        assertEquals(3, SystemProgram.Command.CreateAccountWithSeed.value)
        assertEquals(4, SystemProgram.Command.AdvanceNonceAccount.value)
        assertEquals(5, SystemProgram.Command.WithdrawNonceAccount.value)
        assertEquals(6, SystemProgram.Command.InitializeNonceAccount.value)
        assertEquals(7, SystemProgram.Command.AuthorizeNonceAccount.value)
        assertEquals(8, SystemProgram.Command.Allocate.value)
        assertEquals(9, SystemProgram.Command.AllocateWithSeed.value)
        assertEquals(10, SystemProgram.Command.AssignWithSeed.value)
        assertEquals(11, SystemProgram.Command.TransferWithSeed.value)

        // TokenProgram
        assertEquals(0, TokenProgram.Command.InitializeMint.value)
        assertEquals(1, TokenProgram.Command.InitializeAccount.value)
        assertEquals(2, TokenProgram.Command.InitializeMultisig.value)
        assertEquals(3, TokenProgram.Command.Transfer.value)
        assertEquals(4, TokenProgram.Command.Approve.value)
        assertEquals(5, TokenProgram.Command.Revoke.value)
        assertEquals(6, TokenProgram.Command.SetAuthority.value)
        assertEquals(7, TokenProgram.Command.MintTo.value)
        assertEquals(8, TokenProgram.Command.Burn.value)
        assertEquals(9, TokenProgram.Command.CloseAccount.value)
        assertEquals(10, TokenProgram.Command.FreezeAccount.value)
        assertEquals(11, TokenProgram.Command.ThawAccount.value)
        assertEquals(12, TokenProgram.Command.Transfer2.value)
        assertEquals(13, TokenProgram.Command.Approve2.value)
        assertEquals(14, TokenProgram.Command.MintTo2.value)
        assertEquals(15, TokenProgram.Command.Burn2.value)

        // TokenProgram AuthorityType
        assertEquals(0, TokenProgram.AuthorityType.AuthorityTypeMintTokens.value)
        assertEquals(1, TokenProgram.AuthorityType.AuthorityFreezeAccount.value)
        assertEquals(2, TokenProgram.AuthorityType.AuthorityAccountHolder.value)
        assertEquals(3, TokenProgram.AuthorityType.AuthorityCloseAccount.value)

    }
}
