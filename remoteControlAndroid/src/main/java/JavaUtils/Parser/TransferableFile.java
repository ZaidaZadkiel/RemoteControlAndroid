package JavaUtils.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import JavaUtils.UtilHelpers.FileUtils;
import JavaUtils.XML.NoElementFoundException;

public class TransferableFile implements ParseAble {

    String name;
    String content;
    long length;

    public TransferableFile(File f) throws IOException {
        name = f.getName();
        content = FileUtils.readAll(f);
        length = f.length();
    }

    public TransferableFile(String filePath) throws IOException {
        File f = new File(filePath);
        name = f.getName();
        content = FileUtils.readAll(f);
        length = f.length();
    }

    protected TransferableFile(String name, String content, long length) {
        this.name = name;
        this.content = content;
        this.length = length;
    }

    @Override
    public ParseObject parse() {
        ParseObject po = new ParseObject(getId());
        po.add("File.Name", name);
        po.add("File.Content", content);
        po.add("File.Length", length);
        return po;
    }

    @Override
    public String getId() {
        return "TransferableFile";
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        Section file = s.getSection("File");
        return new TransferableFile(file.getValue("Name"), file.getValue("Content"), Long.valueOf(file.getValue("Length")));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public long getLength() {
        return length;
    }

    public void saveToFile(File f) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        pw.write(content);
        pw.close();
    }

    public void saveToFile(String filePath) throws IOException {
        saveToFile(new File(filePath));
    }

}
