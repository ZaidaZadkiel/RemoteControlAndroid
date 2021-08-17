package JavaUtils.Parser;

import java.util.HashMap;

public class ParserManager {

    static HashMap<String, ParseAble> parseAbles = new HashMap<String, ParseAble>();

    public static void registerParseAble(ParseAble p) {
        parseAbles.put(p.getId(), p);
    }

    public static ParseAble getParseAble(String id) {
        if (!parseAbles.containsKey(id)) {
            System.out.println("LOLOL!! ERROR LOLOL!!");
            System.out.println("Type " + id + " weren't found!\nAvailable Types are: ");
            for (String s : parseAbles.keySet()) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
        return parseAbles.get(id);
    }

}
