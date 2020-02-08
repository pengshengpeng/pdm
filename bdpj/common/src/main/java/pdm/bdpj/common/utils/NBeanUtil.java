package pdm.bdpj.common.utils;

import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NBeanUtil {

    public static <T> void populate(T t, Map properties) throws Exception {
        if (t != null && properties != null) {
            Iterator names = properties.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (name != null) {
                    String beanPropertyName = name;
                    if (name.indexOf("_") != -1) {
                        String[] arr = name.split("_");
                        beanPropertyName = arr[0];
                        for (int i = 1; i < arr.length; i++) {
                            String firstLetter = (arr[i].charAt(0) + "").toUpperCase();
                            beanPropertyName += firstLetter + arr[i].substring(1);
                        }
                    }
                    Object value = properties.get(name);
                    BeanUtils.setProperty(t, beanPropertyName, value);
                }
            }

        }
    }

    public static <T> void populateSimple(T t, Map properties) throws Exception {
        if (t != null && properties != null) {
            Iterator names = properties.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (name != null) {
                    Object value = properties.get(name);
                    BeanUtils.setProperty(t, name, value);
                }
            }

        }
    }

    public static <T, V> V populateBean(T t, Class<V> v) throws Exception {
        V vv = v.newInstance();
        BeanUtils.copyProperties(vv, t);
        return vv;
    }

    public static <T, V> List<V> populateList(List<T> srcList, Class<V> v) {
        List<V> destList = new ArrayList<>();
        if (srcList != null) {
            try {
                for (T t : srcList) {
                    V vv = v.newInstance();
                    BeanUtils.copyProperties(vv, t);
                    destList.add(vv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return destList;
    }

    public static Map describe(Object t) throws Exception {
        return BeanUtils.describe(t);
    }
}
