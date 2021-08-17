package JavaUtils.HTTPManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Manager for {@link Connection}s
 *
 * @author Max
 * @see JavaUtils
 * @see Connection
 */
public class InetManager {

    static CookieManager cm;
    private static boolean set = false;

    /**
     * Opens {@link URLConnection}s
     *
     * @param url The URL of the Connection
     * @return The URLConnection
     * @throws IOException Occurs when the Host can't be found
     */
    private static URLConnection open(URL url) throws IOException {
        return url.openConnection();
    }

    private static void init() {
        if (set) return;
        cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cm);
        set = true;
    }

    /**
     * Opens an Connection with a URL
     *
     * @param url The URL of the Connection
     * @return The Connection of the URL
     * @throws IOException Occurs when the Host can't be found
     */
    public static Connection openConnection(URL url) throws IOException {
        init();
        return new Connection(open(url));
    }

    /**
     * Opens a Connection with a URL (z.B. http://www.google.de/)
     *
     * @param url The URL in the form of a String (z.B. http://www.google.de/)
     * @return The Connection of the URL
     * @throws MalformedURLException Occurs when the URL is malformed
     * @throws IOException           Occurs when the Host can't be found
     */
    public static Connection openConnection(String url) throws IOException {
        init();
        return new Connection(url);
    }

    /**
     * Gets the URL of an Connection
     *
     * @param c The Connection
     * @return The URL of the Connection
     */
    public static URL getURL(Connection c) {
        return c.getURL();
    }


}
