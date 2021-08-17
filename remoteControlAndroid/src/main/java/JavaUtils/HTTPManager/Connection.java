package JavaUtils.HTTPManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Creates an URLConnection to a Host and let you make an Get or Post Command
 *
 * @author Max
 * @version 1.0
 * @see InetManager
 */
public class Connection {

    boolean connected = false;
    HttpURLConnection conn;
    DataOutputStream wr;
    BufferedReader br;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    private String content = "text/plain";

    /**
     * Initialising the Class Connection with a already open URLConnection
     *
     * @param conn The Already open URLConnection
     */
    public Connection(URLConnection conn) {
        this.conn = (HttpURLConnection) conn;
    }

    /**
     * Initialising the Class Connection with a URL
     *
     * @param url The URL to where the Connection should be established
     * @throws IOException Occurs when the Host can't be found
     */
    public Connection(URL url) throws IOException {
        conn = (HttpURLConnection) url.openConnection();
    }

    /**
     * Initialising the Class Connection with a URL
     *
     * @param url The URL to where the Connection should be established
     * @throws IOException           Occurs when the Host can't be found
     * @throws MalformedURLException Occurs when the given URL is malformed (Example:
     *                               "hppt:www.google.ocm", Right: "http://www.google.com")
     */
    public Connection(String url) throws IOException {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        conn = (HttpURLConnection) new URL(url).openConnection();
    }

    /**
     * @return The URL of the Connection
     */
    public URL getURL() {
        return conn.getURL();
    }

    /**
     * Initialising an Post Command to the Host
     *
     * @param requestPropertys The Request Propertys for the Post Command (can be empty)
     * @throws IOException Occurs when the Host can't be found
     */
    public Connection initPost(HashMap<String, String> requestPropertys)
            throws IOException {
        int index = 1;
        String sb = "";
        for (String s : requestPropertys.keySet()) {
            if (index != requestPropertys.size()) {
                sb = sb + s + "="
                        + URLEncoder.encode(requestPropertys.get(s), "UTF-8")
                        + "&";
            } else {
                sb = sb + s + "="
                        + URLEncoder.encode(requestPropertys.get(s), "UTF-8");
            }
            index++;
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", content + "; charset=UTF-8");
        conn.setRequestProperty("User-Agent", userAgent);
        if (!requestPropertys.isEmpty())
            conn.setRequestProperty("Content-Length", String.valueOf(sb.length()));
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(sb);
        wr.flush();
        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        connected = true;
        return this;
    }

    /**
     * Writes the Output to the Host
     *
     * @param output The String that would be written to the Host (mostly in the
     *               Format "variable1=value1&variable2=value2"
     * @return The String that come from the Host
     * @throws IOException Occurs when the Host can't be found
     */
    private StringBuffer writeToHost(String output) throws IOException {
        wr.writeBytes("&" + output);
        wr.flush();
        wr.close();
        StringBuffer sb = new StringBuffer();
        String input = "";
        while ((input = br.readLine()) != null) {
            sb.append(input + "\n");
        }
        sb.substring(0, sb.length() - 1);
        return sb;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Initialising a Get Command
     *
     * @param needToWrite      If something must be written to the Host after this
     *                         initialisiation
     * @param requestPropertys The Request Propertys for this Command (can be empty)
     * @throws IOException Occurs when the Host can't be found
     */
    public Connection initGet(boolean needToWrite,
                              HashMap<String, String> requestPropertys) throws IOException {
        if (!requestPropertys.isEmpty())
            conn = (HttpURLConnection) new URL(
                    conn.getURL()
                            + join(requestPropertys.keySet(),
                            requestPropertys.values()))
                    .openConnection();
        conn.setDoOutput(needToWrite);
        conn.setRequestProperty("Content-Type", content + "; charset=UTF-8");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestMethod("GET");
        if (needToWrite)
            wr = new DataOutputStream(conn.getOutputStream());
        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        conn.connect();
        connected = true;
        return this;
    }

    private String join(Set<String> set, Collection<String> collection) {
        String s = "?";
        Object[] a = set.toArray();
        Object[] b = collection.toArray();
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                s = s + "&";
            s = s + a[i] + "=" + b[i];
        }

        return s;
    }

    /**
     * Gets something from the Host
     *
     * @param output The String that would be written to the Host (mostly in the
     *               Format "variable1=value1&variable2=value2", needs an '&' in
     *               front if in the initialising were Request Propertys as well)
     * @return The String that comes from the Host
     * @throws IOException Occurs when the Host can't be found
     */
    public String get(String output) throws IOException {
        return new String(writeToHost(output));
    }

    /**
     * Gets something from the Host without extra Request Propertys
     *
     * @return The String that comes from the Host
     * @throws IOException Occurs when the Host can't be found
     */
    public String get() throws IOException {
        if (br == null) {
            initPost(new HashMap<String, String>());
        }
        StringBuffer sb = new StringBuffer();
        String input = "";
        while ((input = br.readLine()) != null) {
            sb.append(input + "\n");
        }
        if (sb.length() != 0) sb.substring(0, sb.length() - 1);
        if (conn.getResponseCode() == 301) {
            return new Connection(conn.getHeaderField("Location")).initGet(false, new HashMap()).get();
        }
        return new String(sb);
    }

    /**
     * Posts something to the Host
     *
     * @return The String that comes from the Host
     * @throws IOException Occurs when the Host can't be found
     */
    public String post() throws IOException {
        wr.close();
        StringBuffer sb = new StringBuffer();
        String input = "";
        while ((input = br.readLine()) != null) {
            sb.append(input + "\n");
        }
        sb.substring(0, sb.length() - 1);
        return new String(sb);
    }

    /**
     * Posts something to the Host
     *
     * @param output The String that would be written to the Host (mostly in the
     *               Format "variable1=value1&variable2=value2", needs an '&' in
     *               front if in the initialising were Request Propertys as well)
     * @return The String that comes from the Host
     * @throws IOException Occurs when the Host can't be found
     */
    public String post(String output) throws IOException {
        return new String(writeToHost(output));
    }

    public void addCookie(URI domain, String name, String value) {
        InetManager.cm.getCookieStore().add(domain, new HttpCookie(name, value));
    }

    public void setContentType(String type) {
        content = type;
    }

}
