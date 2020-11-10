package org.kin.sdk.base.models.solana

import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.tools.Base58
import org.kin.sdk.base.tools.intToByteArray
import org.kin.sdk.base.tools.longToByteArray
import org.kin.sdk.base.tools.subByteArray
import org.kin.stellarfork.codec.Base64
import java.io.ByteArrayOutputStream

object SystemProgram {
    val PROGRAM_KEY = Key.PublicKey(ByteArray(32))

    sealed class Command(val value: Int) {
        object CreateAccount : Command(0)
        object Assign : Command(1)
        object Transfer : Command(2)
        object CreateAccountWithSeed : Command(3)
        object AdvanceNonceAccount : Command(4)
        object WithdrawNonceAccount : Command(5)
        object InitializeNonceAccount : Command(6)
        object AuthorizeNonceAccount : Command(7)
        object Allocate : Command(8)
        object AllocateWithSeed : Command(9)
        object AssignWithSeed : Command(10)
        object TransferWithSeed : Command(11)
    }

    // Reference: https://github.com/solana-labs/solana/blob/f02a78d8fff2dd7297dc6ce6eb5a68a3002f5359/sdk/src/system_instruction.rs#L58-L72
    data class CreateAccount(
        val subsidizer: Key.PublicKey,
        val address: Key.PublicKey,
        val owner: Key.PublicKey,
        val lamports: Long,
        val size: Long
    ) {
        // # Account references
        //   0. [WRITE, SIGNER] Funding account
        //   1. [WRITE, SIGNER] New account
        //
        // CreateAccount {
        //   // Number of lamports to transfer to the new account
        //   lamports: u64,
        //   // Number of bytes of memory to allocate
        //   space: u64,
        //
        //   //Address of program that will own the new account
        //   owner: Pubkey,
        // }
        //
        val instruction: Instruction by lazy {
            val data = with(ByteArrayOutputStream()) {
                write(Command.CreateAccount.value.intToByteArray().subByteArray(0, 4))
                write(lamports.longToByteArray().subByteArray(0, 8))
                write(size.longToByteArray().subByteArray(0, 8))
                write(owner.value)
                toByteArray()
            }

            Instruction.newInstruction(
                PROGRAM_KEY,
                data,
                AccountMeta.newAccountMeta(subsidizer, true),
                AccountMeta.newAccountMeta(address, true),
            )
        }
    }
}

object TokenProgram {
    // Reference: https://github.com/solana-labs/solana-program-library/blob/11b1e3eefdd4e523768d63f7c70a7aa391ea0d02/token/program/src/state.rs#L125
    val accountSize: Long = 165L

    // ProgramKey is the address of the token program that should be used.
    //
    // Current key: TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
    //
    // todo: lock this in, or make configurable.
    // TODO: should be using ServiceConfig for now...this may not be the final one, use ServiceConfig instead - USE THIS IN TESTS ONLY UNTIL FINAL!
    val PROGRAM_KEY: Key.PublicKey = Key.PublicKey(
        arrayOf(
            6, 221, 246, 225, 215, 101, 161, 147, 217, 203, 225,
            70, 206, 235, 121, 172, 28, 180, 133, 237, 95, 91, 55,
            145, 58, 140, 245, 133, 126, 255, 0, 169
        ).map { it.toByte() }.toByteArray()
    )
    val SYS_VAR_RENT = Base58.decode("SysvarRent111111111111111111111111111111111")

    sealed class Command(val value: Int) {
        object InitializeMint : Command(0)
        object InitializeAccount : Command(1)
        object InitializeMultisig : Command(2)
        object Transfer : Command(3)
        object Approve : Command(4)
        object Revoke : Command(5)
        object SetAuthority : Command(6)
        object MintTo : Command(7)
        object Burn : Command(8)
        object CloseAccount : Command(9)
        object FreezeAccount : Command(10)
        object ThawAccount : Command(11)
        object Transfer2 : Command(12)
        object Approve2 : Command(13)
        object MintTo2 : Command(14)
        object Burn2 : Command(15)
    }

