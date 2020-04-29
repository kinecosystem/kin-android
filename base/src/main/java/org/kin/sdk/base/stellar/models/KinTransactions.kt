package org.kin.sdk.base.stellar.models

data class KinTransactions(
    val items: List<KinTransaction>,
    val headPagingToken: KinTransaction.PagingToken?,
    val tailPagingToken: KinTransaction.PagingToken?
)
