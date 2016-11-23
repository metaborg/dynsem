package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "mapNode", type = TermBuild.class)
public abstract class TypedMapValues extends TermBuild {

	private final String valueListClass;

	public static TypedMapValues create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapValues", 2);
		TermBuild mapNode = TermBuild.create(Tools.applAt(t, 0), fd);
		String valueListClass = Tools.javaStringAt(t, 1);
		return TypedMapValuesNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), valueListClass, mapNode);
	}

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
