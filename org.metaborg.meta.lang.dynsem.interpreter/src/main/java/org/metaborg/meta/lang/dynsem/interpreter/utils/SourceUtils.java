package org.metaborg.meta.lang.dynsem.interpreter.utils;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class SourceUtils {

	public static SourceSection dynsemSourceSectionFromATerm(IStrategoTerm t) {
		return Source.newBuilder("notext").name("noname").internal().mimeType(DynSemLanguage.DYNSEM_MIME).build()
				.createUnavailableSection();
	}

	public static SourceSection dynsemSourceSectionUnvailable() {
		return Source.newBuilder("notext").name("noname").internal().mimeType(DynSemLanguage.DYNSEM_MIME).build()
				.createUnavailableSection();
	}

	public static Source getSyntheticSource(final String text, final String name, final String mimetype) {
		return Source.newBuilder(text).internal().name(name).mimeType(mimetype).build();
	}
}
