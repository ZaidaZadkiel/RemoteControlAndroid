package JavaUtils.Parser;

import java.text.ParseException;

import JavaUtils.XML.NoElementFoundException;

public class Exception implements ParseAble {

    String exception;

    public Exception(String exception) {
        this.exception = exception;
    }

    @Override
    public ParseObject parse() {
        ParseObject po = new ParseObject(getId());
        po.add("Exception.Description", exception);
        return po;
    }

    @Override
    public String getId() {
        return "Exception";
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        return new Exception(s.getSection("Exception").getValue("Description"));
    }

    public String getException() {
        return exception;
    }

}
