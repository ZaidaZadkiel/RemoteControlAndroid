/**
 *
 */
package JavaUtils.TCPManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocketFactory;

/**
 * @author Max
 * @see TCPManager
 * @see TcpConnection
 */
public class TcpServer {

    private ServerSocket s;
    private ArrayList<TcpConnection> clients = new ArrayList<TcpConnection>();

    /**
     * @param port The Port where The Server runs on
     * @throws IOException
     */
    TcpServer(int port) throws IOException {
        s = new ServerSocket(port);
    }

    public TcpServer(int port, SSLServerSocketFactory ssl) throws IOException {
        s = ssl.createServerSocket(port);
    }

    /**
     * Looks for a Client that would connect
     *
     * @return The Connecting Client
     * @throws IOException
     */
    public TcpConnection lookForClients() throws IOException {
        TcpConnection con = new TcpConnection(s.accept(), TCPManager.connections.size());
        addClient(con);
        return con;
    }

    void addClient(TcpConnection c) {
        clients.add(c);
    }

    /**
     * Stops the Server
     *
     * @return If the Server is successfully stopped
     */
    public boolean stopServer() {
        try {
            s.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public ArrayList<TcpConnection> getClients() {
        ArrayList<TcpConnection> remove = new ArrayList<TcpConnection>();
        for (TcpConnection c : clients) {
            if (!c.isConnected())
                remove.add(c);
        }
        clients.removeAll(remove);
        return clients;
    }

    public boolean isOnline() {
        return !s.isClosed();
    }

}
