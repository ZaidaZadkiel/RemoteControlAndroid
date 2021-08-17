package JavaUtils.TCPManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import javax.crypto.NoSuchPaddingException;

import JavaUtils.Parser.CustomParseAble;
import JavaUtils.Parser.ParseAble;
import JavaUtils.Parser.ParseObject;
import JavaUtils.Parser.ParserManager;
import JavaUtils.XML.NoElementFoundException;
import JavaUtils.XML.XmlElement;
import JavaUtils.XML.XmlParser;

public class XmlTcpConnection extends TcpConnection {

    XmlTcpConnection(Socket accept, int index) throws IOException {
        super(accept, index);
    }

    public XmlTcpConnection(TcpConnection connect) throws IOException {
        super(connect.s, connect.index);
    }

    XmlTcpConnection(InputStream in, OutputStream out, Socket s, int index) {
        super(in, out, s, index);
    }

    public XmlParser readXml() throws ParseException {
        String xml = "";
        String line_seperator = "\n";
        String line = "";
        while (!(line = readLine()).contains("$$FF%%FF$$")) xml = xml + line_seperator + line;
        xml = xml.replaceAll("$$FF%%FF$$", "");
        return new XmlParser(xml);
    }

    public void writeXml(XmlElement x) {
        super.writeLine(x.decode() + System.getProperty("line.separator") + "$$FF%%FF$$");
    }

    public void writeXmlClass(ParseAble p) {
        ParseObject po = new ParseObject("Information");
        po.add("Type", p.getId());
        writeLine(new XmlElement(p).decode() + System.getProperty("line.separator") + new XmlElement(new CustomParseAble(po, p.getId())).decode().substring("<?xml version= 1.0  encoding= UTF-8  ?>".length()));
        writeLine("$$FF%%FF$$");
    }

    public ParseAble readXmlClass() throws ParseException, NoElementFoundException {
        XmlParser p = readXml();
        String parseAble = p.getSections("Information").get(0).getSections("Type").get(0).getValue();
        ParseAble parse = ParserManager.getParseAble(parseAble);
        ParseAble pa = parse.fromParser(p.getSection(parseAble));
        return pa;
    }

    public CryptedXmlTcpConnection toCryptedConnection(String password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        return new CryptedXmlTcpConnection(this, password);
    }

    public Socket getSocket() {
        return s;
    }
}
