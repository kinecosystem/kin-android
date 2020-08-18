package org.kin.sdk.base.network.api

import okhttp3.OkHttpClient
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.network.api.horizon.KinFriendBotApi
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Util
import org.kin.stellarfork.requests.RequestBuilder
import org.kin.stellarfork.responses.HttpResponseException
import org.kin.stellarfork.responses.TransactionResponseLight
import org.kin.stellarfork.xdr.AccountEntry
import org.kin.stellarfork.xdr.TransactionMeta
import java.net.URI


internal class FriendBotRequestBuilder(client: OkHttpClient, friendBotBaseUri: URI) :
    RequestBuilder(client, friendBotBaseUri) {

    fun createAccount(keyPair: KeyPair): TransactionResponseLight? {
        uriBuilder.addQueryParameter("addr", keyPair.accountId)
        return execute(buildUri())
    }

    fun fundAccount(keyPair: KeyPair): TransactionResponseLight? {
        setSegments("fund")
        uriBuilder.addQueryParameter("addr", keyPair.accountId)
        return execute(buildUri())
    }
}

/**
 * This is valid for testnet only
 */
class FriendBotApi(
    private val okHttpClient: OkHttpClient,
    private val friendBotBaseUrl: String = "https://friendbot-testnet.kininfrastructure.com"
) : KinFriendBotApi {
    override fun createAccount(
        request: CreateAccountRequest,
        onCompleted: (CreateAccountResponse) -> Unit
    ) {
        try {
            val keyPair = request.accountId.toKeyPair()
            onCompleted(FriendBotRequestBuilder(okHttpClient, URI.create(friendBotBaseUrl)).createAccount(keyPair).toCreateAccountResponse(keyPair))
        } catch (t: Throwable) {
            if ((t as? HttpResponseException)?.statusCode == 400) {
                onCompleted(CreateAccountResponse(CreateAccountResponse.Result.Exists))
            } else {
                onCompleted(CreateAccountResponse(CreateAccountResponse.Result.TransientFailure(t)))
            }
        }
    }

    override fun fundAccount(
        request: CreateAccountRequest,
        onCompleted: (CreateAccountResponse) -> Unit
    ) {
        try {
            val keyPair = request.accountId.toKeyPair()
            onCompleted(FriendBotRequestBuilder(okHttpClient, URI.create(friendBotBaseUrl)).fundAccount(keyPair).toFundAccountResponse(keyPair))
        } catch (t: Throwable) {
            onCompleted(CreateAccountResponse(CreateAccountResponse.Result.TransientFailure(t)))
        }
    }
}

internal fun TransactionResponseLight?.toCreateAccountResponse(keyPair: KeyPair): CreateAccountResponse {
    var accountEntry: AccountEntry? = null
    TransactionMeta.decode(Util.createXdrDataInputStream(this!!.resultMetaXdr))
        .operations.first()!!
        .changes!!
        .ledgerEntryChanges
        .forEach {
            val maybeAccountId = it!!.created?.data?.account?.accountID?.accountID
            if (maybeAccountId != null && KeyPair.fromXdrPublicKey(maybeAccountId).accountId == keyPair.accountId
            ) {
                accountEntry = it.created!!.data!!.account
            }
        }

    val balance = KinBalance(QuarkAmount(accountEntry!!.balance!!.int64!!).toKin())
    val seqId = accountEntry!!.seqNum!!.sequenceNumber!!.uint64!!

    return CreateAccountResponse(
        CreateAccountResponse.Result.Ok,
        KinAccount(
            keyPair.asPublicKey(),
            balance = balance,
            status = KinAccount.Status.Registered(seqId)
        )
    )
}

internal fun TransactionResponseLight?.toFundAccountResponse(keyPair: KeyPair): CreateAccountResponse {
    val xdrDataInputStream = Util.createXdrDataInputStream(this!!.resultMetaXdr)
    val transactionMeta = TransactionMeta.decode(xdrDataInputStream)

    var accountEntry: AccountEntry? = null
    transactionMeta.operations
        .first()!!
        .changes!!
        .ledgerEntryChanges
        .forEach {
            val maybeAccountId = it!!.updated?.data?.account?.accountID?.accountID
            if (maybeAccountId != null && KeyPair.fromXdrPublicKey(maybeAccountId).accountId == keyPair.accountId
            ) {
                accountEntry = it.updated!!.data!!.account
            }
        }

    val balance = KinBalance(QuarkAmount(accountEntry!!.balance!!.int64!!).toKin())
    val seqId = accountEntry!!.seqNum!!.sequenceNumber!!.uint64!!

    return CreateAccountResponse(
        CreateAccountResponse.Result.Ok,
        KinAccount(
            keyPair.asPublicKey(),
            balance = balance,
            status = KinAccount.Status.Registered(seqId)
        )
    )
}
