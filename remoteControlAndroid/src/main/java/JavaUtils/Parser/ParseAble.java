package JavaUtils.Parser;

import java.text.ParseException;

import JavaUtils.XML.NoElementFoundException;


public interface ParseAble {

    ParseObject parse();

    String getId();

    ParseAble fromParser(Section s) throws ParseException, NoElementFoundException;

}
