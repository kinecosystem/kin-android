package org.kin.sdk.demo.di.modern

import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.KinAccount

class DemoAppConfig {
    companion object {
        // Used to test with no webhook (0 is webhook un-set pure pass through to the blockchain)
        val DEMO_APP_IDX = AppIdx(0)
        val DEMO_APP_SECRET_SEED = "SA7PKYPSJHOU5I6YTU6OJIOLZXREBCX6N5QK7USSXQCKS65SWSQPIMA7"
        val DEMO_APP_ACCOUNT_ID =
            KinAccount.Id("GDV4TKOCDBHB3XGCKAXWYETQRIN4RTJKSD6FQV43E2AUHORR56B4YDC4") //"GAO47SC3PMCXVWIQLZSKUCFZQ4MLUAEZIPPILUKSFCFQDCHZHGZJDNQ6")


//        // Used to test with webhook - blindly whitelists with no restrictions
//        val DEMO_APP_IDX = AppIdx(1)
//        val DEMO_APP_ACCOUNT_ID =
//            KinAccount.Id("GDHCB4VCNNFIMZI3BVHLA2FVASECBR2ZXHOAXEBBFVUH5G2YAD7V3JVH")
    }
}
