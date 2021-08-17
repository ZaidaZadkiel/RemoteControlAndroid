package JavaUtils.Parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParseObject {

    public LinkedHashMap<String, Object> objects = new LinkedHashMap<String, Object>();
    String name;
    boolean autoName = false;

    public ParseObject(String name) {
        this.name = name;
    }

    public ParseObject(String name, boolean autoName) {
        this.name = name;
        this.autoName = autoName;
    }

    public void add(String path, String o) {
        if (path.contains(".")) {
            path = path.replace(".", ":");
            String[] paths = path.split(":");
            LinkedHashMap<String, Object> map;
            if (objects.containsKey(paths[0])) {
                map = (LinkedHashMap<String, Object>) objects.get(paths[0]);
            } else {
                map = new LinkedHashMap<String, Object>();
            }
            if (paths.length > 2) {
                objects.put(paths[0], insert(map, paths, 1, o));
            } else {
                map.put(paths[1], o);
                objects.put(paths[0], map);
            }
        } else {
            objects.put(path, o);
        }
    }

    private LinkedHashMap<String, Object> insert(LinkedHashMap<String, Object> map,
                                                 String[] p, int index, String o) {
        LinkedHashMap<String, Object> map2;
        if (map.containsKey(p[index])) {
            map2 = (LinkedHashMap<String, Object>) map.get(p[index]);
        } else {
            map2 = new LinkedHashMap<String, Object>();
        }
        if (p.length > index + 2) {
            insert(map2, p, index + 1, o);
        } else {
            map2.put(p[index + 1], o);
        }
        map.put(p[index], map2);
        return map;
    }

    public void add(String path, int i) {
        String s = String.valueOf(i);
        if (s.contains(".")) s = s.split(".")[0];
        add(path, s);
    }

    public void add(String path, long l) {
        String s = String.valueOf(l);
        if (s.contains(".")) s = s.split(".")[0];
        add(path, s);
    }

    public void add(String path, double d) {
        add(path, String.valueOf(d));
    }

    public void add(String path, boolean b) {
        add(path, String.valueOf(b));
    }

    /*
        public void add(String path, Map<String, Object> m, boolean autoName) {
            String path2 = path;
            String keys = "";
            for (String s : m.keySet()) {
                if(keys.isEmpty())keys = s;
                else keys = keys + " / " + s;
                Object o = m.get(s);
                if(path.isEmpty())path2 = s;
                else if(autoName&&!path2.equalsIgnoreCase(s))path2 = path + "." + s;
                if (o instanceof String) {
                    add(path2, (String) o);
                } else if (o instanceof Integer) {
                    add(path2, (Integer) o);
                } else if (o instanceof Double) {
                    add(path2, (Double) o);
                } else if (o instanceof Boolean) {
                    add(path2, (Boolean) o);
                } else if (o instanceof List) {
                    add(path2, (List<Object>) o);
                } else if (o instanceof ParseAble) {
                    add(path2, (ParseAble) o);
                } else if (o instanceof Map) {
                    add(path2, (Map<String, Object>) o,autoName);
                }
            }
            add(path + ".map_keys",keys);
        }
    */
    public void add(String path, List<Object> a) {
        String s = "";
        for (Object l : a)
            s = s + "%$%$" + l;
        add(path, s);
    }

    public void add(String path, ParseAble p) {
        add(path + "~~type=ParseAble--" + p.getId(), p.parse().objects);
    }

    public LinkedHashMap<String, Object> getObjects() {
        if (!autoName) return objects;
        LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
        obj.put(name, objects);
        return obj;
    }

    public void set(LinkedHashMap<String, Object> objects2) {
        objects = objects2;
    }

    public void add(String s, ParseAble x, boolean b) {
        if (!b) add("", x.parse().objects);
        else add(s, x);
    }

    public void add(String path, Object o) {
        if (o instanceof Integer) {
            add(path, ((Integer) o).intValue());
        } else if (o instanceof Long) {
            add(path, ((Long) o).longValue());
        } else if (o instanceof Double) {
            add(path, ((Double) o).doubleValue());
        } else if (o instanceof Boolean) {
            add(path, ((Boolean) o).booleanValue());
        } else if (o instanceof Map) {
            LinkedHashMap<String, Object> objects = null;
            int index = 0;
            if (path.contains(".")) {
                for (String s : path.split(".")) {
                    if (objects == null) {
                        objects = (LinkedHashMap<String, Object>) this.objects.get(s);
                    } else {
                        objects = (LinkedHashMap<String, Object>) objects.get(s);
                    }
                    if (objects == null) {
                        objects = new LinkedHashMap<String, Object>();
                    }
                    index++;
                    if (index + 1 == path.split(".").length) {
                        break;
                    }
                }
            } else {
                objects = (LinkedHashMap<String, Object>) this.objects.get(path);
                if (objects == null) {
                    objects = new LinkedHashMap<String, Object>();
                }
            }
            if (path.contains(".")) {
                objects.put(path.split(".")[path.split(".").length - 1], o);
            } else {
                objects.put(path, o);
            }
            String[] paths = path.split(".");
            LinkedHashMap<String, Object> objects2 = null;
            for (int i = 0; i < paths.length - 2; i++) {
                for (int i2 = 0; i < paths.length - 2 - i; i++) {
                    String s = paths[i2];
                    if (objects2 == null) {
                        objects2 = (LinkedHashMap<String, Object>) this.objects.get(s);
                    } else {
                        objects2 = (LinkedHashMap<String, Object>) objects2.get(s);
                    }
                    if (objects2 == null) {
                        objects2 = new LinkedHashMap<String, Object>();
                    }
                }
                objects2.put(paths[paths.length - i - 2], objects);
                objects = objects2;
                objects2 = null;
            }
        } else if (o instanceof List) {
            add(path, (List<Object>) o);
        } else if (o instanceof ParseAble) {
            add(path, (ParseAble) o);
        } else if (o instanceof String) {
            add(path, (String) o);
        }
    }

}
