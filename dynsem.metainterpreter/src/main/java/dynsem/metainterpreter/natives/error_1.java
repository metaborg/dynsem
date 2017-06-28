package dynsem.metainterpreter.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "s", type = TermBuild.class)
public abstract class error_1 extends TermBuild {

	public error_1(SourceSection source) {
		super(source);
	}

	@Specialization
	public Object doObjects(String s) {
		throw new ReductionFailure(s, InterpreterUtils.createStacktrace());
	}

	public static TermBuild create(SourceSection source, TermBuild s) {
		return error_1NodeGen.create(source, s);
	}
}