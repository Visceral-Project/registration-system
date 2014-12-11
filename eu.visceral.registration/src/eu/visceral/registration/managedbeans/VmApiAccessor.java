package eu.visceral.registration.managedbeans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "vmAccessor")
@RequestScoped
public class VmApiAccessor {
    public String callApi(String endpoint, String VmId) throws Exception {

        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                // System.out.println("Warning: URL Host: " + urlHostName +
                // " vs. " + session.getPeerHost());
                return true;
            }
        };

        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        String url;
        if (endpoint.contains("clu")) {
            url = "https://visceral.eu:8181/VisceralVMService/resources/vm/" + endpoint + "/" + VmId;
        } else {
            url = "https://visceral.eu:8181/VisceralVMService/resources/vm/" + endpoint + "/" + VmId.replaceFirst(".cloudapp.net", "");
        }
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        // Used BASE64 encoding of "visceral-su:pwd4$visceral$SU" for
        // authentication
        con.setRequestProperty("Authorization", "Basic dmlzY2VyYWwtc3U6cHdkNCR2aXNjZXJhbCRTVQ==");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    // Just add these two functions in your program
    public static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
    }

    private static void trustAllHttpsCertificates() throws Exception {
        // Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts =

        new javax.net.ssl.TrustManager[1];

        javax.net.ssl.TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc =

        javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

        sc.getSocketFactory());
    }
}