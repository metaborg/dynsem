package org.metaborg.meta.interpreter.framework;

import java.lang.reflect.Field;

public class NodeUtils {

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

	public static <T extends INode> T replaceChild(INode parent,
			INode oldChild, T newChild) {
		// locate the field of the oldChild
		try {
			for (Field f : parent.getClass().getDeclaredFields()) {
				if (f.isAnnotationPresent(Child.class)) {
					// single child case
					if (f.get(parent) == oldChild) {
						// do the replacement
						// ask the parent to adopt the child
						oldChild.setReplacedBy(newChild);
						parent.adoptChild(newChild);
						f.set(parent, newChild);
						return newChild;
					}
				} else if (f.isAnnotationPresent(Children.class)) {
					INodeList list = (INodeList) f.get(parent);
					while (list.size() > 0) {
						if (list.head() == oldChild) {
							// do the replacement
							// ask the parent to adopt the child
							oldChild.setReplacedBy(newChild);
							list.replaceHead(newChild);
							parent.adoptChild(newChild);
							return newChild;
						}
						list = list.tail();
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new InterpreterException("Failed to rewrite", null, e);
		}

		throw new RewritingException("Cannot find child field to replace");
	}

}
