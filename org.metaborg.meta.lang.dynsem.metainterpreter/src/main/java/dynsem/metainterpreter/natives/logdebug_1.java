package dynsem.metainterpreter.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "print", type = TermBuild.class)
public abstract class logdebug_1 extends TermBuild {

	public logdebug_1(SourceSection source) {
		super(source);
	}

	@Specialization
	public String doString(String s) {
		if (getContext().isDEBUG()) {
			final StringBuilder msgBuilder = new StringBuilder();
			int currentStackDepth = InterpreterUtils.stackDepth();
			for (; currentStackDepth > 0; currentStackDepth--) {
				msgBuilder.append(" ");
			}
			msgBuilder.append(" ");
			msgBuilder.append(s);

			getContext().getErr().println(msgBuilder.toString());
		}
		return s;
	}

	public static TermBuild create(SourceSection source, TermBuild print) {
		return logdebug_1NodeGen.create(source, print);
	}
}