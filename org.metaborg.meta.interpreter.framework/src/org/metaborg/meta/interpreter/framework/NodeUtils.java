package org.metaborg.meta.interpreter.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

public class NodeUtils {

	public static String toString(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		String s = o.getClass().getSimpleName() + "(";
		for (Field f : fields) {
			try {
				s += f.get(o) + ", ";
			} catch (IllegalAccessException e) {
				 s += "<" + f.getName() + " INACCESSIBLE>";
			}
		}
		s += ")";
		return s;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
							list.replaceHead(newChild);
							parent.adoptChild(newChild);
							return newChild;
						}
						list = list.tail();
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new InterpreterException("Failed to rewrite", e);
		}

		throw new RewritingException("Cannot find child field to replace");
	}

	public static <T> INodeList<T> makeList(int length,
			IStrategoTerm parentTerm, Class<T> clazz) {
		INodeList<T> list = NodeList.NIL();
		for (int i = length - 1; i >= 0; i--) {
			if (clazz == Integer.class) {
				list = new NodeList<T>(clazz.cast(((IStrategoInt) parentTerm
						.getSubterm(i)).intValue()), list);
			} else if (clazz == String.class) {
				list = new NodeList<T>(clazz.cast(((IStrategoString) parentTerm
						.getSubterm(i)).stringValue()), list);
			} else {
				Constructor<T> c;
				try {
					c = clazz.getConstructor(INodeSource.class,
							IStrategoTerm.class);
					final ImploderNodeSource source = parentTerm.getSubterm(i)
							.getAttachment(ImploderAttachment.TYPE) != null ? new ImploderNodeSource(
							parentTerm.getSubterm(i).getAttachment(
									ImploderAttachment.TYPE)) : null;
					list = new NodeList<T>(c.newInstance(source,
							parentTerm.getSubterm(i)), list);
				} catch (Exception ex) {
					throw new RewritingException(ex);
				}
			}
		}
		return list;
	}

}
