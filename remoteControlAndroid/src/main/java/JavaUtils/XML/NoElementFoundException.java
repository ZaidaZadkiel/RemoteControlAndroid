package JavaUtils.XML;

public class NoElementFoundException extends Exception {

    public String elementNotFound;
    public String[] args;

    public NoElementFoundException(String noElementFound, String... args) {
        super("Element not found: " + noElementFound + " Extra Infos: " + join(args));
        elementNotFound = noElementFound;
        this.args = args;
    }

    private static String join(String[] args2) {
        String r = "";
        for (String s : args2) {
            r = r + s + " ";
        }
        return r;
    }

}
