package JavaUtils.UtilHelpers;

import java.util.HashMap;

public class MultiMap<K, V, V2> extends HashMap<K, Object[]> {

    /**
     *
     */
    private static final long serialVersionUID = -5779318186929757134L;

    public Object[] get(String name) {
        return super.get(name);
    }

    public V getValue1(String name) {
        return (V) get(name)[0];
    }

    public V2 getValue2(String name) {
        return (V2) get(name)[1];
    }

    public void put(K key, V value1, V2 value2) {
        super.put(key, new Object[]{value1, value2});
    }
}