    // Reference: https://github.com/solana-labs/solana-program-library/blob/b011698251981b5a12088acba18fad1d41c3719a/token/program/src/instruction.rs#L41-L55
    data class InitializeAccount(
        val account: Key.PublicKey,
        val mint: Key.PublicKey,
        val owner: Key.PublicKey,
        val programKey: Key.PublicKey
    ) {
        // Accounts expected by this instruction:
        //
        //   0. `[writable]`  The account to initialize.
        //   1. `[]` The mint this account will be associated with.
        //   2. `[]` The new account's owner/multisignature.
        //   3. `[]` Rent sysvar
        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                programKey,
                byteArrayOf(Command.InitializeAccount.value.toByte()),
                AccountMeta.newAccountMeta(account, true),
                AccountMeta.newReadonlyAccountMeta(mint, false),
                AccountMeta.newReadonlyAccountMeta(owner, false),
                AccountMeta.newReadonlyAccountMeta(Key.PublicKey(SYS_VAR_RENT), false)
            )
        }
    }

    // todo(feature): support multi-sig
    //
    // Reference: https://github.com/solana-labs/solana-program-library/blob/b011698251981b5a12088acba18fad1d41c3719a/token/program/src/instruction.rs#L76-L91
    data class Transfer(
        val source: Key.PublicKey,
        val destination: Key.PublicKey,
        val owner: Key.PublicKey,
        val amount: KinAmount,
        val programKey: Key.PublicKey
    ) {
        // Accounts expected by this instruction:
        //
        //   * Single owner/delegate
        //   0. `[writable]` The source account.
        //   1. `[writable]` The destination account.
        //   2. `[signer]` The source account's owner/delegate.
        //
        //   * Multisignature owner/delegate
        //   0. `[writable]` The source account.
        //   1. `[writable]` The destination account.
        //   2. `[]` The source account's multisignature owner/delegate.
        //   3. ..3+M `[signer]` M signer accounts.
        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                programKey,
                byteArrayOf(
                    Command.Transfer.value.toByte(),
                    *amount.toQuarks().value.longToByteArray().subByteArray(0, 8)
                ),
                AccountMeta.newAccountMeta(source, false),
                AccountMeta.newAccountMeta(destination, false),
                AccountMeta.newAccountMeta(owner, true)
            )
        }
    }

    sealed class AuthorityType(val value: Int) {
        object AuthorityTypeMintTokens : AuthorityType(0)
        object AuthorityFreezeAccount : AuthorityType(1)
        object AuthorityAccountHolder : AuthorityType(2)
        object AuthorityCloseAccount : AuthorityType(3)
    }

    data class SetAuthority(
        val account: Key.PublicKey,
        val currentAuthority: Key.PublicKey,
        val newAuthority: Key.PublicKey?,
        val authorityType: AuthorityType,
        val programKey: Key.PublicKey
    ) {
        // Sets a new authority of a mint or account.
        //
        // Accounts expected by this instruction:
        //
        //   * Single authority
        //   0. `[writable]` The mint or account to change the authority of.
        //   1. `[signer]` The current authority of the mint or account.
        //
        //   * Multisignature authority
        //   0. `[writable]` The mint or account to change the authority of.
        //   1. `[]` The mint's or account's multisignature authority.
        //   2. ..2+M `[signer]` M signer accounts
        val instruction: Instruction by lazy {
            var data =
                byteArrayOf(Command.SetAuthority.value.toByte(), authorityType.value.toByte(), 0.toByte())
            if (newAuthority != null) {
                data[2] = 1
                data += newAuthority.value
            }
            Instruction.newInstruction(
                programKey,
                data,
                AccountMeta.newAccountMeta(account, false),
                AccountMeta.newReadonlyAccountMeta(currentAuthority, true)
            )
        }
    }
}

object MemoProgram {
    // ProgramKey is the address of the memo program that should be used.
    //
    // Current key: Memo1UhkJRfHyvLMcVucJwxXeuD728EqVDDwQDxFMNo
    //
    // todo: lock this in, or make configurable
    val PROGRAM_KEY: Key.PublicKey = Key.PublicKey(
        arrayOf(
            5, 74, 83, 80, 248, 93, 200, 130, 214, 20, 165, 86, 114, 120, 138, 41, 109, 223,
            30, 171, 171, 208, 166, 6, 120, 136, 73, 50, 244, 238, 246, 160
        ).map { it.toByte() }.toByteArray()
    )

    data class Base64EncodedMemo(val base64Value: String) {
        companion object {
            fun fromBytes(bytes: ByteArray): Base64EncodedMemo = Base64EncodedMemo(Base64.encodeBase64String(bytes)!!)
        }

        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                PROGRAM_KEY,
                base64Value.toByteArray(Charsets.UTF_8)
            )
        }
    }

    data class RawMemo(val bytes: ByteArray) {
        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                PROGRAM_KEY,
                bytes
            )
        }
    }
}
