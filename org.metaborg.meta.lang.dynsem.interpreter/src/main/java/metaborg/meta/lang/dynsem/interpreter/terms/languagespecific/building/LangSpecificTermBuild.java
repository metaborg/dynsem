package metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.building;

import metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.IExprTerm;
import metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.TypesGen;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public abstract class LangSpecificTermBuild extends TermBuild {

	public LangSpecificTermBuild(SourceSection source) {
		super(source);
	}

	public IExprTerm executeIExprTerm(VirtualFrame frame)
			throws UnexpectedResultException {
		return TypesGen.expectIExprTerm(executeGeneric(frame));
	}

}
