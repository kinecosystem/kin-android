package org.kin.sdk.base.viewmodel.di

import org.kin.sdk.design.di.Resolver

interface MetaResolver : Resolver {
    val spendResolver: SpendResolver
}
