package org.kin.sdk.base.models.solana

import org.kin.sdk.base.models.Key
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun Transaction.marshal(): ByteArray {
    val output = ByteArrayOutputStream()

    // Signatures
    ShortVec.encodeShortVecOf(output, signatures, Signature::marshal)

    // Message
    output.write(message.marshal())

    return output.toByteArray()
}

fun Signature.marshal(): ByteArray = value.byteArray

fun Signature.Companion.unmarshal(bytes: ByteArray): Signature = Signature(FixedByteArray64(bytes))

fun Transaction.Companion.unmarshal(bytes: ByteArray): Transaction {
    val input = ByteArrayInputStream(bytes)

    val signatures =
        ShortVec.decodeShortVecOf(input, Signature.SIZE_OF, Signature.Companion::unmarshal)

    return Transaction(
        message = Message.unmarshal(input.readRemainingBytes()),
        signatures = signatures
    )
}

fun CompiledInstruction.marshal(): ByteArray {
    val output = ByteArrayOutputStream()

    output.write(byteArrayOf(programIndex))

    // Accounts
    ShortVec.encodeLen(output, accounts.size)
    output.write(accounts)

    // Data
    ShortVec.encodeLen(output, data.size)
    output.write(data)

    return output.toByteArray()
}

//fun CompiledInstruction.Companion.unmarshal(): Instruction {
//
//}

fun Key.PublicKey.marshal(): ByteArray = value

fun Hash.marshal(): ByteArray = value.byteArray

fun Message.marshal(): ByteArray {
    val output = ByteArrayOutputStream()

    // Header
    output.write(header.numSignatures)
    output.write(header.numReadOnlySigned)
    output.write(header.numReadOnly)

    // Accounts (panic?)
    ShortVec.encodeShortVecOf(output, accounts, Key.PublicKey::marshal)

    // Recent Blockhash
    output.write(recentBlockhash.marshal())

    // Instructions
    ShortVec.encodeShortVecOf(output, instructions, CompiledInstruction::marshal)

    return output.toByteArray()
}

fun Message.Companion.unmarshal(bytes: ByteArray): Message {
    val input = ByteArrayInputStream(bytes)

    // Header
    val numSignatures = wrapError("failed to read num signatures", input::read)
    val numReadOnlySigned = wrapError("failed to read num readonly signatures", input::read)
    val numReadOnly = wrapError("failed to read num readonly", input::read)
    val header = Header(
        numSignatures = numSignatures,
        numReadOnlySigned = numReadOnlySigned,
        numReadOnly = numReadOnly
    )

    // Accounts
    val accounts = ShortVec.decodeShortVecOf<Key.PublicKey>(input, 32)

    // Recent Block Hash
    val recentBlockHash =
        wrapError("failed to read block hash") {
            input.read(Hash.SIZE_OF).toModel<Hash> {
                Hash(FixedByteArray32(it))
            }
        }

    // Instructions
    val instructions = mutableListOf<CompiledInstruction>()
    val instructionsLength = ShortVec.decodeLen(input)
    (0 until instructionsLength).forEach { i ->

        // Program Index
        val programIndex =
            wrapError("failed to read instruction[$i] program index") { input.read().toByte() }
        if (programIndex < 0 || programIndex >= accounts.size) {
            throw RuntimeException("program index out of range: $i:$programIndex")
        }

        // Account Indexes
        val accountIndexesLength = ShortVec.decodeLen(input)
        val accountIndexesBytes = wrapError("failed to read instruction[$i] accounts") {
            input.read(accountIndexesLength)
        }
        accountIndexesBytes.forEach {
            if (it.toInt() >= accounts.size) {
                throw RuntimeException("account index out of range: $i:$it")
            }
        }

        // Data
        val dataLength =
            wrapError("failed to read instruction[$i] data") { ShortVec.decodeLen(input) }
        val dataBytes = input.read(dataLength)

        val instruction = CompiledInstruction(
            programIndex = programIndex,
            accounts = accountIndexesBytes,
            data = dataBytes,
        )
        instructions.add(instruction)
    }

    return Message(
        header = header,
        accounts = accounts,
        instructions = instructions,
        recentBlockhash = recentBlockHash
    )
}


