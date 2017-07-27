package com.hansdesk.template.net;

import io.netty.util.internal.SystemPropertyUtil;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * 이 예제에서는 반드시 Component로 선언할 필요는 없지만 Component로 생성하면 다른 Component를 주입받아 사용할 수 있기 때문에 더 편리하다.
 * 실제 사용에서는 preferences 등의 객체를 주입받아 사용하는 경우가 많다.
 *
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public final class SslServerContextFactory {

    private static final String PROTOCOL = "TLSv1.2";

    private SSLContext SERVER_CONTEXT;

    ///////////////////////////////////////////////////////////////////////
    // 서버를 위한 SSLContext 생성
    public SslServerContextFactory() {
        String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null)
            algorithm = "SunX509";

        // JKS 포맷의 keystore 파일을 인증서로 사용하며 Client의 인증서 체크는 하지 않는다.
        // 이 예에서는 cert.keystore 파일이 있고 그 파일의 암호는 certpassword라고 가정한다.
        SSLContext serverContext;
        try {
            String keyStoreFilePath = "cert.keystore"; // keystore 파일 (keytool로 생성된 JKS 파일임)
            String keyStoreFilePassword = "certpassword"; // keystore 파일 비밀번호

            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fin = new FileInputStream(keyStoreFilePath);
            ks.load(fin, keyStoreFilePassword.toCharArray());

            // Set up key manager factory to use our key store.
            // 여기서는 key password와 cert file password가 같다고 가정함.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, keyStoreFilePassword.toCharArray());

            // Initialise the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
      }

    public SSLContext getSslContext() {
        return SERVER_CONTEXT;
    }
}
