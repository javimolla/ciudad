package es.javimolla.city.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
	public static Map<? extends Object, ? extends Object> listsToMap(List<? extends Object> keys,
			List<? extends Object> values) {
		Map<Object, Object> map = new HashMap<>();
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}
		return map;
	}
}
