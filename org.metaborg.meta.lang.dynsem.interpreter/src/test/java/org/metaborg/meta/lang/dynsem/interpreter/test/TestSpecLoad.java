package org.metaborg.meta.lang.dynsem.interpreter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public class TestSpecLoad {

	@Test
	public void testLoadSpec() throws Exception {
		File testDir = new File("src/test/resources/");
		File specFile = new File(testDir, "testSpec1.aterm");
		
		assert (DummyDynSemLanguage.INSTANCE != null);
		
		InputStream specInput = new FileInputStream(specFile);
		RuleRegistry rreg = new RuleRegistry(specInput);
		assertEquals(5, rreg.ruleCount());
	}

	@TruffleLanguage.Registration(name = "Dummy", version = "0.1", mimeType = "application/x-dummy")
	public static class DummyDynSemLanguage extends DynSemLanguage {

		public static final DummyDynSemLanguage INSTANCE = new DummyDynSemLanguage();

		public DummyDynSemLanguage() {
			DynSemContext.LANGUAGE = this;
		}

		@Override
		protected DynSemContext createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
			return new DynSemContext(null, null);
		}

		@Override
		protected CallTarget parse(Source code, Node context, String... argumentNames) throws IOException {
			return null;
		}

	}
}
