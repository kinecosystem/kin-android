package org.kin.sdk.base.models.solana

import org.kin.sdk.base.models.Key

/**
 * AccountMeta represents the account information required
 * for building transactions.
 */
data class AccountMeta constructor(
    val publicKey: Key.PublicKey,
    val isSigner: Boolean = false,
    val isWritable: Boolean = false,
    val isPayer: Boolean = false,
    val isProgram: Boolean = false,
) : Comparable<AccountMeta> {
    companion object {
        fun newAccountMeta(
            publicKey: Key.PublicKey,
            isSigner: Boolean,
            isPayer: Boolean = false,
            isProgram: Boolean = false,
        ): AccountMeta =
            AccountMeta(
                publicKey = publicKey,
                isSigner = isSigner,
                isWritable = true,
                isPayer = isPayer,
                isProgram = isProgram,
            )

        fun newReadonlyAccountMeta(
            publicKey: Key.PublicKey,
            isSigner: Boolean,
            isPayer: Boolean = false,
            isProgram: Boolean = false,
        ): AccountMeta =
            AccountMeta(
                publicKey = publicKey,
                isSigner = isSigner,
                isWritable = false,
                isPayer = isPayer,
                isProgram = isProgram
            )
    }

    override fun compareTo(other: AccountMeta): Int {
        fun less(other: AccountMeta): Boolean {
            if (isPayer != other.isPayer) {
                return isPayer
            }
            if (isProgram != other.isProgram) {
                return !isProgram
            }
            if (isSigner != other.isSigner) {
                return isSigner
            }
            if (isWritable != other.isWritable) {
                return isWritable
            }

            return false
        }
        return if (less(other)) -1 else 1
    }
}

/**
 * Instruction represents a transaction instruction.
 */
data class Instruction constructor(
    val program: Key.PublicKey,
    val accounts: List<AccountMeta>,
    val data: ByteArray,
) {
    companion object {
        // NewInstruction creates a new instruction.
        fun newInstruction(
            program: Key.PublicKey,
            data: ByteArray,
            vararg accounts: AccountMeta,
        ): Instruction {
            return Instruction(
                program,
                accounts.asList(),
                data
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Instruction) return false

        if (program != other.program) return false
        if (accounts != other.accounts) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = program.hashCode()
        result = 31 * result + accounts.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

data class CompiledInstruction(
    val programIndex: Byte,
    val accounts: ByteArray,
    val data: ByteArray,
) {
    companion object {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompiledInstruction) return false

        if (programIndex != other.programIndex) return false
        if (!accounts.contentEquals(other.accounts)) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = programIndex.toInt()
        result = 31 * result + accounts.contentHashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
