package com.hansdesk.template.net;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * 이 예제에서는 반드시 Component로 선언할 필요는 없지만 Component로 생성하면 다른 Component를 주입받아 사용할 수 있기 때문에 더 편리하다.
 * 실제 사용에서는 preferences 등의 객체를 주입받아 사용하는 경우가 많다.
 *
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public final class SslClientContextFactory {

    private static final String PROTOCOL = "TLSv1.2";

    private SSLContext CLIENT_CONTEXT;

    ///////////////////////////////////////////////////////////////////////
    // 클라이언트를 위한 SSLContext 생성. 이 예제에서는 모든 인증서를 Accept한다.
    // 다른 bean을 주입받아서 사용할 수 있도록 생성자 대신 postConstruct()를 사용함.
    @PostConstruct
    private void postConstruct() {
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

    public SSLContext getSslContext() {
        return CLIENT_CONTEXT;
    }
}
