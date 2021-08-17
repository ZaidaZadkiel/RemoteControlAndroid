package JavaUtils.Parser;

import java.util.ArrayList;
import java.util.LinkedList;

import JavaUtils.XML.NoElementFoundException;

public interface Section {

    Section getSection(String name) throws NoElementFoundException;

    String getValue(String name) throws NoElementFoundException;

    LinkedList<ParseAble> getParseAble(String string);

    ArrayList<String> getStringList(String string) throws NoElementFoundException;
}
