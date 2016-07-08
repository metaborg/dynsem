package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public class SourceSectionUtil {

	public static SourceSection none() {
		return SourceSection.createUnavailable("Unvailable", "noname");
	}

	public static SourceSection fromStrategoTerm(IStrategoTerm aterm) {
		return SourceSection.createUnavailable("sourceinfo", "somename");
	}
}
