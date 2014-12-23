package cs.hm.edu.sisy.chat.tools;

import android.annotation.SuppressLint;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class EasySSLSocketFactory implements SocketFactory, LayeredSocketFactory {

private SSLContext sslcontext = null;

@SuppressLint("TrulyRandom")
private static SSLContext createEasySSLContext() throws IOException {
    try {
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");  //TLS
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            return sc;
        } catch (Exception e) {

        }
        return null;
    } catch (Exception e) {
        throw new IOException(e.getMessage());
    }
}

private SSLContext getSSLContext() throws IOException {
    if (this.sslcontext == null) {
        this.sslcontext = createEasySSLContext();
    }
    return this.sslcontext;
}

/**
 * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket, java.lang.String, int,
 *      java.net.InetAddress, int, org.apache.http.params.HttpParams)
 */
public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort,
        HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
    int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
    int soTimeout = HttpConnectionParams.getSoTimeout(params);
    InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
    SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

    if ((localAddress != null) || (localPort > 0)) {
        // we need to bind explicitly
        if (localPort < 0) {
            localPort = 0; // indicates "any"
        }
        InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
        sslsock.bind(isa);
    }

    sslsock.connect(remoteAddress, connTimeout);
    sslsock.setSoTimeout(soTimeout);
    return sslsock;

}

/**
 * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
 */
public Socket createSocket() throws IOException {
    return getSSLContext().getSocketFactory().createSocket();
}

/**
 * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
 */
public boolean isSecure(Socket socket) throws IllegalArgumentException {
    return true;
}

/**
 * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket, java.lang.String, int,
 *      boolean)
 */
public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
        UnknownHostException {
    return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
}

// -------------------------------------------------------------------
// javadoc in org.apache.http.conn.scheme.SocketFactory says :
// Both Object.equals() and Object.hashCode() must be overridden
// for the correct operation of some connection managers
// -------------------------------------------------------------------

public boolean equals(Object obj) {
    return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));
}

public int hashCode() {
    return EasySSLSocketFactory.class.hashCode();
}

public static HttpClient getNewHttpClient() {
    try {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        return new DefaultHttpClient(ccm, params);
    } catch (Exception e) {
        return new DefaultHttpClient();
    }
}
}