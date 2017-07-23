package com.hansdesk.template.net;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public final class SslClientContextFactory {

    private static final String PROTOCOL = "TLSv1.2";
    private static final SSLContext CLIENT_CONTEXT;

    static {
        ///////////////////////////////////////////////////////////////////////
        // 클라이언트를 위한 SSLContext 생성.
        // 이 예제에서는 모든 인증서를 Accept한다.

        SSLContext clientContext;
        try {
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, new TrustManager[] { tm }, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getSslContext() {
        return CLIENT_CONTEXT;
    }

    private SslClientContextFactory() {
        // Singleton object.
    }
}
