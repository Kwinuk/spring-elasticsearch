package test.elasticsearch.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    public static List<String> queryValueToArray(Object val) {
        if (val == null) {
            return new ArrayList<>();
        }

        if (! (val instanceof List)) {
            List<String> result = new ArrayList<>();
            result.add(val.toString());
            return result;
        }

        return (List<String>) val;
    }

}
