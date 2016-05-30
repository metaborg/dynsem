package org.metaborg.meta.lang.dynsem.interpreter;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.Source;

/**
 * Parser which provides an {@link IStrategoTerm} for a given source.
 */
public interface IDynSemLanguageParser {

	/**
	 * Provide the {@link IStrategoTerm} (or ATerm) corresponding to the given source.
	 *
	 * @param src
	 *            A truffle {@link Source source code unit}.
	 * @return The parsed {@link IStrategoTerm}.
	 */
	IStrategoTerm parse(Source src);
}
