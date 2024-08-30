package com.tanla.template_matching.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticSearchService {

    private static final String SERVER_URL = "https://127.0.0.1:9200";
    private static final String apiKey = "X242VGw1RUItMHZMSW1Eajg3Y2Y6TmZQaUY5amRTOGF5NjJHYVZQZ2tvZw==";

    @Bean
    public RestClient restClient() throws CertificateException, IOException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        Path caCertificatePath = Paths.get("/home/dinesh/docker/es01_certificate.crt");

        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        Certificate trustedCa;

        try (
                InputStream is = Files.newInputStream(caCertificatePath)) {
            trustedCa = factory.generateCertificate(is);
        }

        KeyStore trustStore = KeyStore.getInstance("pkcs12");

        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadTrustMaterial(trustStore, null);
        final SSLContext sslContext = sslContextBuilder.build();

        // Create the low-level client
        RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(SERVER_URL))
                .setDefaultHeaders(new Header[] {
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                }).setHttpClientConfigCallback(new HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setSSLContext(sslContext);
                    }
                });

        return restClientBuilder.build();
    }

    // Create the transport with a Jackson mapper
    @Bean
    public ElasticsearchTransport transport() throws KeyManagementException, CertificateException, KeyStoreException,
            NoSuchAlgorithmException, IOException {
        return new RestClientTransport(
                restClient(), new JacksonJsonpMapper());
    }

    // And create the API client
    @Bean
    public ElasticsearchClient elasticsearchClient() throws KeyManagementException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException, IOException {
        return new ElasticsearchClient(transport());
    }

}
