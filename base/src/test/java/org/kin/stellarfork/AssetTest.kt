package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.KeyPair.Companion.random

/**
 * Created by andrewrogers on 7/1/15.
 */
class AssetTest {
    @Test
    fun testAssetTypeNative() {
        val asset = AssetTypeNative
        val xdr = asset.toXdr()
        val parsedAsset = fromXdr(xdr)
        Assert.assertTrue(parsedAsset is AssetTypeNative)
    }

    @Test
    fun testAssetTypeCreditAlphaNum4() {
        val code = "USDA"
        val issuer = random()
        val asset = AssetTypeCreditAlphaNum4(code, issuer)
        val xdr = asset.toXdr()
        val parsedAsset =
            fromXdr(xdr) as AssetTypeCreditAlphaNum4
        Assert.assertEquals(code, asset.code)
        Assert.assertEquals(issuer.accountId, parsedAsset.issuer.accountId)
    }

    @Test
    fun testAssetTypeCreditAlphaNum12() {
        val code = "TESTTEST"
        val issuer = random()
        val asset = AssetTypeCreditAlphaNum12(code, issuer)
        val xdr = asset.toXdr()
        val parsedAsset =
            fromXdr(xdr) as AssetTypeCreditAlphaNum12
        Assert.assertEquals(code, asset.code)
        Assert.assertEquals(issuer.accountId, parsedAsset.issuer.accountId)
    }

    @Test
    fun testHashCode() {
        val issuer1 = random()
        val issuer2 = random()
        // Equal
        Assert.assertEquals(
            AssetTypeNative.hashCode().toLong(),
            AssetTypeNative.hashCode().toLong()
        )
        Assert.assertEquals(
            AssetTypeCreditAlphaNum4("USD", issuer1).hashCode().toLong(),
            AssetTypeCreditAlphaNum4("USD", issuer1).hashCode().toLong()
        )
        Assert.assertEquals(
            AssetTypeCreditAlphaNum12(
                "ABCDE",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode().toLong()
        )
        // Not equal
        Assert.assertNotEquals(
            AssetTypeNative.hashCode().toLong(),
            AssetTypeCreditAlphaNum4("USD", issuer1).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeNative.hashCode().toLong(),
            AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum4(
                "EUR",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum4("USD", issuer1).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum4(
                "EUR",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum4("EUR", issuer2).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum4(
                "EUR",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum12("EUROPE", issuer1).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum4(
                "EUR",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum12("EUROPE", issuer2).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum12(
                "ABCDE",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum12("EDCBA", issuer1).hashCode().toLong()
        )
        Assert.assertNotEquals(
            AssetTypeCreditAlphaNum12(
                "ABCDE",
                issuer1
            ).hashCode().toLong(), AssetTypeCreditAlphaNum12("ABCDE", issuer2).hashCode().toLong()
        )
    }

    @Test
    fun testAssetEquals() {
        val issuer1 = random()
        val issuer2 = random()
        Assert.assertTrue(AssetTypeNative.equals(AssetTypeNative))
        Assert.assertTrue(
            AssetTypeCreditAlphaNum4("USD", issuer1).equals(
                AssetTypeCreditAlphaNum4("USD", issuer1)
            )
        )
        Assert.assertTrue(
            AssetTypeCreditAlphaNum12("ABCDE", issuer1).equals(
                AssetTypeCreditAlphaNum12("ABCDE", issuer1)
            )
        )
        Assert.assertFalse(
            AssetTypeNative.equals(
                AssetTypeCreditAlphaNum4(
                    "USD",
                    issuer1
                )
            )
        )
        Assert.assertFalse(
            AssetTypeNative.equals(
                AssetTypeCreditAlphaNum12(
                    "ABCDE",
                    issuer1
                )
            )
        )
        Assert.assertFalse(
            AssetTypeCreditAlphaNum4("EUR", issuer1).equals(
                AssetTypeCreditAlphaNum4("USD", issuer1)
            )
        )
        Assert.assertFalse(
            AssetTypeCreditAlphaNum4("EUR", issuer1).equals(
                AssetTypeCreditAlphaNum4("EUR", issuer2)
            )
        )
        Assert.assertFalse(
            AssetTypeCreditAlphaNum12("ABCDE", issuer1).equals(
                AssetTypeCreditAlphaNum12("EDCBA", issuer1)
            )
        )
        Assert.assertFalse(
            AssetTypeCreditAlphaNum12("ABCDE", issuer1).equals(
                AssetTypeCreditAlphaNum12("ABCDE", issuer2)
            )
        )
    }
}
