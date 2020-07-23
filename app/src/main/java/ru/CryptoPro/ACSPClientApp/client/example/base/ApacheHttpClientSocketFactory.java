/**
 * $RCSfileApacheHttpClientSocketFactory.java,v $
 * version $Revision: 36379 $
 * created 23.12.2019 14:46 by afevma
 * last modified $Date: 2012-05-30 12:19:27 +0400 (Ср, 30 май 2012) $ by $Author: afevma $
 * (C) ООО Крипто-Про 2004-2019.
 * <p/>
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 * <p/>
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package ru.CryptoPro.ACSPClientApp.client.example.base;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Класс ApacheHttpClientSocketFactory в основном копирует
 * класс {@link org.apache.http.conn.ssl.SSLSocketFactory},
 * но при этом использует явный ГОСТ SSLContext, а не *Factory
 * по умолчанию, чтобы не обращаться к алгоритмам по
 * умолчанию (System.getProperty(javax.net.*) и Security.setProperty(ssl.*)).
 *
 * См.
 * \org\apache\httpcomponents\httpclient\4.2\httpclient-4.2-sources.jar
 * \org\apache\http\conn\ssl\SSLSocketFactory.java
 *
 * @author Copyright 2004-2019 Crypto-Pro. All rights reserved.
 * @.Version
 * @see ru.CryptoPro.JCPRequest.client.Apache4SSLSocketFactoryImpl
 */
public class ApacheHttpClientSocketFactory implements SchemeSocketFactory,
        LayeredSchemeSocketFactory, LayeredSocketFactory {

    private final HostNameResolver nameResolver;
    private final SSLSocketFactory delegate;
    private volatile X509HostnameVerifier hostnameVerifier;

    /**
     * Конструктор.
     *
     * @param delegate Фабрика сокетов.
     * @param hostnameVerifier Проверка хоста.
     */
    public ApacheHttpClientSocketFactory(final SSLSocketFactory delegate,
        final X509HostnameVerifier hostnameVerifier) {
        this.delegate = delegate;
        this.nameResolver = null;
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        if ( hostnameVerifier == null ) {
            throw new IllegalArgumentException("Hostname verifier may not be null");
        }
        this.hostnameVerifier = hostnameVerifier;
    }

    @Override
    public Socket createSocket() throws IOException {
        return delegate.createSocket();
    }

    @Override
    public Socket connectSocket(Socket socket, String host, int port,
                                InetAddress localAddress, int localPort, HttpParams params)
        throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            // we need to bind explicitly
            if (localPort < 0) {
                localPort = 0; // indicates "any"
            }
            local = new InetSocketAddress(localAddress, localPort);
        }
        InetAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = this.nameResolver.resolve(host);
        } else {
            remoteAddress = InetAddress.getByName(host);
        }
        InetSocketAddress remote = new HttpInetSocketAddress(new HttpHost(host, port), remoteAddress, port);
        return connectSocket(socket, remote, local, params);
    }

    @Override
    public Socket createSocket(HttpParams params) throws IOException {
        SSLSocket sock = (SSLSocket) this.delegate.createSocket();
        return sock;
    }

    @Override
    public Socket connectSocket(
            final Socket socket,
            final InetSocketAddress remoteAddress,
            final InetSocketAddress localAddress,
            final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
//        if (remoteAddress == null) {
//            throw new IllegalArgumentException("Remote address may not be null");
//        }
//        if (params == null) {
//            throw new IllegalArgumentException("HTTP parameters may not be null");
//        }
//        Socket sock = socket != null ? socket : this.delegate.createSocket();
//        if (localAddress != null) {
//            sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
//            sock.bind(localAddress);
//        }
//
//        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
//        int soTimeout = HttpConnectionParams.getSoTimeout(params);
//
//        try {
//            sock.setSoTimeout(soTimeout);
//            sock.connect(remoteAddress, connTimeout);
//        } catch (SocketTimeoutException ex) {
//            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
//        }
//
//        String hostname;
//        if (remoteAddress instanceof HttpInetSocketAddress) {
//            hostname = ((HttpInetSocketAddress) remoteAddress).getHttpHost().getHostName();
//        } else {
//            hostname = remoteAddress.getHostName();
//        }
//
//        SSLSocket sslsock;
//        // Setup SSL layering if necessary
//        if (sock instanceof SSLSocket) {
//            sslsock = (SSLSocket) sock;
//        } else {
//            int port = remoteAddress.getPort();
//            sslsock = (SSLSocket) this.delegate.createSocket(sock, hostname, port, true);
//        }
//        if (this.hostnameVerifier != null) {
//            try {
//                this.hostnameVerifier.verify(hostname, sslsock);
//                // verifyHostName() didn't blowup - good!
//            } catch (IOException iox) {
//                // close the socket before re-throwing the exception
//                try { sslsock.close(); } catch (Exception x) { /*ignore*/ }
//                throw iox;
//            }
//        }
        return null;
    }

    @Override
    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        // This instanceof check is in line with createSocket() above.
        if (!(sock instanceof SSLSocket)) {
            throw new IllegalArgumentException("Socket not created by this factory");
        }
        // This check is performed last since it calls the argument object.
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed");
        }
        return true;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) this.delegate.createSocket(
                socket,
                host,
                port,
                autoClose
        );
        if (this.hostnameVerifier != null) {
            this.hostnameVerifier.verify(host, sslSocket);
        }
        // verifyHostName() didn't blowup - good!
        return sslSocket;
    }

    @Override
    public Socket createLayeredSocket(Socket socket, String host,
                                      int port, boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) this.delegate.createSocket(
                socket,
                host,
                port,
                autoClose
        );
        if (this.hostnameVerifier != null) {
            this.hostnameVerifier.verify(host, sslSocket);
        }
        // verifyHostName() didn't blowup - good!
        return sslSocket;
    }

}
