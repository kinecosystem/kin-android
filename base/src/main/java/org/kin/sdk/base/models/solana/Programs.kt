package org.kin.sdk.base.models.solana

import net.i2p.crypto.eddsa.math.GroupElement
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.tools.Base58
import org.kin.sdk.base.tools.intToByteArray
import org.kin.sdk.base.tools.longToByteArray
import org.kin.sdk.base.tools.subByteArray
import org.kin.stellarfork.codec.Base64
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.math.max

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

val SYS_VAR_RENT_KEY = Key.PublicKey(Base58.decode("SysvarRent111111111111111111111111111111111"))


object Address {

    object ErrTooManySeeds: Exception("too many seeds")
    object ErrMaxSeedLengthExceeded : Exception("max seed length exceeded")
    object ErrInvalidPublicKey: Exception("invalid public key")

    private val maxSeeds = 16
    private val maxSeedLength = 32

    // CreateProgramAddress mirrors the implementation of the Solana SDK's CreateProgramAddress.
    //
    // ProgramAddresses are public keys that _do not_ lie on the ed25519 curve to ensure that
    // there is no associated private key. In the event that the program and seed parameters
    // result in a valid public key, ErrInvalidPublicKey is returned.
    //
    // Reference: https://github.com/solana-labs/solana/blob/5548e599fe4920b71766e0ad1d121755ce9c63d5/sdk/program/src/pubkey.rs#L158
    fun createProgramAddress(program: Key.PublicKey, vararg seeds: ByteArray) : Key.PublicKey? {
        if (seeds.size > maxSeeds) {
            throw ErrTooManySeeds
        }

        val h =  MessageDigest.getInstance("SHA-256")
        for (s in seeds) {
            if (s.size > maxSeedLength) {
                throw ErrMaxSeedLengthExceeded
            }
            try {
                h.update(s)
            } catch (t: Throwable) {
                throw Exception("failed to hash seed", t)
            }
        }

        for (v in arrayOf(program.value, "ProgramDerivedAddress".toByteArray())) {
            try {
                h.update(v)
            } catch (t: Throwable) {
                throw Exception("failed to hash seed", t)
            }
        }

        val pub = h.digest()

        // Following the Solana SDK, we want to _reject_ the generated public key
        // if it's a valid compressed EdwardsPoint.
        //
        // Reference: https://github.com/solana-labs/solana/blob/5548e599fe4920b71766e0ad1d121755ce9c63d5/sdk/program/src/pubkey.rs#L182-L187
        try {
            val a = GroupElement(EdDSANamedCurveTable.ED_25519_CURVE_SPEC.curve, pub)
        } catch (t: Throwable) {
            return Key.PublicKey(pub)
        }
        throw ErrInvalidPublicKey
    }

    // FindProgramAddress mirrors the implementation of the Solana SDK's FindProgramAddress. Its primary
    // use case (for Kin and Agora) is for deriving associated accounts.
    //
    // Reference: https://github.com/solana-labs/solana/blob/5548e599fe4920b71766e0ad1d121755ce9c63d5/sdk/program/src/pubkey.rs#L234
    fun findProgramAddress(program: Key.PublicKey, vararg seeds: ByteArray) : Key.PublicKey? {
        val maxUint8  = (1 shl 8) - 1 // consistent with go impl
        val bumpSeed = byteArrayOf(maxUint8.toByte())

        for(i in 0 until maxUint8) {
            try {
                return createProgramAddress(
                    program,
                    *seeds.toMutableList()
                        .apply { add(bumpSeed) }
                        .toTypedArray()
                )
            } catch (e: ErrInvalidPublicKey) {
                bumpSeed[0]--
            }
        }
        return null
    }
}

object AssociatedTokenProgram {
    val PROGRAM_KEY: Key.PublicKey = Key.PublicKey(
        arrayOf(
            140, 151, 37, 143, 78, 36, 137, 241, 187, 61, 16, 41, 20, 142,
            13, 131, 11, 90, 19, 153, 218, 255, 16, 132, 4, 142, 123, 216,
            219, 233, 248, 89
        ).map { it.toByte() }.toByteArray()
    )

    // GetAssociatedAccount returns the associated account address for an SPL token.
    //
    // Reference: https://spl.solana.com/associated-token-account#finding-the-associated-token-account-address
    fun getAssociatedAccount(wallet: Key.PublicKey, mint: Key.PublicKey) : Key.PublicKey? {
        return Address.findProgramAddress(
            PROGRAM_KEY,
            wallet.value,
            TokenProgram.PROGRAM_KEY.value,
            mint.value,
        )
    }

    // Reference: https://github.com/solana-labs/solana-program-library/blob/0639953c7dd0f5228c3ceda3ba68fece3b46ff1d/associated-token-account/program/src/lib.rs#L54
    data class CreateAssociatedTokenAccount(
        val subsidizer: Key.PublicKey,
        val wallet: Key.PublicKey,
        val mint: Key.PublicKey
    ) {
        val addr = getAssociatedAccount(wallet, mint) ?: throw Exception("can't derive an associated account")

        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                PROGRAM_KEY,
                byteArrayOf(),
                AccountMeta.newAccountMeta(subsidizer, true),
                AccountMeta.newAccountMeta(addr, false),
                AccountMeta.newReadonlyAccountMeta(wallet, false),
                AccountMeta.newReadonlyAccountMeta(mint, false),
                AccountMeta.newReadonlyAccountMeta(SystemProgram.PROGRAM_KEY, false),
                AccountMeta.newReadonlyAccountMeta(TokenProgram.PROGRAM_KEY, false),
                AccountMeta.newReadonlyAccountMeta(SYS_VAR_RENT_KEY, false)
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
                AccountMeta.newReadonlyAccountMeta(SYS_VAR_RENT_KEY, false)
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
                byteArrayOf(
                    Command.SetAuthority.value.toByte(),
                    authorityType.value.toByte(),
                    0.toByte()
                )
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

    // Reference: https://github.com/solana-labs/solana-program-library/blob/b011698251981b5a12088acba18fad1d41c3719a/token/program/src/instruction.rs#L183-L197
    data class CloseAccount(
        val account: Key.PublicKey,
        val dest: Key.PublicKey,
        val owner: Key.PublicKey
    ) {

        // Close an account by transferring all its SOL to the destination account.
        // Non-native accounts may only be closed if its token amount is zero.
        //
        // Accounts expected by this instruction:
        //
        //   * Single owner
        //   0. `[writable]` The account to close.
        //   1. `[writable]` The destination account.
        //   2. `[signer]` The account's owner.
        //
        //   * Multisignature owner
        //   0. `[writable]` The account to close.
        //   1. `[writable]` The destination account.
        //   2. `[]` The account's multisignature owner.
        //   3. ..3+M `[signer]` M signer accounts.
        val instruction: Instruction by lazy {
            Instruction.newInstruction(
                PROGRAM_KEY,
                byteArrayOf(Command.CloseAccount.value.toByte()),
                AccountMeta.newAccountMeta(account, false),
                AccountMeta.newAccountMeta(dest, false),
                AccountMeta.newReadonlyAccountMeta(owner, false)
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
            fun fromBytes(bytes: ByteArray): Base64EncodedMemo = Base64EncodedMemo(
                Base64.encodeBase64String(
                    bytes
                )!!
            )
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
