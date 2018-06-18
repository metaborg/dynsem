package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.DeclEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.DeclarationsLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "occurrence", type = TermBuild.class),
		@NodeChild(value = "label", type = TermBuild.class) })
public abstract class AssocScopeOf extends NativeOpBuild {

	public AssocScopeOf(SourceSection source) {
		super(source);
	}

	@Specialization
	public ScopeIdentifier executeGet(Occurrence occurrence, Label label) {
		DynSemContext ctx = getContext();
		DynamicObject nabl2 = ctx.getNaBL2Solution();
		// DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(nabl2);

		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(nabl2);
		DynamicObject declarations = ScopeGraphLayoutImpl.INSTANCE.getDeclarations(sg);
		assert DeclarationsLayoutImpl.INSTANCE.isDeclarations(declarations);
		DynamicObject declEntry = (DynamicObject) declarations.get(occurrence);
		DynamicObject assocs = DeclEntryLayoutImpl.INSTANCE.getAssociatedScopes(declEntry);
		ScopeIdentifier[] scopes = (ScopeIdentifier[]) assocs.get(label);
		assert scopes.length == 1;
		return scopes[0];
	}

	public static AssocScopeOf create(SourceSection source, TermBuild occurrence, TermBuild label) {
		return ScopeNodeFactories.createAssocScopeOf(source, occurrence, label);
	}

}
