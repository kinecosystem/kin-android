package org.kin.sdk.base.models.solana

import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.tools.quickSort
import org.kin.sdk.base.tools.toHexString

data class Signature(val value: FixedByteArray64 = FixedByteArray64()) {
    companion object {
        const val SIZE_OF = 64
    }
}

data class Hash(val value: FixedByteArray32) {
    companion object {
        const val SIZE_OF = 32
    }
}

data class Header(
    val numSignatures: Int,
    val numReadOnlySigned: Int,
    val numReadOnly: Int,
)

data class Message(
    val header: Header,
    val accounts: List<Key.PublicKey>,
    val instructions: List<CompiledInstruction>,
    val recentBlockhash: Hash,
) {
    companion object
}

data class Transaction(
    val message: Message,
    val signatures: List<Signature> = emptyList(),
) {
    val numRequiredSignatures: Int = message.header.numSignatures

    companion object {
        fun newTransaction(
            payer: Key.PublicKey,
            vararg instructions: Instruction,
        ): Transaction {
            val accounts = mutableListOf(
                AccountMeta(
                    payer,
                    isSigner = true,
                    isWritable = true,
                    isPayer = true,
                )
            )

            // Extract all of the unique accounts from the instructions.
            instructions.forEach {
                accounts.add(
                    AccountMeta(
                        publicKey = it.program,
                        isProgram = true,
                    )
                )
                accounts.addAll(it.accounts)
            }

            // Sort the account meta's based on:
            //   1. Payer is always the first account / signer.
            //   1. All signers are before non-signers.
            //   2. Writable accounts before read-only accounts.
            //   3. Programs last
            val uniqueAccounts = accounts.filterUnique()
                .toMutableList()
                .quickSort()

            val header = Header(
                numSignatures = uniqueAccounts.filter { it.isSigner }.count(),
                numReadOnlySigned = uniqueAccounts.filter { !it.isWritable && it.isSigner }.count(),
                numReadOnly = uniqueAccounts.filter { !it.isWritable && !it.isSigner }.count()
            )
            val accountPublicKeys = uniqueAccounts.map { it.publicKey }
            val messageInstructions = instructions.map {
                CompiledInstruction(
                    programIndex = indexOf(accountPublicKeys, it.program).toByte(),
                    data = it.data,
                    accounts = it.accounts.map { indexOf(accountPublicKeys, it.publicKey).toByte() }
                        .toByteArray()
                )
            }
            val message = Message(
                header = header,
                accounts = accountPublicKeys,
                instructions = messageInstructions,
                recentBlockhash = Hash(FixedByteArray32())
                /** Empty unless set with [copyAndSetRecentBlockhash] **/
            )

            return Transaction(message = message)
        }

        private fun indexOf(slice: List<Key.PublicKey>, item: Key.PublicKey): Int {
            slice.forEachIndexed { i, publicKey ->
                if (publicKey.value.contentEquals(item.value)) {
                    return i
                }
            }
            return -1
        }
    }

    fun copyAndSetRecentBlockhash(recentBlockhash: Hash): Transaction {
        return copy(message = message.copy(recentBlockhash = recentBlockhash))
    }

    fun copyAndSign(vararg signers: Key.PrivateKey): Transaction {
        if (signers.size > numRequiredSignatures) {
            throw IllegalArgumentException("too many signers")
        }

        val messageBytes = message.marshal()

        val newSignatures = arrayOfNulls<Signature>(numRequiredSignatures)
        signatures.forEachIndexed { index, signature ->
            newSignatures[index] = signature
        }
        signers.forEach {
            val pubKey = it.asPublicKey()
            val index = indexOf(message.accounts, pubKey)
            if (index < 0) {
                throw IllegalArgumentException(
                    "signing account " +
                            "${pubKey.value.toHexString()} is not in the account list"
                )
            }
            newSignatures[index] = Signature(FixedByteArray64(it.sign(messageBytes)))
        }

        return copy(
            signatures = newSignatures.map { it ?: Signature() }
        )
    }
}

/**
 * Provide a unique set by publicKey of AccountMeta with the highest write permission
 */
fun List<AccountMeta>.filterUnique(): List<AccountMeta> {
    val filtered = mutableListOf<AccountMeta>()

    outer@ for (i in this) {
        for ((j, accountMeta) in filtered.withIndex()) {
            if (i.publicKey.value.contentEquals(accountMeta.publicKey.value)) {
                // Promote the existing account to writable if applicable
                if (i.isSigner) {
                    filtered[j] = filtered[j].copy(isSigner = true)
                }
                if (i.isWritable) {
                    filtered[j] = filtered[j].copy(isWritable = true)
                }
                if (i.isPayer) {
                    filtered[j] = filtered[j].copy(isPayer = true)
                }
                continue@outer
            }
        }
        filtered.add(i)
    }

    return filtered.toList()
}





