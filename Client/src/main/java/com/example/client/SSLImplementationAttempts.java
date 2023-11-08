package com.example.client;

public class SSLImplementationAttempts {
    //        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        try (InputStream keyStoreStream = getClass().getResourceAsStream("/path/to/keystore.jks")) {
//            keyStore.load(keyStoreStream, "password".toCharArray());
//        }
//
//        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        try (InputStream trustStoreStream = getClass().getResourceAsStream("/path/to/truststore.jks")) {
//            trustStore.load(trustStoreStream, "password".toCharArray());
//        }
//
//        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
//        sslContextBuilder.loadKeyMaterial(keyStore, "password".toCharArray());
//        sslContextBuilder.loadTrustMaterial(trustStore, null);
//        SSLContext sslContext = sslContextBuilder.build();
//
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
//        keyManagerFactory.init(keyStore, "password".toCharArray());
//        sslContext.init(keyManagerFactory.getKeyManagers(), null, new java.security.SecureRandom());
//
//        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
//
//        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(socketFactory).build();
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
//
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//
//        WebSocketClient client = new StandardWebSocketClient(requestFactory);
//        this.stompClient = new WebSocketStompClient(client);
//        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
}
