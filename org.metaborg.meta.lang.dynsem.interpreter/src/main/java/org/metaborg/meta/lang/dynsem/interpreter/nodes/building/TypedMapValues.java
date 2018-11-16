package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "mapNode", type = TermBuild.class)
public abstract class TypedMapValues extends TermBuild {

	private final String valueListClass;

	public TypedMapValues(SourceSection source, String keylistClass) {
		super(source);
		this.valueListClass = keylistClass;
	}

	@Override
	public abstract Object executeGeneric(VirtualFrame frame);

	@Specialization
	public IListTerm<?> doCached(@SuppressWarnings("rawtypes") PersistentMap map,
			@SuppressWarnings("rawtypes") @Cached("getListClassConstructor()") Constructor<IListTerm> constr) {
		try {
			return constr.newInstance(new Object[] { map.values() });
		} catch (ReflectiveOperationException e) {
			throw new InterpreterException("Could not instantiate list of values", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Constructor<IListTerm> getListClassConstructor() {
		try {
			Class<IListTerm> listClass = (Class<IListTerm>) this.getClass().getClassLoader().loadClass(valueListClass);
			Constructor<IListTerm> constructor = listClass.getConstructor(Collection.class);
			if (constructor == null) {
				throw new InterpreterException("Could not find suitable constructor for list class " + valueListClass);
			}

			return constructor;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			throw new InterpreterException("Could not find suitable class for list class " + valueListClass, e);
		}
	}

}
