package com.processapi.rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Component
public class CertificateLogger implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        logCertificateInfo(chain, "Client");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        logCertificateInfo(chain, "Server");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private void logCertificateInfo(X509Certificate[] chain, String type) {
        if (chain != null && chain.length > 0) {
            X509Certificate cert = chain[0];
            log.info("{} Certificate Info:", type);
            log.info("  Subject: {}", cert.getSubjectDN());
            log.info("  Issuer: {}", cert.getIssuerDN());
            log.info("  Valid From: {}", cert.getNotBefore());
            log.info("  Valid Until: {}", cert.getNotAfter());
        }
    }

    public void logCertificates(X509TrustManager trustManager) {
        if (trustManager == null) {
            log.warn("No trust manager configured for certificate logging");
            return;
        }

        X509Certificate[] acceptedIssuers = trustManager.getAcceptedIssuers();
        if (acceptedIssuers == null || acceptedIssuers.length == 0) {
            log.warn("No certificates found in trust manager");
            return;
        }

        log.info("Found {} certificates in trust store", acceptedIssuers.length);
        Arrays.stream(acceptedIssuers).forEach(this::logCertificate);
    }

    private void logCertificate(X509Certificate cert) {
        if (cert == null) {
            log.warn("Certificate is null");
            return;
        }

        log.info("Certificate Details:");
        log.info("Subject: {}", cert.getSubjectX500Principal());
        log.info("Issuer: {}", cert.getIssuerX500Principal());
        log.info("Serial Number: {}", cert.getSerialNumber());
        log.info("Valid From: {}", cert.getNotBefore());
        log.info("Valid Until: {}", cert.getNotAfter());
        log.info("Signature Algorithm: {}", cert.getSigAlgName());
        log.info("Version: {}", cert.getVersion());
        log.info("Public Key Algorithm: {}", cert.getPublicKey().getAlgorithm());
    }
} 