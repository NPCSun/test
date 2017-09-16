package test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sun on 2017/7/21 上午8:45.
 */
public class ReflectTest {
	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> list = new ArrayList<String>();
		Class<?> clazz = list.getClass();
		Type type = clazz.getGenericSuperclass();
		System.err.println("generic super class type:" + type);

		TypeVariable trueType = (TypeVariable)((ParameterizedType) type).getActualTypeArguments()[0];
		System.err.println("generic super class type:" + trueType);

	}
}
