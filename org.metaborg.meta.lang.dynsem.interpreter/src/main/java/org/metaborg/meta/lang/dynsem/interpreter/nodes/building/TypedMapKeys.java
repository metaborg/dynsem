package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.lang.reflect.Array;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
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
public abstract class TypedMapKeys extends TermBuild {

	private final String keyClass;
	private final String keyArrayClass;

	public static TypedMapKeys create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapKeys", 2);
		TermBuild mapNode = TermBuild.create(Tools.applAt(t, 0), fd);
		String keyClass = Tools.javaStringAt(t, 1);
		return TypedMapKeysNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), keyClass, mapNode);
	}

	public TypedMapKeys(SourceSection source, String keyClass) {
		super(source);
		this.keyClass = keyClass;
		this.keyArrayClass = "[L" + keyClass + ";";
	}

	@Override
	public abstract Object executeGeneric(VirtualFrame frame);

	@Specialization
	public Object doKeys(@SuppressWarnings("rawtypes") PersistentMap map, @Cached("getKeyClass()") Class<?> keyClass,
			@Cached("getListInit(keyClass)") ITermInit listInit) {
		@SuppressWarnings("unchecked")
		Object[] keyArray = map.keySet().toArray((Object[]) Array.newInstance(keyClass, 0));
		return listInit.apply(keyArray);
	}

	protected Class<?> getKeyClass() {
		try {
			return Class.forName(this.keyClass);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	protected ITermInit getListInit(Class<?> keyClass) {
		ITermRegistry termReg = getContext().getTermRegistry();
		return termReg.lookupClassConstructorWrapper(termReg.getListClass(keyClass));
	}

	// @Specialization
	// public IListTerm<?> doCached(@SuppressWarnings("rawtypes") PersistentMap map,
	// @SuppressWarnings("rawtypes") @Cached("getListClassConstructor()") Constructor<IListTerm> constr) {
	// return mkList(constr, map);
	//
	// }
	//
	// @TruffleBoundary
	// // FIXME: reflection is really bad for speed.
	// private IListTerm<?> mkList(@SuppressWarnings("rawtypes") Constructor<IListTerm> constr,
	// @SuppressWarnings("rawtypes") PersistentMap map) {
	// try {
	// return constr.newInstance(new Object[] { map.keySet() });
	// } catch (ReflectiveOperationException e) {
	// throw new InterpreterException("Could not instantiate list of keys", e);
	// }
	// }
	//
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// protected Constructor<IListTerm> getListClassConstructor() {
	// try {
	// Class<IListTerm> listClass = (Class<IListTerm>) this.getClass().getClassLoader().loadClass(keyListClass);
	// Constructor<IListTerm> constructor = listClass.getConstructor(Collection.class);
	// if (constructor == null) {
	// throw new InterpreterException("Could not find suitable constructor for list class " + keyListClass);
	// }
	//
	// return constructor;
	// } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
	// throw new InterpreterException("Could not find suitable class for list class " + keyListClass, e);
	// }
	// }

}
