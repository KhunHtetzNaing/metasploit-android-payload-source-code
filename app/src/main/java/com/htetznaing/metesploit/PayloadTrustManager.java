package com.htetznaing.metesploit;

import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PayloadTrustManager
        implements X509TrustManager, HostnameVerifier
{
    private byte[] certHash;

    private PayloadTrustManager(byte[] paramArrayOfByte)
    {
        this.certHash = paramArrayOfByte;
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }

    public static byte[] getCertificateSHA1(X509Certificate paramX509Certificate)
            throws NoSuchAlgorithmException, CertificateEncodingException
    {
        MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-1");
        localMessageDigest.update(paramX509Certificate.getEncoded());
        return localMessageDigest.digest();
    }

    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
            throws CertificateException
    {
        if (this.certHash == null) {
            return;
        }
        if ((paramArrayOfX509Certificate == null) || (paramArrayOfX509Certificate.length < 1)) {
            throw new CertificateException();
        }
        for (X509Certificate localX509Certificate : paramArrayOfX509Certificate) {
            try
            {
                byte[] arrayOfByte = getCertificateSHA1(localX509Certificate);
                if (!Arrays.equals(this.certHash, arrayOfByte)) {
                    throw new CertificateException("Invalid certificate");
                }
            }
            catch (Exception localException)
            {
                throw new CertificateException(localException);
            }
        }
    }

    public boolean verify(String paramString, SSLSession paramSSLSession)
    {
        return true;
    }

    public static void useFor(URLConnection paramURLConnection, byte[] paramArrayOfByte)
            throws Exception
    {
        if ((paramURLConnection instanceof HttpsURLConnection))
        {
            HttpsURLConnection localHttpsURLConnection = (HttpsURLConnection)paramURLConnection;
            PayloadTrustManager localPayloadTrustManager = new PayloadTrustManager(paramArrayOfByte);
            SSLContext localSSLContext = SSLContext.getInstance("SSL");
            localSSLContext.init(null, new TrustManager[] { localPayloadTrustManager }, new SecureRandom());
            localHttpsURLConnection.setSSLSocketFactory(localSSLContext.getSocketFactory());
            localHttpsURLConnection.setHostnameVerifier(localPayloadTrustManager);
        }
    }
}
