package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

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

public class DynSemLanguageParser {
	private Path parsetable;
	private SGLR parser;

	public DynSemLanguageParser(Path parsetable) {
		this.parsetable = parsetable;
	}

	public IStrategoTerm parse(Source src, String startSymbol) {
		if (parser == null) {
			createParser();
		}

		try {
			SGLRParseResult parseResult = parser.parse(IOUtils.toString(src.getInputStream()), src.getName(),
					startSymbol);
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
		try (InputStream stream = new FileInputStream(new File(parsetable.toUri()));) {
			TermReader termReader = new TermReader(factory);
			IStrategoTerm parseTableTerm = termReader.parseFromStream(stream);

			return new ParseTable(parseTableTerm, factory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
