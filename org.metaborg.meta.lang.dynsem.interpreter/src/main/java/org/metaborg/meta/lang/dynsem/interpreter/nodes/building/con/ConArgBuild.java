package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "arg", type = TermBuild.class)
public abstract class ConArgBuild extends DynSemNode {

	public ConArgBuild(SourceSection source) {
		super(source);
	}

	public abstract Cons executeArguments(VirtualFrame frame);

	public static ConArgBuild fromTermBuilds(SourceSection source, TermBuild[] terms) {
		ConArgBuild args = null;
		for (int i = terms.length - 1; i >= 0; i--) {
			if (args == null) {
				args = SingleConArgBuildNodeGen.create(source, terms[i]);
			} else {
				args = MultiConArgBuildNodeGen.create(source, terms[i], args);
			}
		}
		return args;
	}

}
