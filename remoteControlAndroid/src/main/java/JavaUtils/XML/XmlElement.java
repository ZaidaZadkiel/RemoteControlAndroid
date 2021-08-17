package JavaUtils.XML;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import JavaUtils.Annotations.NeedElement;
import JavaUtils.Parser.ParseAble;
import JavaUtils.Parser.ParseObject;
import JavaUtils.Parser.ParserManager;
import JavaUtils.Parser.Section;
import JavaUtils.XML.XmlParserEngine.EngineOption;

public class XmlElement implements ParseAble, Section {

    public final static String ATTRIBUTES = "Xml Attribut";
    final static int NAME = 1, VALUE = 2, END = 3;
    String name;
    String value = "";
    LinkedHashMap<String, LinkedList<Object>> values = new LinkedHashMap<String, LinkedList<Object>>();
    LinkedHashMap<String, String> attributes;
    private boolean finished = false;
    private LinkedList<RecommendedParserRunnable> parsingThreads = new LinkedList<RecommendedParserRunnable>();

    XmlElement(String name, String value, LinkedHashMap<String, String> attributes) {
        this.name = name;
        this.value = value;
        this.attributes = attributes;
    }

    public XmlElement(String name, LinkedHashMap<String, String> attributes, String xml,
                      XmlParserEngine engine) {
        this.name = name;
        this.attributes = attributes;
        encode(xml, engine);
    }

    public XmlElement(String name, LinkedHashMap<String, String> attributes, String xml) {
        this.name = name;
        this.attributes = attributes;
        encode(xml, XmlParserEngine.DEPRECATED);
    }

    public XmlElement(ParseAble p) {
        this.name = "root";
        this.attributes = new LinkedHashMap<String, String>();
        HashMap<String, Object> objects = p.parse().getObjects();
        for (String s : objects.keySet()) {
            Object o = objects.get(s);
            LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
            if (s.contains("~~")) {
                String at = s.split("~~")[1];
                s = s.split("~~")[0];
                for (String att : at.split("&&")) {
                    attributes.put(att.split("=")[0], att.split("=")[1]);
                }
            }
            if (o instanceof HashMap) {
                XmlElement x = new XmlElement(s, (HashMap<String, Object>) o, attributes);
                if (values.containsKey(s)) {
                    LinkedList<Object> l = values.get(s);
                    l.add(x);
                    values.put(s, l);
                } else {
                    LinkedList<Object> l = new LinkedList<Object>();
                    l.add(x);
                    values.put(s, l);
                }
            } else {
                if (!((String) o).contains("%$%$")) {
                    XmlElement x = new XmlElement(s, (String) o, attributes);
                    if (values.containsKey(s)) {
                        LinkedList<Object> l = values.get(s);
                        l.add(x);
                        values.put(s, l);
                    } else {
                        LinkedList<Object> l = new LinkedList<Object>();
                        l.add(x);
                        values.put(s, l);
                    }
                } else {
                    for (String o2 : ((String) o).split("%$%$")) {
                        XmlElement x = new XmlElement(s, o2, attributes);
                        if (values.containsKey(s)) {
                            LinkedList<Object> l = values.get(s);
                            l.add(x);
                            values.put(s, l);
                        } else {
                            LinkedList<Object> l = new LinkedList<Object>();
                            l.add(x);
                            values.put(s, l);
                        }
                    }
                }
            }
        }
    }

    public XmlElement(String name, HashMap<String, Object> objects,
                      LinkedHashMap<String, String> atributes) {
        this.name = name;
        this.attributes = atributes;
        for (String s : objects.keySet()) {
            Object o = objects.get(s);
            LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
            if (s.contains("~~")) {
                String at = s.split("~~")[1];
                s = s.split("~~")[0];
                for (String att : at.split("&&")) {
                    attributes.put(att.split("=")[0], att.split("=")[1]);
                }
            }
            if (o instanceof HashMap) {
                XmlElement x = new XmlElement(s, (HashMap<String, Object>) o, attributes);
                if (values.containsKey(s)) {
                    LinkedList<Object> l = values.get(s);
                    l.add(x);
                    values.put(s, l);
                } else {
                    LinkedList<Object> l = new LinkedList<Object>();
                    l.add(x);
                    values.put(s, l);
                }
            } else {
                if (!((String) o).contains("%$%$")) {
                    XmlElement x = new XmlElement(s, (String) o, attributes);
                    if (values.containsKey(s)) {
                        LinkedList<Object> l = values.get(s);
                        l.add(x);
                        values.put(s, l);
                    } else {
                        LinkedList<Object> l = new LinkedList<Object>();
                        l.add(x);
                        values.put(s, l);
                    }
                } else {
                    for (String o2 : ((String) o).split("%$%$")) {
                        XmlElement x = new XmlElement(s, o2, attributes);
                        if (values.containsKey(s)) {
                            LinkedList<Object> l = values.get(s);
                            l.add(x);
                            values.put(s, l);
                        } else {
                            LinkedList<Object> l = new LinkedList<Object>();
                            l.add(x);
                            values.put(s, l);
                        }
                    }
                }
            }
        }
    }

