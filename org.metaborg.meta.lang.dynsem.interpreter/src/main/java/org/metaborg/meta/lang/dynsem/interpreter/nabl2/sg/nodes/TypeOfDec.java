package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "dec", type = TermBuild.class)
public abstract class TypeOfDec extends NativeOpBuild {

	public TypeOfDec(SourceSection source) {
		super(source);
	}

	@Specialization
	public Object executeGetType(Occurrence dec) {
		return NaBL2LayoutImpl.INSTANCE.getTypes(getContext().getNaBL2Solution()).get(dec);
	}

	public static TypeOfDec create(SourceSection source, TermBuild dec) {
		return TypeOfDecNodeGen.create(source, dec);
	}
}
