package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.Asset.Companion.createNonNativeAsset
import org.kin.stellarfork.KeyPair.Companion.fromAccountId
import org.kin.stellarfork.KeyPair.Companion.fromSecretSeed
import org.kin.stellarfork.Network.Companion.testNetwork
import org.kin.stellarfork.Operation.Companion.fromXdr
import org.kin.stellarfork.Operation.Companion.fromXdrAmount
import org.kin.stellarfork.Operation.Companion.toXdrAmount
import org.kin.stellarfork.Price.Companion.fromString
import org.kin.stellarfork.Signer.ed25519PublicKey
import org.kin.stellarfork.Signer.preAuthTx
import org.kin.stellarfork.Signer.sha256Hash
import java.io.IOException
import java.util.Arrays

class OperationTest {
    @Test
    @Throws(
        FormatException::class,
        IOException::class,
        AssetCodeLengthInvalidException::class
    )
    fun testCreateAccountOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val destination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        val startingAmount = "1000"
        val operation =
            CreateAccountOperation.Builder(destination, startingAmount)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as CreateAccountOperation
        Assert.assertEquals(
            100000000L,
            xdr.body!!.createAccountOp!!.startingBalance!!.int64!!.toLong()
        )
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(destination.accountId, parsedOperation.destination.accountId)
        Assert.assertEquals(startingAmount, parsedOperation.startingBalance)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAABfXhAA==",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(
        FormatException::class,
        IOException::class,
        AssetCodeLengthInvalidException::class
    )
    fun testPaymentOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val destination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        val asset: Asset = AssetTypeNative
        val amount = "1000"
        val operation =
            PaymentOperation.Builder(destination, asset, amount)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as PaymentOperation
        Assert.assertEquals(100000000L, xdr.body!!.paymentOp!!.amount!!.int64!!.toLong())
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(destination.accountId, parsedOperation.destination.accountId)
        Assert.assertTrue(parsedOperation.asset is AssetTypeNative)
        Assert.assertEquals(amount, parsedOperation.amount)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAEAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAAAAAAAAX14QA=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(
        FormatException::class,
        IOException::class,
        AssetCodeLengthInvalidException::class
    )
    fun testPathPaymentOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val destination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        // GCGZLB3X2B3UFOFSHHQ6ZGEPEX7XYPEH6SBFMIV74EUDOFZJA3VNL6X4
        val issuer =
            fromSecretSeed("SBOBVZUN6WKVMI6KIL2GHBBEETEV6XKQGILITNH6LO6ZA22DBMSDCPAG")
        // GAVAQKT2M7B4V3NN7RNNXPU5CWNDKC27MYHKLF5UNYXH4FNLFVDXKRSV
        val pathIssuer1 =
            fromSecretSeed("SALDLG5XU5AEJWUOHAJPSC4HJ2IK3Z6BXXP4GWRHFT7P7ILSCFFQ7TC5")
        // GBCP5W2VS7AEWV2HFRN7YYC623LTSV7VSTGIHFXDEJU7S5BAGVCSETRR
        val pathIssuer2 =
            fromSecretSeed("SA64U7C5C7BS5IHWEPA7YWFN3Z6FE5L6KAMYUIT4AQ7KVTVLD23C6HEZ")
        val sendAsset: Asset = AssetTypeNative
        val sendMax = "0.0001"
        val destAsset: Asset = AssetTypeCreditAlphaNum4("USD", issuer)
        val destAmount = "0.0001"
        val path = arrayOf<Asset?>(
            AssetTypeCreditAlphaNum4("USD", pathIssuer1),
            AssetTypeCreditAlphaNum12("TESTTEST", pathIssuer2)
        )
        val operation = PathPaymentOperation.Builder(
            sendAsset, sendMax, destination, destAsset, destAmount
        )
            .setPath(path)
            .setSourceAccount(source)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as PathPaymentOperation
        Assert.assertEquals(10L, xdr.body!!.pathPaymentOp!!.sendMax!!.int64!!.toLong())
        Assert.assertEquals(10L, xdr.body!!.pathPaymentOp!!.destAmount!!.int64!!.toLong())
        Assert.assertTrue(parsedOperation.sendAsset is AssetTypeNative)
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(destination.accountId, parsedOperation.destination.accountId)
        Assert.assertEquals(sendMax, parsedOperation.sendMax)
        Assert.assertTrue(parsedOperation.destAsset is AssetTypeCreditAlphaNum4)
        Assert.assertEquals(destAmount, parsedOperation.destAmount)
        Assert.assertEquals(path.size.toLong(), parsedOperation.path.size.toLong())
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAIAAAAAAAAAAAAAAAoAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAABVVNEAAAAAACNlYd30HdCuLI54eyYjyX/fDyH9IJWIr/hKDcXKQbq1QAAAAAAAAAKAAAAAgAAAAFVU0QAAAAAACoIKnpnw8rtrfxa276dFZo1C19mDqWXtG4ufhWrLUd1AAAAAlRFU1RURVNUAAAAAAAAAABE/ttVl8BLV0csW/xgXtbXOVf1lMyDluMiafl0IDVFIg==",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(
        FormatException::class,
        IOException::class,
        AssetCodeLengthInvalidException::class
    )
    fun testPathPaymentEmptyPathOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val destination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        // GCGZLB3X2B3UFOFSHHQ6ZGEPEX7XYPEH6SBFMIV74EUDOFZJA3VNL6X4
        val issuer =
            fromSecretSeed("SBOBVZUN6WKVMI6KIL2GHBBEETEV6XKQGILITNH6LO6ZA22DBMSDCPAG")
        // GAVAQKT2M7B4V3NN7RNNXPU5CWNDKC27MYHKLF5UNYXH4FNLFVDXKRSV
        val sendAsset: Asset = AssetTypeNative
        val sendMax = "0.1"
        val destAsset: Asset = AssetTypeCreditAlphaNum4("USD", issuer)
        val destAmount = "0.1"
        val operation = PathPaymentOperation.Builder(
            sendAsset, sendMax, destination, destAsset, destAmount
        )
            .setSourceAccount(source)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as PathPaymentOperation
        Assert.assertEquals(10000L, xdr.body!!.pathPaymentOp!!.sendMax!!.int64!!.toLong())
        Assert.assertEquals(10000L, xdr.body!!.pathPaymentOp!!.destAmount!!.int64!!.toLong())
        Assert.assertTrue(parsedOperation.sendAsset is AssetTypeNative)
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(destination.accountId, parsedOperation.destination.accountId)
        Assert.assertEquals(sendMax, parsedOperation.sendMax)
        Assert.assertTrue(parsedOperation.destAsset is AssetTypeCreditAlphaNum4)
        Assert.assertEquals(destAmount, parsedOperation.destAmount)
        Assert.assertEquals(0, parsedOperation.path.size.toLong())
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAIAAAAAAAAAAAAAJxAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAABVVNEAAAAAACNlYd30HdCuLI54eyYjyX/fDyH9IJWIr/hKDcXKQbq1QAAAAAAACcQAAAAAA==",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(FormatException::class, IOException::class)
    fun testChangeTrustOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val asset: Asset = AssetTypeNative
        val limit = "92233720368547.75807"
        val operation =
            ChangeTrustOperation.Builder(asset, limit)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as ChangeTrustOperation
        Assert.assertEquals(
            9223372036854775807L,
            xdr.body!!.changeTrustOp!!.limit!!.int64!!.toLong()
        )
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertTrue(parsedOperation.asset is AssetTypeNative)
        Assert.assertEquals(limit, parsedOperation.limit)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAYAAAAAf/////////8=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testAllowTrustOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val trustor =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        val assetCode = "USDA"
        val authorize = true
        val operation =
            AllowTrustOperation.Builder(trustor, assetCode, authorize)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as AllowTrustOperation
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(trustor.accountId, parsedOperation.trustor.accountId)
        Assert.assertEquals(assetCode, parsedOperation.assetCode)
        Assert.assertEquals(authorize, parsedOperation.authorize)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAcAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAABVVNEQQAAAAE=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testAllowTrustOperationAssetCodeBuffer() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val trustor =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        val assetCode = "USDABC"
        val authorize = true
        val operation =
            AllowTrustOperation.Builder(trustor, assetCode, authorize)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as AllowTrustOperation
        Assert.assertEquals(assetCode, parsedOperation.assetCode)
    }

    @Test
    @Throws(FormatException::class)
    fun testSetOptionsOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val inflationDestination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        // GBCP5W2VS7AEWV2HFRN7YYC623LTSV7VSTGIHFXDEJU7S5BAGVCSETRR
        val signer =
            ed25519PublicKey(fromSecretSeed("SA64U7C5C7BS5IHWEPA7YWFN3Z6FE5L6KAMYUIT4AQ7KVTVLD23C6HEZ"))
        val clearFlags = 1
        val setFlags = 1
        val masterKeyWeight = 1
        val lowThreshold = 2
        val mediumThreshold = 3
        val highThreshold = 4
        val homeDomain = "stellar.org"
        val signerWeight = 1
        val operation = SetOptionsOperation.Builder()
            .setInflationDestination(inflationDestination)
            .setClearFlags(clearFlags)
            .setSetFlags(setFlags)
            .setMasterKeyWeight(masterKeyWeight)
            .setLowThreshold(lowThreshold)
            .setMediumThreshold(mediumThreshold)
            .setHighThreshold(highThreshold)
            .setHomeDomain(homeDomain)
            .setSigner(signer, signerWeight)
            .setSourceAccount(source)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as SetOptionsOperation
        Assert.assertEquals(
            inflationDestination.accountId,
            parsedOperation.inflationDestination!!.accountId
        )
        Assert.assertEquals(clearFlags, parsedOperation.clearFlags)
        Assert.assertEquals(setFlags, parsedOperation.setFlags)
        Assert.assertEquals(masterKeyWeight, parsedOperation.masterKeyWeight)
        Assert.assertEquals(lowThreshold, parsedOperation.lowThreshold)
        Assert.assertEquals(mediumThreshold, parsedOperation.mediumThreshold)
        Assert.assertEquals(highThreshold, parsedOperation.highThreshold)
        Assert.assertEquals(homeDomain, parsedOperation.homeDomain)
        Assert.assertEquals(
            signer.discriminant!!.value.toLong(),
            parsedOperation.signer!!.discriminant!!.value.toLong()
        )
        Assert.assertEquals(
            signer.ed25519!!.uint256,
            parsedOperation.signer!!.ed25519!!.uint256
        )
        Assert.assertEquals(signerWeight, parsedOperation.signerWeight)
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAUAAAABAAAAAO3gUmG83C+VCqO6FztuMtXJF/l7grZA7MjRzqdZ9W8QAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAIAAAABAAAAAwAAAAEAAAAEAAAAAQAAAAtzdGVsbGFyLm9yZwAAAAABAAAAAET+21WXwEtXRyxb/GBe1tc5V/WUzIOW4yJp+XQgNUUiAAAAAQ==",
            operation.toXdrBase64()
        )
    }

    @Test
    fun testSetOptionsOperationSingleField() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val homeDomain = "stellar.org"
        val operation = SetOptionsOperation.Builder()
            .setHomeDomain(homeDomain)
            .setSourceAccount(source)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as SetOptionsOperation
        Assert.assertEquals(null, parsedOperation.inflationDestination)
        Assert.assertEquals(null, parsedOperation.clearFlags)
        Assert.assertEquals(null, parsedOperation.setFlags)
        Assert.assertEquals(null, parsedOperation.masterKeyWeight)
        Assert.assertEquals(null, parsedOperation.lowThreshold)
        Assert.assertEquals(null, parsedOperation.mediumThreshold)
        Assert.assertEquals(null, parsedOperation.highThreshold)
        Assert.assertEquals(homeDomain, parsedOperation.homeDomain)
        Assert.assertEquals(null, parsedOperation.signer)
        Assert.assertEquals(null, parsedOperation.signerWeight)
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAtzdGVsbGFyLm9yZwAAAAAA",
            operation.toXdrBase64()
        )
    }

    @Test
    fun testSetOptionsOperationSignerSha256() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val preimage = "stellar.org".toByteArray()
        val hash = Util.hash(preimage)
        val operation = SetOptionsOperation.Builder()
            .setSigner(sha256Hash(hash), 10)
            .setSourceAccount(source)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as SetOptionsOperation
        Assert.assertEquals(null, parsedOperation.inflationDestination)
        Assert.assertEquals(null, parsedOperation.clearFlags)
        Assert.assertEquals(null, parsedOperation.setFlags)
        Assert.assertEquals(null, parsedOperation.masterKeyWeight)
        Assert.assertEquals(null, parsedOperation.lowThreshold)
        Assert.assertEquals(null, parsedOperation.mediumThreshold)
        Assert.assertEquals(null, parsedOperation.highThreshold)
        Assert.assertEquals(null, parsedOperation.homeDomain)
        Assert.assertTrue(
            Arrays.equals(
                hash,
                parsedOperation.signer!!.hashX!!.uint256
            )
        )
        Assert.assertEquals(10, parsedOperation.signerWeight)
        Assert.assertEquals(source.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAACbpRqMkaQAfCYSk/n3xIl4fCoHfKqxF34ht2iuvSYEJQAAAAK",
            operation.toXdrBase64()
        )
    }

    @Test
    fun testSetOptionsOperationPreAuthTxSigner() { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
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
            .build()
        // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val opSource =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val operation = SetOptionsOperation.Builder()
            .setSigner(preAuthTx(transaction), 10)
            .setSourceAccount(opSource)
            .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as SetOptionsOperation
        Assert.assertEquals(null, parsedOperation.inflationDestination)
        Assert.assertEquals(null, parsedOperation.clearFlags)
        Assert.assertEquals(null, parsedOperation.setFlags)
        Assert.assertEquals(null, parsedOperation.masterKeyWeight)
        Assert.assertEquals(null, parsedOperation.lowThreshold)
        Assert.assertEquals(null, parsedOperation.mediumThreshold)
        Assert.assertEquals(null, parsedOperation.highThreshold)
        Assert.assertEquals(null, parsedOperation.homeDomain)
        Assert.assertTrue(
            Arrays.equals(
                transaction.hash(),
                parsedOperation.signer!!.preAuthTx!!.uint256
            )
        )
        Assert.assertEquals(10, parsedOperation.signerWeight)
        Assert.assertEquals(opSource.accountId, parsedOperation.sourceAccount!!.accountId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAABpyjrLKgNYz4rlAOXBVLamj+N103AG2lW5GAsoXHZoBMAAAAK",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testManageOfferOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GBCP5W2VS7AEWV2HFRN7YYC623LTSV7VSTGIHFXDEJU7S5BAGVCSETRR
        val issuer =
            fromSecretSeed("SA64U7C5C7BS5IHWEPA7YWFN3Z6FE5L6KAMYUIT4AQ7KVTVLD23C6HEZ")
        val selling: Asset = AssetTypeNative
        val buying =
            createNonNativeAsset("USD", issuer)
        val amount = "0.01"
        val price = "0.85334384" // n=5333399 d=6250000
        val priceObj = fromString(price)
        val offerId: Long = 1
        val operation =
            ManageOfferOperation.Builder(selling, buying, amount, price)
                .setOfferId(offerId)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as ManageOfferOperation
        Assert.assertEquals(1000L, xdr.body!!.manageOfferOp!!.amount!!.int64!!.toLong())
        Assert.assertTrue(parsedOperation.selling is AssetTypeNative)
        Assert.assertTrue(parsedOperation.buying is AssetTypeCreditAlphaNum4)
        Assert.assertTrue(parsedOperation.buying.equals(buying))
        Assert.assertEquals(amount, parsedOperation.amount)
        Assert.assertEquals(price, parsedOperation.price)
        Assert.assertEquals(priceObj.numerator.toLong(), 5333399)
        Assert.assertEquals(priceObj.denominator.toLong(), 6250000)
        Assert.assertEquals(offerId, parsedOperation.offerId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAMAAAAAAAAAAVVTRAAAAAAARP7bVZfAS1dHLFv8YF7W1zlX9ZTMg5bjImn5dCA1RSIAAAAAAAAD6ABRYZcAX14QAAAAAAAAAAE=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testCreatePassiveOfferOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GBCP5W2VS7AEWV2HFRN7YYC623LTSV7VSTGIHFXDEJU7S5BAGVCSETRR
        val issuer =
            fromSecretSeed("SA64U7C5C7BS5IHWEPA7YWFN3Z6FE5L6KAMYUIT4AQ7KVTVLD23C6HEZ")
        val selling: Asset = AssetTypeNative
        val buying =
            createNonNativeAsset("USD", issuer)
        val amount = "0.01"
        val price = "2.93850088" // n=36731261 d=12500000
        val priceObj = fromString(price)
        val operation =
            CreatePassiveOfferOperation.Builder(selling, buying, amount, price)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            Operation.fromXdr(xdr) as CreatePassiveOfferOperation
        Assert.assertEquals(1000L, xdr.body!!.createPassiveOfferOp!!.amount!!.int64!!.toLong())
        Assert.assertTrue(parsedOperation.selling is AssetTypeNative)
        Assert.assertTrue(parsedOperation.buying is AssetTypeCreditAlphaNum4)
        Assert.assertTrue(parsedOperation.buying.equals(buying))
        Assert.assertEquals(amount, parsedOperation.amount)
        Assert.assertEquals(price, parsedOperation.price)
        Assert.assertEquals(priceObj.numerator.toLong(), 36731261)
        Assert.assertEquals(priceObj.denominator.toLong(), 12500000)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAQAAAAAAAAAAVVTRAAAAAAARP7bVZfAS1dHLFv8YF7W1zlX9ZTMg5bjImn5dCA1RSIAAAAAAAAD6AIweX0Avrwg",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testAccountMergeOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        // GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR
        val destination =
            fromSecretSeed("SDHZGHURAYXKU2KMVHPOXI6JG2Q4BSQUQCEOY72O3QQTCLR2T455PMII")
        val operation =
            AccountMergeOperation.Builder(destination)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as AccountMergeOperation
        Assert.assertEquals(destination.accountId, parsedOperation.destination.accountId)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAgAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxA=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testManageDataOperation() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val operation =
            ManageDataOperation.Builder("test", byteArrayOf(0, 1, 2, 3, 4))
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as ManageDataOperation
        Assert.assertEquals("test", parsedOperation.name)
        Assert.assertTrue(
            Arrays.equals(
                byteArrayOf(0, 1, 2, 3, 4),
                parsedOperation.value
            )
        )
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAoAAAAEdGVzdAAAAAEAAAAFAAECAwQAAAA=",
            operation.toXdrBase64()
        )
    }

    @Test
    @Throws(IOException::class, FormatException::class)
    fun testManageDataOperationEmptyValue() { // GC5SIC4E3V56VOHJ3OZAX5SJDTWY52JYI2AFK6PUGSXFVRJQYQXXZBZF
        val source =
            fromSecretSeed("SC4CGETADVYTCR5HEAVZRB3DZQY5Y4J7RFNJTRA6ESMHIPEZUSTE2QDK")
        val operation =
            ManageDataOperation.Builder("test", null)
                .setSourceAccount(source)
                .build()
        val xdr = operation.toXdr()
        val parsedOperation =
            fromXdr(xdr) as ManageDataOperation
        Assert.assertEquals("test", parsedOperation.name)
        Assert.assertEquals(null, parsedOperation.value)
        Assert.assertEquals(
            "AAAAAQAAAAC7JAuE3XvquOnbsgv2SRztjuk4RoBVefQ0rlrFMMQvfAAAAAoAAAAEdGVzdAAAAAA=",
            operation.toXdrBase64()
        )
    }

    @Test
    fun testToXdrAmount() {
        Assert.assertEquals(0L, toXdrAmount("0"))
        Assert.assertEquals(1L, toXdrAmount("0.00001"))
        Assert.assertEquals(10000000L, toXdrAmount("100"))
        Assert.assertEquals(
            11234567L,
            toXdrAmount("112.34567")
        )
        Assert.assertEquals(
            729912843007381L,
            toXdrAmount("7299128430.07381")
        )
        Assert.assertEquals(
            729912843007381L,
            toXdrAmount("7299128430.073810")
        )
        Assert.assertEquals(
            1014016711446800155L,
            toXdrAmount("10140167114468.00155")
        )
        Assert.assertEquals(
            9223372036854775807L,
            toXdrAmount("92233720368547.75807")
        )
        try {
            toXdrAmount("0.000001")
            Assert.fail()
        } catch (e: ArithmeticException) {
        } catch (e: Exception) {
            Assert.fail()
        }
        try {
            toXdrAmount("7299128430.073811")
            Assert.fail()
        } catch (e: ArithmeticException) {
        } catch (e: Exception) {
            Assert.fail()
        }
    }

    @Test
    fun testFromXdrAmount() {
        Assert.assertEquals("0", fromXdrAmount(0L))
        Assert.assertEquals("0.00001", fromXdrAmount(1L))
        Assert.assertEquals("100", fromXdrAmount(10000000L))
        Assert.assertEquals(
            "112.34567",
            fromXdrAmount(11234567L)
        )
        Assert.assertEquals(
            "7299128430.07381",
            fromXdrAmount(729912843007381L)
        )
        Assert.assertEquals(
            "10140167114468.00155",
            fromXdrAmount(1014016711446800155L)
        )
        Assert.assertEquals(
            "92233720368547.75807",
            fromXdrAmount(9223372036854775807L)
        )
    }
}
