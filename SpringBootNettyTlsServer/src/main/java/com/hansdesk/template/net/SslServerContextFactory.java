package com.hansdesk.template.net;

import io.netty.util.internal.SystemPropertyUtil;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public final class SslServerContextFactory {

    private static final String PROTOCOL = "TLSv1.2";
    private static final SSLContext SERVER_CONTEXT;

    ///////////////////////////////////////////////////////////////////////
    // 서버를 위한 SSLContext 생성
    static {
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
            // Assume key password is the same as the key store file password.
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

    public static SSLContext getSslContext() {
        return SERVER_CONTEXT;
    }

    private SslServerContextFactory() {
        // Singleton object.
    }
}
