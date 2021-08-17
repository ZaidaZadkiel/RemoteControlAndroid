package JavaUtils.TCPManager;

import java.io.File;

public class TcpServerMode {
    public static TcpServerMode NO_ENCRYPTION = new TcpServerMode("NO_ENCRYPTION"), SSL_MODE = new TcpServerMode("SSL_MODE");

    File certificate;
    String password;
    int port = 80;
    String enc;

    public TcpServerMode(String string) {
        enc = string;
    }

    public String getEncryption() {
        return enc;
    }

    public File getCertificateFile() {
        return certificate;
    }

    public char[] getCertificatePassword() {
        return password.toCharArray();
    }

    public void setCertificate(File certificate, String password) {
        this.certificate = certificate;
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public TcpServerMode setPort(int port) {
        TcpServerMode m = new TcpServerMode(enc);
        m.port = port;
        return m;
    }
}
