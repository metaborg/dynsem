package org.metaborg.meta.lang.dynsem.interpreter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.TestMatches;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.TestRules;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.TestPremises;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestMatches.class, TestPremises.class, TestRules.class })
public class TestSuite {

}
