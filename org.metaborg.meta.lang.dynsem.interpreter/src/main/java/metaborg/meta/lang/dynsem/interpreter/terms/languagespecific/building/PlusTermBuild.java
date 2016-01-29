package metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.building;

import metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.PlusTerm;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class PlusTermBuild extends LangSpecificTermBuild {

	@Child protected LangSpecificTermBuild e1;
	@Child protected LangSpecificTermBuild e2;

	public PlusTermBuild(SourceSection source, TermBuild e1, TermBuild e2) {
		super(source);
		this.e1 = (LangSpecificTermBuild) e1;
		this.e2 = (LangSpecificTermBuild) e2;
	}

	@Override
	public PlusTerm executeGeneric(VirtualFrame frame) {
		try {
			return new PlusTerm(e1.executeIExprTerm(frame),
					e2.executeIExprTerm(frame));
		} catch (UnexpectedResultException e) {
			throw new RuntimeException("Unexpected subterm: " + e.getResult());
		}
	}

}
