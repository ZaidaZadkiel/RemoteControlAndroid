package JavaUtils.Parser;

import JavaUtils.XML.NoElementFoundException;

public interface Parser {

    Section getSection(String name) throws NoElementFoundException;

    String getValue(String name) throws NoElementFoundException;

}
