package JavaUtils.Parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import JavaUtils.ClassLoader.UtilClass;
import JavaUtils.ClassLoader.UtilClassBuilder;
import JavaUtils.XML.NoElementFoundException;

public class JavaClassModule extends TransferableFile implements ParseAble {

    public JavaClassModule(File f) throws IOException {
        super(f);
    }

    public JavaClassModule(String filePath) throws IOException {
        super(filePath);
    }

    private JavaClassModule(TransferableFile f) {
        super(f.getName(), f.getContent(), f.getLength());
    }

    @Override
    public ParseObject parse() {
        ParseObject po = new ParseObject(getId());
        ParseObject p2 = super.parse();
        for (String s : p2.objects.keySet()) {
            po.add("ClassFile." + s, p2.objects.get(s));
        }
        return po;
    }

    @Override
    public String getId() {
        return "JavaClassModule";
    }

    @Override
    public ParseAble fromParser(Section s) throws ParseException,
            NoElementFoundException {
        return new JavaClassModule((TransferableFile) super.fromParser(s
                .getSection("ClassFile")));
    }

    public void runModule(final String method, boolean newTask,
                          final Object... InitialisingArgs) throws IOException,
            NoSuchMethodException, SecurityException, ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        if (newTask) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        File f = new File(getName());
                        saveToFile(f);
                        UtilClassBuilder<Class> utilClassBuilder = new UtilClassBuilder<Class>(
                                f.getParentFile());
                        UtilClass<Class> uc = utilClassBuilder
                                .newUtilClass(getName());
                        Class c = uc.initialise(InitialisingArgs);
                        c.getMethod(method).invoke(c);
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
            return;
        }
        File f = new File(getName());
        saveToFile(f);
        UtilClassBuilder<Class> utilClassBuilder = new UtilClassBuilder<Class>(
                f.getParentFile());
        UtilClass<Class> uc = utilClassBuilder.newUtilClass(getName());
        Class c = uc.initialise(InitialisingArgs);
        c.getMethod(method).invoke(c);
    }

}
