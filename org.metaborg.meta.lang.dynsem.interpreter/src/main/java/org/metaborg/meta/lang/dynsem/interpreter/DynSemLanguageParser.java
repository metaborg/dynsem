package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.ParseTable;
import org.spoofax.jsglr.client.SGLR;
import org.spoofax.jsglr.client.SGLRParseResult;
import org.spoofax.jsglr.client.imploder.TermTreeFactory;
import org.spoofax.jsglr.client.imploder.TreeBuilder;
import org.spoofax.jsglr.shared.SGLRException;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.TermReader;

import com.oracle.truffle.api.source.Source;

/**
 * Default {@link IDynSemLanguageParser} implementation which parses a program in its textual representation to an
 * {@link IStrategoTerm}, using the {@link SGLR} parser.
 */
public class DynSemLanguageParser implements IDynSemLanguageParser {
	private InputStream parsetableInput;
	private String startSymbol;
	private SGLR parser;

	public DynSemLanguageParser(InputStream parsetableInput, String startSymbol) {
		this.parsetableInput = parsetableInput;
		this.startSymbol = startSymbol;
	}

	@Override
	public IStrategoTerm parse(Source src, @Nullable String overridingStartSymbol) {
		if (parser == null) {
			createParser();
		}
		String startSymbol = this.startSymbol;
		if (overridingStartSymbol != null) {
			startSymbol = overridingStartSymbol;
		}

		try {
			SGLRParseResult parseResult = parser.parse(IOUtils.toString(src.getInputStream(), Charset.defaultCharset()),
					src.getName(), startSymbol);
			IStrategoTerm term = (IStrategoTerm) parseResult.output;
			return term;
		} catch (SGLRException | InterruptedException | IOException e) {
			throw new IllegalStateException("File failed to parse", e);
		}

	}

	private void createParser() {
		TreeBuilder treebuilder = new TreeBuilder(new TermTreeFactory(new TermFactory()));

		parser = new SGLR(treebuilder, loadPT());

		parser.setUseStructureRecovery(false);
	}

	private ParseTable loadPT() {
		TermFactory factory = new TermFactory();
		try {
			TermReader termReader = new TermReader(factory);
			IStrategoTerm parseTableTerm = termReader.parseFromStream(parsetableInput);
			parsetableInput.close();
			return new ParseTable(parseTableTerm, factory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
