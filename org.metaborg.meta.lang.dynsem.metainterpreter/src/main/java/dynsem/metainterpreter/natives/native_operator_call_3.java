package dynsem.metainterpreter.natives;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.metaborg.dynsem.metainterpreter.generated.TypesGen;
import org.metaborg.dynsem.metainterpreter.generated.terms.ITTerm;
import org.metaborg.dynsem.metainterpreter.generated.terms.List_ITTerm;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "name", type = TermBuild.class), @NodeChild(value = "arity", type = TermBuild.class),
		@NodeChild(value = "args", type = TermBuild.class) })
public abstract class native_operator_call_3 extends TermBuild {

	public native_operator_call_3(SourceSection source) {
		super(source);
	}

	@Specialization
	public Object doCall(String s, int arity, List_ITTerm args) {
		assert arity == args.size();
		final String pkgName = "simpl.interpreter.natives";
		final String className = s + "_" + arity + "NodeGen";
		final String qClassName = pkgName + "." + className;

		final SourceSection section = SourceUtils.dynsemSourceSectionUnvailable();
		final ITTerm[] paramTerms = args.toArray();

		final Class<?>[] callParamTypes = new Class[arity + 1];
		callParamTypes[0] = SourceSection.class;
		Arrays.fill(callParamTypes, 1, arity + 1, TermBuild.class);

		final Object[] callParams = new Object[arity + 1];
		callParams[0] = section;

		for (int i = 0; i < arity; i++) {
			final ITTerm paramTerm = paramTerms[i];
			callParams[i + 1] = new TermBuild(section) {

				@Override
				public Object executeGeneric(VirtualFrame frame) {
					return paramTerm;
				}
			};
		}

		try {
			final ClassLoader loader = this.getClass().getClassLoader();
			final Class<?> theClass = loader.loadClass(qClassName);
			final Method factoryMethod = theClass.getMethod("create", callParamTypes);

			TermBuild opInst = (TermBuild) factoryMethod.invoke(null, callParams);
			ITTerm result = TypesGen.asITTerm(opInst.executeGeneric(Truffle.getRuntime().createVirtualFrame(new Object[0], new FrameDescriptor())));
			return result;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load native operator class", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not create native operator class", e);
		} catch (SecurityException e) {
			throw new RuntimeException("Could not create native operator class", e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new UnsupportedOperationException("Not implemented");
	}

	public static TermBuild create(SourceSection source, TermBuild name, TermBuild arity, TermBuild args) {
		return native_operator_call_3NodeGen.create(source, name, arity, args);
	}
}