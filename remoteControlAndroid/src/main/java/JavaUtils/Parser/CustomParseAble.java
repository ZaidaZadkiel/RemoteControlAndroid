package JavaUtils.Parser;

import java.text.ParseException;

import JavaUtils.XML.NoElementFoundException;

public class CustomParseAble implements ParseAble {

    ParseObject po;
    String id;

    public CustomParseAble(ParseObject po, String id) {
        this.po = po;
        this.id = id;
    }

    @Override
    public ParseObject parse() {
        return po;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        return null;
    }

}
