package org.metaborg.meta.interpreter.framework;

import java.lang.reflect.Field;

@Deprecated
public class NodeUtils {

	@Deprecated
	public static String toString(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		String s = o.getClass().getSimpleName() + "(";
		for (Field f : fields) {
			try {
				s += f.get(o) + ", ";
			} catch (IllegalAccessException e) {
				// s += "<" + f.getName() + ">";
			}
		}
		s += ")";
		return s;
	}
	

}
