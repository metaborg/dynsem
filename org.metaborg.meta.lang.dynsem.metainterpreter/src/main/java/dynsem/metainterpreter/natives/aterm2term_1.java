package dynsem.metainterpreter.natives;

import org.metaborg.dynsem.metainterpreter.generated.terms.ITTerm;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "term", type = TermBuild.class)
public abstract class aterm2term_1 extends TermBuild {

	public aterm2term_1(SourceSection source) {
		super(source);
	}

	@Specialization
	public ITTerm doEvaluated(IStrategoTerm aterm) {
		ITTerm trm = TermConverter.convert(aterm);
		return trm;
	}

	public static TermBuild create(SourceSection source, TermBuild tb) {
		return aterm2term_1NodeGen.create(source, tb);
	}

}
