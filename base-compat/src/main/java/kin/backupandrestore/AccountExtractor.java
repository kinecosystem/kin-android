package kin.backupandrestore;

import androidx.annotation.Nullable;

import kin.sdk.KinAccount;
import kin.sdk.KinClient;

public class AccountExtractor {

    @Nullable
    public static KinAccount getKinAccount(KinClient kinClient, String publicAddress) {
        KinAccount kinAccount = null;
        if (kinClient != null && publicAddress != null && publicAddress.length() > 0 ) {
            int numOfAccounts = kinClient.getAccountCount();
            for (int i = 0; i < numOfAccounts; i++) {
                KinAccount account = kinClient.getAccount(i);
                if (account != null && account.getPublicAddress().equals(publicAddress)) {
                    kinAccount = account;
                    break;
                }
            }
        }
        return kinAccount;
    }
}
