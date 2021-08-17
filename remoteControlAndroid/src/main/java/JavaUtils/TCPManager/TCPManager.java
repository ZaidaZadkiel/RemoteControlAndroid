package JavaUtils.TCPManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Manages the TCP Connections and an TCP Server
 *
 * @author Max
 * @see TcpServer
 * @see TcpConnection
 */
public class TCPManager {

    static ArrayList<TcpConnection> connections = new ArrayList<TcpConnection>();
    private static boolean server = false;
    private static ArrayList<Thread> lookForClients;
    private static Thread lookForInput;
    private static TcpClientListener clientListener;
    private static SSLSocketFactory factory;

    public TCPManager() {
    }

    /**
     * Starts an Server
     *
     * @param port                        The Port where the Server should listen on
     * @param lookForClientsAutomatically If the Manager should automatically Look for Clients
     * @param listener                    An TcpServerListener [Only Needed if
     *                                    {@code lookForClientsAutomatically} is true]
     * @return
     * @throws IOException
     */
    public static TcpServer startServer(int port, boolean lookForClientsAutomatically,
                                        TcpServerListener listener) throws IOException {
        server = true;
        TcpServer tcpServer = null;
        tcpServer = new TcpServer(port);
        if (lookForClientsAutomatically) {
            lookForClientsAutomatically(tcpServer, listener);
        }
        return tcpServer;
    }

    /**
     * Starts an Server
     *
     * @param port                        The Port where the Server should listen on
     * @param lookForClientsAutomatically If the Manager should automatically Look for Clients
     * @param listener                    An TcpServerListener [Only Needed if
     *                                    {@code lookForClientsAutomatically} is true]
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static TcpServer startServer(int port, boolean lookForClientsAutomatically,
                                        TcpServerListener listener, TcpServerMode mode)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, KeyManagementException {
        if (mode.getEncryption().equals(TcpServerMode.NO_ENCRYPTION.getEncryption())) {
            return startServer(port, lookForClientsAutomatically, listener);
        } else if (mode.getEncryption().equals(TcpServerMode.SSL_MODE.getEncryption())) {
            KeyStore k = KeyStore.getInstance("KJS");
            k.load(new FileInputStream(mode.getCertificateFile()), mode.getCertificatePassword());
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(k);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            SSLServerSocketFactory sslFactory = ctx.getServerSocketFactory();
            server = true;
            TcpServer tcpServer = null;
            try {
                tcpServer = new TcpServer(port, sslFactory);
            } catch (IOException e) {
                server = false;
                return null;
            }
            if (lookForClientsAutomatically) {
                lookForClientsAutomatically(tcpServer, listener);
            }
            return tcpServer;
        }
        return null;
    }

    /**
     * Looks automatically for New Clients Connecting
     *
     * @param tcpServer
     * @param listener  An TcpServerListener
     */
    public static void lookForClientsAutomatically(final TcpServer tcpServer,
                                                   final TcpServerListener listener) {
        if (lookForClients == null) lookForClients = new ArrayList<Thread>();
        Thread l = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (tcpServer.isOnline()) {
                        final TcpConnection s = tcpServer.lookForClients();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean allow = true;
                                if (listener != null)
                                    allow = listener.clientConnect(s, connections.size());
                                connections.add(s);
                                if (!allow) s.disconnect();
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        l.start();
        lookForClients.add(l);
    }

    /**
     * Stops the TCP Server
     *
     * @return If the Server was successfully stopped
     */
    public static boolean stopServer(TcpServer tcpServer) {
        return tcpServer.stopServer();
    }

    /**
     * Connects to an URL on the given Port
     *
     * @param url             The Url where the TCP Server is
     * @param port            The Port where the Server listens
     * @param automaticListen If the Manager should autmatic Listen for incoming Messages
     *                        (Recommended)
     * @param listener        An TcpClientListener [Only Needed if {@code automaticListen}
     *                        is true]
     * @return The Connected Connection
     * @throws UnknownHostException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    public static TcpConnection connect(String url, int port, boolean automaticListen,
                                        TcpClientListener listener, TcpConnectionMode mode)
            throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, KeyManagementException {
        Socket s = null;
        if (mode == TcpConnectionMode.NO_ENCRYPTION) {
            s = new Socket(url, port);
        } else if (mode == TcpConnectionMode.SSL_ENCRYPTION) {
            KeyStore k = KeyStore.getInstance("KJS");
            k.load(new FileInputStream(mode.getCertificateFile()), mode.getCertificatePassword());
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(k);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            SSLSocketFactory sslFactory = ctx.getSocketFactory();
            sslFactory.createSocket(url, port);
        }
        TcpConnection t = new TcpConnection(s, -1);
        if (automaticListen) automaticClientListen(t, listener);
        return t;
    }

    /**
     * Connects to an URL on the given Port
     *
     * @param url             The Url where the TCP Server is
     * @param port            The Port where the Server listens
     * @param automaticListen If the Manager should autmatic Listen for incoming Messages
     *                        (Recommended)
     * @param listener        An TcpClientListener [Only Needed if {@code automaticListen}
     *                        is true]
     * @return The Connected Connection
     * @throws UnknownHostException
     * @throws IOException
     */
    public static TcpConnection connect(String url, int port, boolean automaticListen,
                                        TcpClientListener listener) throws IOException {
        Socket s = new Socket(url, port);
        TcpConnection t = new TcpConnection(s, -1);
        if (automaticListen) automaticClientListen(t, listener);
        return t;
    }

    /**
     * Makes an Automatic Listener
     *
     * @param tcp      The TCP Connection for the Automatic Listening
     * @param listener An TcpClientListener
     */
    private static void automaticClientListen(final TcpConnection tcp, TcpClientListener listener) {
        clientListener = listener;
        lookForInput = new Thread(new Runnable() {
            @Override
            public void run() {
                String s = "";
                while (tcp.isConnected()) {
                    s = tcp.readLine();
                    clientListener.input(s, tcp);
                }
            }
        });
        lookForInput.start();
    }

    /**
     * Removes a Connection from the Manager
     *
     * @param id The ID of the Connection
     */
    public static void removeConnection(int id) {
        if (id == -1 || connections.isEmpty()) return;
        try {
            connections.remove(id);
        } catch (Exception e) {
        }
    }

    public static XmlTcpConnection connectXml(String string, Integer valueOf, boolean b,
                                              TcpClientListener listener) throws IOException {
        XmlTcpConnection t = new XmlTcpConnection(new Socket(string, valueOf), -1);
        if (b) automaticClientListen(t, listener);
        return t;
    }

    /**
     * Returns the Connection with the given ID
     *
     * @param id The Index of the Connection
     * @return The Connection with the given ID
     */
    public TcpConnection getConnection(int id) {
        return connections.get(id);
    }
}
