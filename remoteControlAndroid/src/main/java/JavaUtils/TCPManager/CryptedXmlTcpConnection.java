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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class CryptedXmlTcpConnection extends XmlTcpConnection {


    public CryptedXmlTcpConnection(TcpConnection connect, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        super(connect.s, connect.index);
        while (password.length() < 31) {
            password = password + password;
        }
        password = password.substring(0, 31);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        SecretKeySpec keyspec = new SecretKeySpec(password.getBytes(), "DES/ECB/PKCS5Padding");
        SecretKeyFactory fa = SecretKeyFactory.getInstance("DES");
        SecretKey key = fa.generateSecret(keyspec);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        OutputStream cos = new CipherOutputStream(super.out, cipher);
        super.out = new BufferedOutputStream(cos);
        super.pw = new PrintWriter(super.out);
        Cipher cipher2 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream cis = new CipherInputStream(super.in, cipher2);
        super.in = new BufferedInputStream(cis);
        super.br = new BufferedReader(new InputStreamReader(cis));
    }

    CryptedXmlTcpConnection(Socket accept, int index, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        super(accept, index);
        while (password.length() < 31) {
            password = password + password;
        }
        password = password.substring(0, 31);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        SecretKeySpec keyspec = new SecretKeySpec(password.getBytes(), "DES/ECB/PKCS5Padding");
        SecretKeyFactory fa = SecretKeyFactory.getInstance("DES");
        SecretKey key = fa.generateSecret(keyspec);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        OutputStream cos = new CipherOutputStream(super.out, cipher);
        super.out = new BufferedOutputStream(cos);
        super.pw = new PrintWriter(super.out);
        Cipher cipher2 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream cis = new CipherInputStream(super.in, cipher2);
        super.in = new BufferedInputStream(cis);
        super.br = new BufferedReader(new InputStreamReader(cis));
    }
}
