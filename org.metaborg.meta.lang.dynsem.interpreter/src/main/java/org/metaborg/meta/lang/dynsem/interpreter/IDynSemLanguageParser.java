package org.metaborg.meta.lang.dynsem.interpreter;

import javax.annotation.Nullable;

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
	default IStrategoTerm parse(Source src) {
		return parse(src, null);
	}

	/**
	 * Provide the {@link IStrategoTerm} (or ATerm) corresponding to the given source.
	 *
	 * @param src
	 *            A truffle {@link Source source code unit}.
	 * @param overridingStartSymbol
	 *            A start symbol that overrides any other start symbol that may have been set. Can be null.
	 * @return The parsed {@link IStrategoTerm}.
	 */
	IStrategoTerm parse(Source src, @Nullable String overridingStartSymbol);

}
