package JavaUtils.Parser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import JavaUtils.UtilHelpers.FileUtils;
import JavaUtils.XML.NoElementFoundException;

public class PFile implements ParseAble {

    String name;
    String content;
    String end;
    int length;

    public PFile(File f) {
        if (f.getName().contains(".")) {
            name = f.getName().substring(0, f.getName().lastIndexOf("."));
            end = f.getName().substring(f.getName().lastIndexOf("."));
        } else {
            name = f.getName();
            end = "";
        }
        try {
            content = FileUtils.readAll(f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        length = content.length();
    }

    public PFile(String name2, String end2, String content2, int length2) {
        name = name2;
        end = end2;
        content2 = content;
        length2 = length;
    }

    @Override
    public ParseObject parse() {
        ParseObject po = new ParseObject(getId());
        po.add("File.Description.Name", name);
        po.add("File.Description.End", end);
        po.add("File.Description.Length", length);
        po.add("File.Content", content);
        return po;
    }

    @Override
    public String getId() {
        return "PFile";
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        Section file = s.getSection("File");
        Section description = s.getSection("Description");
        String name = description.getValue("Name");
        String end = description.getValue("End");
        int length = Integer.valueOf(description.getValue("Length"));
        String content = file.getValue("Content");
        return new PFile(name, end, content, length);
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return name + end;
    }

    public String getEnd() {
        return end;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return length;
    }

}