    private XmlElement(String name, String value, HashMap<String, String> attributes) {
        this.name = name;
        this.value = value;
        this.attributes = new LinkedHashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    @NeedElement("Xml Value")
    public String getValue() throws NoElementFoundException {
        if (value != null) {
            return value;
        } else {
            throw new NoElementFoundException("Xml Value");
        }
    }

    @NeedElement(ATTRIBUTES)
    public String getAttribute(String name) throws NoElementFoundException {
        if (attributes != null) {
            if (attributes.isEmpty()) {
                throw new NoElementFoundException(ATTRIBUTES);
            } else if (!attributes.containsKey(name)) {
                return null;
            }
            String a = attributes.get(name);
            a = a.substring(1);
            return a;
        } else {
            throw new NoElementFoundException(ATTRIBUTES);
        }
    }

    private void encode(String xml, XmlParserEngine engine) {
        try {
            if (engine == XmlParserEngine.RECOMMENDED) {
                encode_recommended(xml, engine);
            } else {
                encode_old(xml, engine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encode_recommended(String xml, XmlParserEngine engine) {
        HashMap<EngineOption, Boolean> options = engine.getParserEngineOptions();
        encode_old(xml, XmlParserEngine.DEPRECATED);
        /*
         * if (options.containsKey(EngineOption.MULTI_THREADED) &&
         * options.get(EngineOption.MULTI_THREADED)) { if
         * (getOption(EngineOption.ON_MAIN_THREAD, options, true)) {
         * _encode_recommended(xml, engine, true, 1); } else { new Thread(new
         * RecommendedParserRunnable(xml, engine, this, true,
         * Runtime.getRuntime().availableProcessors() - 2)) .start(); } } else {
         * if (getOption(EngineOption.ON_MAIN_THREAD, options, true)) {
         * _encode_recommended(xml, engine, false, 1); } else { new Thread(new
         * RecommendedParserRunnable(xml, engine, this, false, 0)).start(); } }
         */
    }

    private boolean getOption(EngineOption option, HashMap<EngineOption, Boolean> options,
                              boolean standard) {
        if (options.containsKey(option)) {
            return options.get(option);
        }
        return standard;
    }

    private String checkForXmlDeclarations(String xml, String... declarationstarts) {
        for (int i = 0; i < declarationstarts.length; i = i + 2) {
            if (xml.startsWith(declarationstarts[i])) xml = xml.substring(
                    xml.indexOf(declarationstarts[i + 1]) + declarationstarts[i].length());
        }
        return xml;
    }

    private String removeComments(String xml) {
        if (xml.contains("<!--")) {
            String[] a = xml.split("<!--");
            for (int i = 1; i < a.length; i++) {
                if (a[i].split("-->").length == 2) {
                    a[i] = a[i].split("-->")[1];
                } else {
                    a[i] = "";
                }
            }
            xml = "";
            for (String s : a) {
                xml = xml + s;
            }
            return xml;
        }
        return xml;
    }

    private void _encode_recommended(String xml, XmlParserEngine engine, boolean startNewThreads,
                                     int deep) {
        xml = removeComments(xml);
        String xml2 = xml;
        while (xml2.length() != (xml = checkForXmlDeclarations(xml, "<!", ">", "<?", "?>", " ", " ",
                "\n", "\n")).length())
            xml2 = xml;
        if (!xml.contains("<") || xml.startsWith("</")) {
            value = xml;
            return;
        }
        int CURRENT = NAME;
        String nextName = null;
        LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
        int end = 0;
        while (xml.contains("<")) {
            int index = 0;
            for (RecommendedParserRunnable rc : parsingThreads) {
                if (rc.isFinished()) {
                    parsingThreads.remove(index);
                    ThreadManager.removeRunningThread();
                }
                index++;
            }
            xml2 = xml;
            attributes = new LinkedHashMap<String, String>();
            String nextWith = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">"));
            xml = xml.substring(xml.indexOf(">") + 1);
            boolean value = true;
            if (nextWith.contains(" ")) {
                nextName = nextWith.split(" ", 2)[0];
                String aBuffer = nextWith.split(" ", 2)[1];
                String[] b = aBuffer.split("\"");
                for (int i = 1; i < b.length; i = i + 2) {
                    b[i] = b[i].replaceAll("=", "%2020%");
                }
                aBuffer = "";
                for (String s : b) {
                    aBuffer = aBuffer + s + "\"";
                }
                String[] at = aBuffer.split("=");
                for (int i = 0; i < at.length; i++) {
                    at[i] = at[i].replaceAll("%2020%", "=");
                }
                if (nextWith.endsWith("/")) {
                    value = false;
                }
                if (at.length > 2) {
                    for (int i = 0; i < at.length; i++) {
                        if (at[i].equals("")) break;
                        else {
                            if (at.length - 1 > i + 1) {
                                if (at[i + 1].equals("")) break;
                            }
                        }
                        if (at[i + 1].contains("\"")) {
                            String name = "";
                            if (at[i].contains("\"")) {
                                name = at[i].split("\"")[2];
                                while (name.startsWith(" "))
                                    name = name.substring(1);
                            } else {
                                name = at[i];
                            }
                            attributes.put(name, at[i + 1].split("\"")[1].split("\"")[0]);
                        } else {
                            String name = "";
                            if (at[i].contains("'")) {
                                name = at[i].split("'")[2];
                                while (name.startsWith(" "))
                                    name = name.substring(1);
                            } else {
                                name = at[i];
                            }
                            attributes.put(name, at[i + 1].split("'")[1].split("'")[0]);
                        }
                        if (at.length - 2 == i) break;
                    }
                } else {
                    String name = at[0];
                    String v = at[1].substring(0, at[1].length() - 1);
                    String v2 = v;
                    while (v2.startsWith(" ")) {
                        v2 = v2.substring(1);
                    }
                    while (v2.endsWith(" ")) {
                        v2 = v2.substring(0, v2.length() - 1);
                    }
                    if (v2.startsWith("\"")) {
                        v = v2.substring(1);
                    } else if (v2.startsWith("\'")) {
                        v = v2.substring(1);
                    }
                    v2 = v;
                    if (v2.endsWith("\"")) {
                        v = v2.substring(0, v2.length() - 1);
                    } else if (v2.endsWith("\'")) {
                        v = v2.substring(0, v2.length() - 1);
                    }
                    attributes.put(name, v);
                    if (!value) {
                        if (values.containsKey(nextName)) {
                            LinkedList<Object> ele = values.get(nextName);
                            ele.add(new XmlElement(nextName, "\\\\\\\\", attributes));
                            values.put(nextName, ele);
                        } else {
                            LinkedList<Object> ele = new LinkedList<Object>();
                            ele.add(new XmlElement(nextName, "\\\\\\\\", attributes));
                            values.put(nextName, ele);
                        }
                        continue;
                    }
                }
            } else {
                nextName = nextWith;
            }
            end = 0;
            String b = xml;
            if (nextName.equals("ins")) {
                System.out.println("ins");
            }
            if ((b.indexOf("</" + nextName + ">") > b.indexOf("<" + nextName + ">")
                    || b.indexOf("</" + nextName + ">") > b.indexOf("<" + nextName + " "))
                    && b.indexOf("<" + nextName) > -1) {
                while ((b.indexOf("</" + nextName + ">") > b.indexOf("<" + nextName + ">") || b
                        .indexOf("</" + nextName + ">") > b.indexOf("<" + nextName + " "))
                        && b.indexOf("<" + nextName) > -1) {
                    end = end + b.indexOf("</" + nextName) + ("</" + nextName).length();
                    b = b.substring(b.indexOf("</" + nextName) + ("</" + nextName + ">").length());
                }
                end = end + b.indexOf("</" + nextName + ">");
            } else {
                if (!value) {
                    end = 0;
                } else {
                    end = xml.indexOf("</" + nextName + ">") - 1;
                }
            }
            if (value) {
                LinkedList<Object> ele = null;
                if (values.containsKey(nextName)) {
                    ele = values.get(nextName);
                } else {
                    ele = new LinkedList<Object>();
                }
                if (startNewThreads && deep == 2) {
                    if (ThreadManager.ifCreateNewThread()) {
                        RecommendedParserRunnable rc = new RecommendedParserRunnable(
                                xml.substring(0, end), engine, this, false, nextName,
                                attributes, deep + 1);
                        parsingThreads.add(rc);
                        new Thread(rc).start();
                    } else {
                        ele.add(new XmlElement(nextName, attributes, xml.substring(0, end)));
                        values.put(nextName, ele);
                    }
                } else {
                    ele.add(new XmlElement(nextName, attributes, xml.substring(0, end)));
                    values.put(nextName, ele);
                }
                CURRENT = END;
            }
            if (xml.length() <= end + ("</" + nextName + ">").length()) {
                xml = "";
            } else {
                xml = xml.substring(end + ("</" + nextName + ">").length());
            }
            CURRENT = NAME;
            end = 0;
        }
        while (!parsingThreads.isEmpty()) {
            int index = 0;
            for (RecommendedParserRunnable rc : parsingThreads) {
                if (rc.isFinished()) {
                    ThreadManager.removeRunningThread();
                    parsingThreads.remove(index);
                }
                index++;
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        finished = true;
    }

    private void encode_old(String xml, XmlParserEngine engine) {
        while (xml.contains("<!") || xml.contains("<?")) {
            xml = xml.substring(xml.indexOf(">") + 1);
        }
        if (xml.contains("<")) {
            while (xml.contains("<")) {
                String nextName = "";
                String nextWith = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">"));
                xml = xml.substring(xml.indexOf(">") + 1);
                LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
                if (nextWith.contains(" ")) {
                    nextName = nextWith.split(" ", 2)[0];
                    String aBuffer = nextWith.split(" ", 2)[1];
                    String[] at = aBuffer.split("=");
                    boolean more = true;
                    if (at[at.length - 1].equals("/")) {
                        at[at.length - 1] = "";
                    } else if (at[at.length - 1].endsWith("/")) {
                        more = false;
                    }
                    if (at.length > 2) {
                        for (int i = 0; i < at.length; i++) {
                            if (at[i].equals("")) break;
                            else {
                                if (at.length - 1 > i + 1) {
                                    if (at[i + 1].equals("")) break;
                                }
                            }
                            if (at[i + 1].contains("\"")) {
                                String name = "";
                                if (at[i].contains("\"")) {
                                    name = at[i].split("\"")[2];
                                    while (name.startsWith(" "))
                                        name = name.substring(1);
                                } else {
                                    name = at[i];
                                }
                                attributes.put(name, at[i + 1].split("\"")[1].split("\"")[0]);
                            } else {
                                String name = "";
                                if (at[i].contains("'")) {
                                    name = at[i].split("'")[2];
                                    while (name.startsWith(" "))
                                        name = name.substring(1);
                                } else {
                                    name = at[i];
                                }
                                attributes.put(name, at[i + 1].split("'")[1].split("'")[0]);
                            }
                            if (at.length - 2 == i) break;
                        }
                        if (at[at.length - 1].equals("") || more == false) {
                            if (values.containsKey(nextName)) {
                                LinkedList<Object> ele = values.get(nextName);
                                ele.add(new XmlElement(nextName, "", attributes));
                                values.put(nextName, ele);
                            } else {
                                LinkedList<Object> ele = new LinkedList<Object>();
                                ele.add(new XmlElement(nextName, "", attributes));
                                values.put(nextName, ele);
                            }
                            continue;
                        }
                    } else {
                        attributes.put(at[0], at[1].substring(0, at[1].length() - 1));
                    }
                } else {
                    nextName = nextWith;
                }
                int end = 0;
                String b = xml;
                boolean plus = false;
                if (b.indexOf("</" + nextName + ">") > b.indexOf("<" + nextName)
                        && b.indexOf("<" + nextName) > -1) {
                    while (b.indexOf("</" + nextName + ">") > b.indexOf("<" + nextName)
                            && b.indexOf("<" + nextName) > -1) {
                        end = end + b.indexOf("</" + nextName + ">")
                                + ("</" + nextName + ">").length();
                        b = b.substring(b.indexOf("</" + nextName + ">")
                                + ("</" + nextName + ">").length());
                    }
                    end = end + b.indexOf("</" + nextName + ">");
                } else {
                    plus = true;
                    end = xml.indexOf("</" + nextName + ">");
                }
                if (attributes.containsKey("type")
                        && attributes.get("type").contains("ParseAble")) {
                    String type = attributes.get("type").split("--")[1];
                    if (values.containsKey(nextName)) {
                        LinkedList<Object> ele = values.get(nextName);
                        try {
                            ele.add(ParserManager.getParseAble(type)
                                    .fromParser(new XmlElement(nextName, attributes,
                                            xml.substring(0, end),
                                            XmlParserEngine.DEPRECATED)));
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (NoElementFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        values.put(nextName, ele);
                    } else {
                        LinkedList<Object> ele = new LinkedList<Object>();
                        try {
                            ParseAble p = ParserManager.getParseAble(type);
                            XmlElement xm = new XmlElement(nextName, attributes,
                                    xml.substring(0, end), XmlParserEngine.DEPRECATED);
                            ParseAble parsed = p.fromParser(xm);
                            ele.add(parsed);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (NoElementFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        values.put(nextName, ele);
                    }
                } else {
                    if (values.containsKey(nextName)) {
                        LinkedList<Object> ele = values.get(nextName);
                        ele.add(new XmlElement(nextName, attributes, xml.substring(0, end),
                                XmlParserEngine.DEPRECATED));
                        values.put(nextName, ele);
                    } else {
                        LinkedList<Object> ele = new LinkedList<Object>();
                        ele.add(new XmlElement(nextName, attributes, xml.substring(0, end),
                                XmlParserEngine.DEPRECATED));
                        values.put(nextName, ele);
                    }
                }
                xml = xml.substring(end + ("</" + nextName + ">").length());
            }
        } else {
            value = xml;
        }
    }

    @NeedElement("Xml Section")
    public LinkedList<XmlElement> getSections(String name) throws NoElementFoundException {
        if (values != null) {
            if (!values.containsKey(name)) throw new NoElementFoundException("Xml Section", name);
            LinkedList<XmlElement> el = new LinkedList<XmlElement>();
            for (Object o : values.get(name)) {
                if (o instanceof XmlElement) {
                    el.add((XmlElement) o);
                }
            }
            return el;
        }
        throw new NoElementFoundException("Xml Section");
    }

    @NeedElement("Xml Section")
    public HashMap<String, LinkedList<XmlElement>> listSections() throws NoElementFoundException {
        if (values != null) {
            HashMap<String, LinkedList<XmlElement>> el = new HashMap<String, LinkedList<XmlElement>>();
            for (String s : values.keySet()) {
                el.put(s, getSections(s));
            }
            return el;
        }
        throw new NoElementFoundException("Xml Section");
    }

    public boolean hasSections() {
        return (!values.isEmpty());
    }

    public boolean hasAttributes() {
        return (!attributes.isEmpty());
    }

    @NeedElement("Xml Attribute")
    public HashMap<String, String> listAttributes() throws NoElementFoundException {
        if (attributes != null) {
            return attributes;
        }
        throw new NoElementFoundException("Xml Attribute");
    }

    public String decode() {
        String line_seperator = System.getProperty("line.separator");
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + line_seperator;
        xml = xml + decode(0);
        return xml;
    }

    private String decode(int space) {
        String xml = "";
        String line_seperator = System.getProperty("line.separator");
        if (hasSections()) {
            try {
                for (LinkedList<XmlElement> a : listSections().values()) {
                    for (XmlElement x : a) {
                        if (!x.value.isEmpty()) {
                            if (x.value.contains("%$%$")) {
                                String v = x.value.substring(4);
                                for (String s : v.split("%$%$")) {
                                    String element = "<" + x.name;
                                    if (x.hasAttributes()) {
                                        HashMap<String, String> at = x.listAttributes();
                                        for (String s2 : at.keySet()) {
                                            element = element + " " + s2 + "=\"" + at.get(s2)
                                                    + "\"";
                                        }
                                        element = element + ">";
                                    } else {
                                        element = element + ">";
                                    }
                                    element = element + s + "</" + x.name + ">" + line_seperator;
                                    xml = xml + element;
                                }
                                continue;
                            }
                        }
                        String element = "<" + x.name;
                        if (x.hasAttributes()) {
                            HashMap<String, String> at = x.listAttributes();
                            for (String s : at.keySet()) {
                                element = element + " " + s + "=\"" + at.get(s) + "\"";
                            }
                            if (x.value.equals("\\\\\\\\")) {
                                element = element + " />" + line_seperator;
                            } else {
                                element = element + ">";
                            }
                        } else {
                            if (x.value.equals("\\\\\\\\")) {
                                element = element + " />";
                            } else {
                                element = element + ">";
                            }
                        }
                        if (!x.value.isEmpty()) {
                            if (!x.value.equals("\\\\\\\\")) {
                                element = element + x.decode(2) + "</" + x.name + ">"
                                        + line_seperator;
                            }
                        } else {
                            element = element + line_seperator + x.decode(2) + line_seperator + "</"
                                    + x.name + ">" + line_seperator;
                        }
                        xml = xml + element;
                    }
                }
            } catch (NoElementFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            xml = xml.substring(0, xml.length() - line_seperator.length());
        } else {
            xml = value;
        }
        return xml;
    }

    @Override
    public ParseObject parse() {
        ParseObject p = new ParseObject("XML", false);
        if (values.isEmpty()) {
            p.add(name, value);
        }
        for (String s : values.keySet()) {
            try {
                for (XmlElement x : getSections(s)) {
                    p.add(s, x.parse().objects);
                }
            } catch (NoElementFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return p;
    }

    @Override
    public String getId() {
        return "XML";
    }

    @Override
    public ParseAble fromParser(Section p) throws ParseException {
        if (p instanceof XmlElement) return new XmlElement((XmlElement) p);
        return null;
    }

    @Deprecated
    @Override
    public Section getSection(String name) throws NoElementFoundException {
        return getSections(name).get(0);
    }

    @Override
    public String getValue(String name) throws NoElementFoundException {
        return ((XmlElement) getSection(name)).getValue();
    }

    @NeedElement("ParseAble Class")
    public LinkedList<ParseAble> getParseAble(String name) {
        if (values != null) {
            LinkedList<ParseAble> el = new LinkedList<ParseAble>();
            for (Object o : values.get(name)) {
                if (o instanceof ParseAble) {
                    el.add((ParseAble) o);
                }
            }
            return el;
        }
        return null;
    }

    @Override
    public ArrayList<String> getStringList(String string) throws NoElementFoundException {
        ArrayList<String> list = new ArrayList<String>();
        for (String s : getValue(string).split("%\\$%\\$")) {
            list.add(s);
        }
        return list;
    }

    class RecommendedParserRunnable implements Runnable {

        XmlElement element;
        String xml;
        XmlParserEngine eng;
        boolean startNewThreads = false;
        String name = null;
        LinkedHashMap<String, String> attributes = null;
        boolean finished = false;
        int deep = 1;

        public RecommendedParserRunnable(String xml, XmlParserEngine engine, XmlElement ele,
                                         boolean startNewThreads, int deep) {
            this.xml = xml;
            element = ele;
            eng = engine;
            this.startNewThreads = startNewThreads;
            this.deep = deep;
        }

        public RecommendedParserRunnable(String xml, XmlParserEngine engine, XmlElement ele,
                                         boolean startNewThreads, String nextName,
                                         LinkedHashMap<String, String> attributes, int deep) {
            this.xml = xml;
            element = ele;
            eng = engine;
            this.startNewThreads = startNewThreads;
            this.name = nextName;
            this.attributes = attributes;
            this.deep = deep;
        }

        public boolean isFinished() {
            return true;
        }

        @Override
        public void run() {
            if (name == null) {
                element._encode_recommended(xml, eng, startNewThreads, deep);
            } else {
                LinkedList<Object> ob = new LinkedList<Object>();
                if (element.values.containsKey(name)) {
                    ob = element.values.get(name);
                }
                XmlElement el = new XmlElement(name, "", attributes);
                el._encode_recommended(xml, eng, startNewThreads, deep);
                ob.add(el);
                element.values.put(name, ob);
            }
            finished = true;
        }

    }
}
