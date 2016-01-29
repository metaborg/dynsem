package metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.matching;

import metaborg.meta.lang.dynsem.interpreter.terms.languagespecific.PlusTerm;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class PlusMatchPattern extends MatchPattern {

	@Child private MatchPattern p1;
	@Child private MatchPattern p2;

	public PlusMatchPattern(SourceSection source, MatchPattern p1,
			MatchPattern p2) {
		super(source);
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public boolean execute(Object term, VirtualFrame frame) {
		if (term instanceof PlusTerm) {
			PlusTerm plus = (PlusTerm) term;
			return p1.execute(plus.get1(), frame)
					&& p2.execute(plus.get2(), frame);
		}
		return false;
	}

}
