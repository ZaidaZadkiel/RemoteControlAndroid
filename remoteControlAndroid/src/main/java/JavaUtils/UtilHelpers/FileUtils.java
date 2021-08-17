package JavaUtils.UtilHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

public class FileUtils {

    public static String readAll(File f) throws IOException {
        return new String(readAllBytes(f));
    }

    public static byte[] readAllBytes(File f) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        return data;
    }

    public static void deleteDirectory(File directory) {
        if (!directory.exists()) return;
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                deleteDirectory(f);
            } else {
                f.delete();
            }
        }
        directory.delete();
    }

    public static BufferedReader initializeCipherFileReader(File f, String pw, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            FileNotFoundException, InvalidKeySpecException {
        while (pw.length() < 31) {
            pw = pw + pw;
        }
        pw = pw.substring(0, 31);
        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        SecretKeySpec keyspec = new SecretKeySpec(pw.getBytes(), algorithm + "/ECB/PKCS5Padding");
        SecretKeyFactory fa = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = fa.generateSecret(keyspec);
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream cis = new CipherInputStream(new FileInputStream(f), cipher);
        return new BufferedReader(new InputStreamReader(cis));
    }

    public static PrintWriter initializeCipherFileWriter(File f, String pw, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            FileNotFoundException, InvalidKeySpecException {
        while (pw.length() < 31) {
            pw = pw + pw;
        }
        pw = pw.substring(0, 31);
        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        SecretKeySpec keyspec = new SecretKeySpec(pw.getBytes(), algorithm + "/ECB/PKCS5Padding");
        SecretKeyFactory fa = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = fa.generateSecret(keyspec);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        OutputStream cis = new CipherOutputStream(new FileOutputStream(f), cipher);
        return new PrintWriter(new OutputStreamWriter(cis));
    }

    public static String readAll(InputStream fileInputStream) throws IOException {
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[1024];
        int read = 0;
        while ((read = fileInputStream.read(data)) != -1) {
            string.append(new String(copyOnly(data, read)));
        }
        fileInputStream.close();
        return string.toString();
    }

    private static byte[] copyOnly(byte[] data, int read) {
        byte[] r = new byte[read];
        for (int i = 0; i < read; i++) {
            r[i] = data[i];
        }
        return r;
    }
}
