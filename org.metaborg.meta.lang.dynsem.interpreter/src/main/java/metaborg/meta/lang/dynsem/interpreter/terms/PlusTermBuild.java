package metaborg.meta.lang.dynsem.interpreter.terms;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class PlusTermBuild extends TermBuild {

	@Child protected TermBuild e1;
	@Child protected TermBuild e2;

	public PlusTermBuild(SourceSection source, TermBuild e1, TermBuild e2) {
		super(source);
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public PlusTerm execute(VirtualFrame frame) {

		// TODO Auto-generated method stub
		return null;
	}

}
