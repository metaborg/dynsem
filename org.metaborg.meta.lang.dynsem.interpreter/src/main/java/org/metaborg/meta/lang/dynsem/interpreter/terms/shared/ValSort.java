package org.metaborg.meta.lang.dynsem.interpreter.terms.shared;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.source.SourceSection;

public abstract class ValSort implements IApplTerm {

	@Override
	public Class<?> getSortClass() {
		return ValSort.class;
	}

	public abstract static class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

	}
}
