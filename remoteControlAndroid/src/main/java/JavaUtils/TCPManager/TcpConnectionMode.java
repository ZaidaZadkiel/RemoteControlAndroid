package JavaUtils.TCPManager;

import java.io.File;

public enum TcpConnectionMode {
    NO_ENCRYPTION, SSL_ENCRYPTION;

    File certificate;
    String password;

    public File getCertificateFile() {
        return certificate;
    }

    public void setCertificate(File certificate, String password) {
        this.certificate = certificate;
        this.password = password;
    }

    public char[] getCertificatePassword() {
        return password.toCharArray();
    }
}
