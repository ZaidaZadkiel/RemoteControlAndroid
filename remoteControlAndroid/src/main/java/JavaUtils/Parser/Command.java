package JavaUtils.Parser;

import java.text.ParseException;

import JavaUtils.XML.NoElementFoundException;

public class Command implements ParseAble {

    String command = "command";
    String user = "_____";
    String pw = "_____";

    @Deprecated
    public Command() {
    }

    public Command(String command) {
        this.command = command;
    }

    public Command(String command, String user, String pw) {
        this.command = command;
        this.user = user;
        this.pw = pw;
    }

    @Override
    public ParseObject parse() {
        ParseObject po = new ParseObject(getId());
        po.add("Command", command);
        po.add("User.Name", user);
        po.add("User.Password", pw);
        return po;
    }

    @Override
    public String getId() {
        return "Command";
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        String command = s.getValue("Command");
        Section user = s.getSection("User");
        String name = user.getValue("Name");
        String pw = user.getValue("Password");
        if (!name.equalsIgnoreCase(this.user)) {
            return new Command(command, name, pw);
        } else {
            return new Command(command);
        }
    }

    public String getCommand() {
        return command;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return pw;
    }

}
