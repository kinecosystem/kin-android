package kin.sdk.legacytests;

import androidx.annotation.NonNull;

import org.kin.sdk.base.stellar.models.KinTransaction;
import org.kin.sdk.base.stellar.models.NetworkEnvironment;
import org.kin.sdk.base.stellar.models.StellarKinTransaction;
import org.kin.stellarfork.codec.Base64;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

final class TestUtils {
    static String loadResource(Class clazz, String res) {
        InputStream is = clazz.getClassLoader()
                .getResourceAsStream(res);
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @NonNull
    static MockResponse generateSuccessMockResponse(Class clazz, String res) {
        return new MockResponse()
                .setBody(loadResource(clazz, res))
                .setResponseCode(200);
    }

    @NonNull
    static MockResponse generateSuccessHttp307MockResponse(String location) {
        return new MockResponse()
                .setHeader("Location", location)
                .setResponseCode(307);
    }

    static void enqueueEmptyResponse(MockWebServer mockWebServer) {
        //simulate http 200 with no body, will cause to parse empty body and response will be null
        mockWebServer.enqueue(new MockResponse().setBodyDelay(1, TimeUnit.SECONDS));
    }
    static KinTransaction kinTransactionFromXDR(String xdr) {
        return new StellarKinTransaction(Objects.requireNonNull(Base64.decodeBase64(xdr)), new KinTransaction.RecordType.InFlight(System.currentTimeMillis()), NetworkEnvironment.KinStellarTestNetKin3.INSTANCE);
    }
}
