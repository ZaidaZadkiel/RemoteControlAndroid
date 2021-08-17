/**
 *
 */
package JavaUtils.TCPManager;

/**
 * @author Max
 *         An Listener for a Server
 */
public interface TcpServerListener {

    /**
     * Occures when a Client connect to your Server
     *
     * @param connect The Connecting Client
     * @param index   The Index of the Client
     * @return If the Client can connect
     */
    boolean clientConnect(TcpConnection connect, int index);

}
