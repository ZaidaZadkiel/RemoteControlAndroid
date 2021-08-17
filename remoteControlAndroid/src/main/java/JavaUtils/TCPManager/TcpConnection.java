/**
 *
 */
package JavaUtils.TCPManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The Connection from a Client to a Server
 * or the Connection from a Server to a Client
 *
 * @author Max
 * @see TCPManager
 * @see TcpServer
 */
public class TcpConnection {

    protected BufferedOutputStream out;
    protected BufferedInputStream in;
    StringBuffer written = new StringBuffer();
    Socket s;
    PrintWriter pw;
    BufferedReader br;
    int index;
    boolean connected = true;

    /**
     * @param accept
     * @throws IOException
     */
    protected TcpConnection(Socket accept, int index) throws IOException {
        s = accept;
        out = new BufferedOutputStream(s.getOutputStream());
        in = new BufferedInputStream(s.getInputStream());
        pw = new PrintWriter(s.getOutputStream());
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.index = index;
    }

    protected TcpConnection(InputStream in, OutputStream out, Socket s, int index) {
        this.s = s;
        this.out = new BufferedOutputStream(out);
        this.in = new BufferedInputStream(in);
        pw = new PrintWriter(out);
        br = new BufferedReader(new InputStreamReader(in));
        this.index = index;
    }

    /**
     * Reads an Line from the Connection
     *
     * @return The read Line
     * @throws IOException
     */
    public String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            TCPManager.removeConnection(index);
        }
        return null;
    }

    public boolean isClosed() {
        return s.isClosed();
    }

    /**
     * Writes an Message to the Connection
     *
     * @param message The Line to write
     * @return If the Writing was successfull
     */
    public boolean writeLine(String message) {
        try {
            pw.println(message);
            written.append(message + "\n");
            if (written.length() > 200) written.delete(0, 50);
            pw.flush();
            if (pw.checkError()) throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            TCPManager.removeConnection(index);
            this.disconnect();
            return false;
        }
        return true;
    }

    public StringBuffer getWritten() {
        return written;
    }

    /**
     * Writes an Message to the Connection
     *
     * @param message   The Line to write
     * @param autoFlush If the Writer should be flushed automatically
     * @return If the Writing was successfull
     */
    public boolean writeString(String message, boolean autoFlush) {
        try {
            pw.print(message);
            written.append(message);
            if (written.length() > 200) written.delete(0, 50);
            if (autoFlush) pw.flush();
        } catch (Exception e) {
            TCPManager.removeConnection(index);
            this.disconnect();
            return false;
        }
        return true;
    }

    /**
     * @return If the TcpConnection is connected
     */
    public boolean isConnected() {
        return s.isConnected();
    }

    /**
     * Flushes the PrintWriter [Only Needed after an write(char)]
     */
    public void flush() {
        pw.flush();
    }

    /**
     * Disconnects
     *
     * @return If the Disconnect was successfull
     */
    public boolean disconnect() {
        try {
            connected = false;
            s.close();
            TCPManager.removeConnection(index);
        } catch (IOException e) {
            TCPManager.removeConnection(index);
            return false;
        }
        return true;
    }

    /**
     * Reads a single Character
     *
     * @return the single Character as Integer
     */
    @Deprecated
    public int read() {
        try {
            return br.read();
        } catch (IOException e) {
            TCPManager.removeConnection(index);
        }
        return -1;
    }

    /**
     * Writes an single Char to the Connection [Needs an Flush]
     *
     * @param c the Written Char
     * @return If the Write was successfull
     */
    @Deprecated
    public boolean write(char c) {
        try {
            pw.write(c);
            written.append(c);
            if (written.length() > 200) written.delete(0, 50);
        } catch (Exception e) {
            TCPManager.removeConnection(index);
            return false;
        }
        return true;
    }

    public String readAll() {
        String back = "";
        String line = "";
        while ((line = readLine()) != null) {
            back = back + System.getProperty("line.seperator") + line;
        }
        return back;
    }

    public boolean writeBytes(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (Exception e) {
            TCPManager.removeConnection(index);
            return false;
        }
        return true;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        in.read(bytes);
        return bytes;
    }

    public XmlTcpConnection toXmlConnection() throws IOException {
        return new XmlTcpConnection(s, index);
    }

    public Socket getSocket() {
        return s;
    }

    public int getIndex() {
        return index;
    }
}
