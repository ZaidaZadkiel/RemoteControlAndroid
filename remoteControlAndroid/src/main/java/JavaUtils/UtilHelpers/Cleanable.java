package JavaUtils.UtilHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Cleanable<T> {

    private static Thread cleaner;
    private static ArrayList<Cleanable> cleanables = new ArrayList<Cleanable>();

    long lastUsed;
    long cleanTime;
    T object;
    ArrayList<Collection<T>> collections = new ArrayList<Collection<T>>();
    ArrayList<Map> maps = new ArrayList<Map>();

    public Cleanable(T o, long cleanTime) {
        object = o;
        lastUsed = System.currentTimeMillis();
        this.cleanTime = cleanTime;
        cleanables.add(this);

        if (cleaner == null) {
            cleaner = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (!cleanables.isEmpty()) {
                        for (Cleanable c : cleanables) {
                            if (c.lastUsed + c.cleanTime < System.currentTimeMillis()) {
                                c.removeAll();
                                cleanables.remove(c);
                            }
                        }
                        try {
                            Thread.sleep(5000L);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    cleaner = null;
                }

            });
            cleaner.start();
        }
    }

    public <K> void addToMap(K key, Map<K, Cleanable<T>> map) {
        maps.add(map);
        map.put(key, this);
    }

    public void addToList(Collection<Cleanable<T>> c) {
        c.add(this);
    }

    public T getObject() {
        lastUsed = System.currentTimeMillis();
        return object;
    }

    public void setObject(T o) {
        object = o;
    }

    protected void removeAll() {
        for (Collection c : collections) {
            c.remove(object);
        }
        for (Map m : maps) {
            m.values().remove(object);
        }
    }

}
