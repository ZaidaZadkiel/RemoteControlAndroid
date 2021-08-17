package JavaUtils.XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import JavaUtils.HTTPManager.Connection;
import JavaUtils.HTTPManager.InetManager;
import JavaUtils.Parser.Parser;
import JavaUtils.Parser.Section;
import JavaUtils.UtilHelpers.FileUtils;

public class XmlParser extends XmlElement implements Parser {

    private static Process p;

    public XmlParser(String xml) {
        super("root", null, xml);
    }

    public XmlParser(File f) throws IOException {
        super("root", null, FileUtils.readAll(f));
    }

    public XmlParser(URL url) throws IOException {
        super("root", null, InetManager.openConnection(url)
                .initGet(false, new HashMap<String, String>()).get());
    }

    public XmlParser(Connection c) throws IOException {
        super("root", null, c.get());
    }

    public XmlParser(String xml, XmlParserEngine engine) {
        super("root", null, xml, engine);
    }

    public XmlParser(File f, XmlParserEngine engine) throws IOException {
        super("root", null, FileUtils.readAll(f), engine);
    }

    public XmlParser(URL url, XmlParserEngine engine) throws IOException {
        super("root", null,
                InetManager.openConnection(url)
                        .initGet(false, new HashMap<String, String>()).get(),
                engine);
    }

    public XmlParser(Connection c, XmlParserEngine engine) throws IOException {
        super("root", null, c.get(), engine);
    }

    /**
     * Blocks current Thread
     *
     * @param r
     * @throws IOException
     */
    public XmlParser(Runtime r, File executable, File currDir, String... arguments) throws IOException {
        super("root", null, startProgram(r, executable, currDir, arguments), XmlParserEngine.DEPRECATED);
    }

    /**
     * Only Available if you initialized this XmlParser with a Program Only one
     * Process is saved for Return
     */
    @Deprecated
    public static Process getStartedProgram() {
        return p;
    }

    private static String startProgram(Runtime r, File executableFile, File currDir,
                                       String... arguments) throws IOException {
        String[] cmdArray = new String[1 + arguments.length];
        cmdArray[0] = executableFile.getAbsolutePath();
        int i = 1;
        for (String s : arguments) {
            cmdArray[i] = s;
            i++;
        }
        Process p = r.exec(cmdArray, null, currDir);
        XmlParser.p = p;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        String xml = "";
        int signs = 0;
        boolean firstSign = false;
        while ((line = br.readLine()) != null) {
            xml += line;
            String[] d = line.split("<");
            for (String s : d) {
                if (!s.startsWith("/") && !s.startsWith("?")) {
                    signs++;
                    firstSign = true;
                }
            }
            signs -= line.split("</").length - 1;
            if (signs == 0 && firstSign) {
                break;
            }
        }
        return xml;
    }

    @Deprecated
    @Override
    public Section getSection(String name) throws NoElementFoundException {
        return super.getSections(name).get(0);
    }

}
