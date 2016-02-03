package org.metaborg.meta.lang.dynsem.interpreter.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.RuleRegistry;

import com.oracle.truffle.api.nodes.NodeUtil;

public class TestSpecLoad {

	@Test
	public void testLoadSpec() throws Exception {
		File testDir = new File("src/test/resources/");
		File specFile = new File(testDir, "testSpec1.aterm");
		RuleRegistry rreg = RuleRegistry.create(specFile);
		assertEquals(5, rreg.ruleCount());
//		NodeUtil.printTreeToString(rreg.lookupRule("Plus", 2));
	}
}
