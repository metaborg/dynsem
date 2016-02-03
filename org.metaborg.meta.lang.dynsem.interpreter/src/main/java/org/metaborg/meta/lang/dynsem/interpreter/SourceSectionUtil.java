package org.metaborg.meta.lang.dynsem.interpreter;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public class SourceSectionUtil {

	public static SourceSection fromStrategoTerm(IStrategoTerm aterm) {
		return SourceSection.createUnavailable("sourceinfo", "somename");
	}
}
