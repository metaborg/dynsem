package org.metaborg.meta.lang.dynsem.interpreter.utils;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;


public class SourceUtils {

	// private final static SourceSection UNAVAILABLE = Source.newBuilder("notext").name("noname").internal()
	// .mimeType(DynSemLanguage.DYNSEM_MIME).build().createUnavailableSection();
	private final static SourceSection UNAVAILABLE = null;

	public static SourceSection dynsemSourceSectionFromATerm(IStrategoTerm t) {
		return UNAVAILABLE;
	}

	public static SourceSection dynsemSourceSectionUnvailable() {
		return UNAVAILABLE;
	}

	public static Source getSyntheticSource(final String text, final String name, final String mimetype) {
		return null;
		// return Source.newBuilder(text).internal().name(name).mimeType(mimetype).build();
	}
}
