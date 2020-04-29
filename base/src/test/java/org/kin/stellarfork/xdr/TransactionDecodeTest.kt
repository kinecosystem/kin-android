package org.kin.stellarfork.xdr

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.TransactionResultCode
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Arrays

class TransactionDecodeTest {
    @Test
    @Throws(IOException::class)
    fun testDecodeTxBody() { // pubnet - ledgerseq 5845058, txid  d5ec6645d86cdcae8212cbe60feaefb8d6b1a8b7d11aeea590608b0863ace4de
        val txBody =
            "AAAAAERmsKL73CyLV/HvjyQCERDXXpWE70Xhyb6MR5qPO3yQAAAAZAAIbkEAACD7AAAAAAAAAAN43bSwpXw8tSAhl7TBtQeOZTQAXwAAAAAAAAAAAAAAAAAAAAEAAAABAAAAAP1qe44j+i4uIT+arbD4QDQBt8ryEeJd7a0jskQ3nwDeAAAAAAAAAADdVhDVFrUiS/jPrRpblXY4bAW9u4hbRI2Hhw+2ATsFpQAAAAAtPWvAAAAAAAAAAAGPO3yQAAAAQHGWVHCBsjTyap/OY9JjPHmzWtN2Y2sL98aMERc/xJ3hcWz6kdQAwjlEhilItCyokDHCrvALZy3v/1TlaDqprA0="
        val base64Codec = Base64()
        val bytes = base64Codec.decode(txBody)
        val transactionEnvelope =
            TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
        Assert.assertEquals(
            2373025265623291L,
            transactionEnvelope.tx!!.seqNum!!.sequenceNumber!!.uint64
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDecodeTxResult() { // pubnet - ledgerseq 5845058, txid  d5ec6645d86cdcae8212cbe60feaefb8d6b1a8b7d11aeea590608b0863ace4de
        val txResult =
            "1exmRdhs3K6CEsvmD+rvuNaxqLfRGu6lkGCLCGOs5N4AAAAAAAAAZAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAA=="
        val base64Codec = Base64()
        val bytes = base64Codec.decode(txResult)
        val transactionResult =
            TransactionResultPair.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
        Assert.assertEquals(
            TransactionResultCode.txSUCCESS,
            transactionResult.result!!.result!!.discriminant
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDecodeTxMeta() { // pubnet - ledgerseq 5845058, txid  d5ec6645d86cdcae8212cbe60feaefb8d6b1a8b7d11aeea590608b0863ace4de
        val txMeta =
            "AAAAAAAAAAEAAAADAAAAAABZMEIAAAAAAAAAAN1WENUWtSJL+M+tGluVdjhsBb27iFtEjYeHD7YBOwWlAAAAAC09a8AAWTBCAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBZL8QAAAAAAAAAAP1qe44j+i4uIT+arbD4QDQBt8ryEeJd7a0jskQ3nwDeAALU1gZ4V7UACD1BAAAAHgAAAAoAAAAAAAAAAAAAAAABAAAAAAAACgAAAAARC07BokpLTOF+/vVKBwiAlop7hHGJTNeGGlY4MoPykwAAAAEAAAAAK+Lzfd3yDD+Ov0GbYu1g7SaIBrKZeBUxoCunkLuI7aoAAAABAAAAAERmsKL73CyLV/HvjyQCERDXXpWE70Xhyb6MR5qPO3yQAAAAAQAAAABSORGwAdyuanN3sNOHqNSpACyYdkUM3L8VafUu69EvEgAAAAEAAAAAeCzqJNkMM/jLvyuMIfyFHljBlLCtDyj17RMycPuNtRMAAAABAAAAAIEi4R7juq15ymL00DNlAddunyFT4FyUD4muC4t3bobdAAAAAQAAAACaNpLL5YMfjOTdXVEqrAh99LM12sN6He6pHgCRAa1f1QAAAAEAAAAAqB+lfAPV9ak+Zkv4aTNZwGaFFAfui4+yhM3dGhoYJ+sAAAABAAAAAMNJrEvdMg6M+M+n4BDIdzsVSj/ZI9SvAp7mOOsvAD/WAAAAAQAAAADbHA6xiKB1+G79mVqpsHMOleOqKa5mxDpP5KEp/Xdz9wAAAAEAAAAAAAAAAAAAAAEAWTBCAAAAAAAAAAD9anuOI/ouLiE/mq2w+EA0AbfK8hHiXe2tI7JEN58A3gAC1NXZOuv1AAg9QQAAAB4AAAAKAAAAAAAAAAAAAAAAAQAAAAAAAAoAAAAAEQtOwaJKS0zhfv71SgcIgJaKe4RxiUzXhhpWODKD8pMAAAABAAAAACvi833d8gw/jr9Bm2LtYO0miAaymXgVMaArp5C7iO2qAAAAAQAAAABEZrCi+9wsi1fx748kAhEQ116VhO9F4cm+jEeajzt8kAAAAAEAAAAAUjkRsAHcrmpzd7DTh6jUqQAsmHZFDNy/FWn1LuvRLxIAAAABAAAAAHgs6iTZDDP4y78rjCH8hR5YwZSwrQ8o9e0TMnD7jbUTAAAAAQAAAACBIuEe47qtecpi9NAzZQHXbp8hU+BclA+JrguLd26G3QAAAAEAAAAAmjaSy+WDH4zk3V1RKqwIffSzNdrDeh3uqR4AkQGtX9UAAAABAAAAAKgfpXwD1fWpPmZL+GkzWcBmhRQH7ouPsoTN3RoaGCfrAAAAAQAAAADDSaxL3TIOjPjPp+AQyHc7FUo/2SPUrwKe5jjrLwA/1gAAAAEAAAAA2xwOsYigdfhu/ZlaqbBzDpXjqimuZsQ6T+ShKf13c/cAAAABAAAAAAAAAAA="
        val base64Codec = Base64()
        val bytes = base64Codec.decode(txMeta)
        val transactionMeta =
            TransactionMeta.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
        Assert.assertEquals(1, transactionMeta.operations.size.toLong())
    }

    @Test
    @Throws(IOException::class)
    fun testTransactionEnvelopeWithMemo() {
        val transactionEnvelopeToDecode =
            "AAAAACq1Ixcw1fchtF5aLTSw1zaYAYjb3WbBRd4jqYJKThB9AAAAZAA8tDoAAAALAAAAAAAAAAEAAAAZR29sZCBwYXltZW50IGZvciBzZXJ2aWNlcwAAAAAAAAEAAAAAAAAAAQAAAAARREGslec48mbJJygIwZoLvRtL6/gGL4ss2TOpnOUOhgAAAAFHT0xEAAAAACq1Ixcw1fchtF5aLTSw1zaYAYjb3WbBRd4jqYJKThB9AAAAADuaygAAAAAAAAAAAA=="
        val base64Codec = Base64()
        val bytes = base64Codec.decode(transactionEnvelopeToDecode)
        val transactionEnvelope =
            TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
        Assert.assertEquals(1, transactionEnvelope.tx!!.operations.size.toLong())
        Assert.assertTrue(
            Arrays.equals(
                byteArrayOf(
                    'G'.toByte(),
                    'O'.toByte(),
                    'L'.toByte(),
                    'D'.toByte()
                ),
                transactionEnvelope.tx!!.operations[0]!!.body!!.paymentOp!!.asset!!.alphaNum4!!.assetCode
            )
        )
    }
}
