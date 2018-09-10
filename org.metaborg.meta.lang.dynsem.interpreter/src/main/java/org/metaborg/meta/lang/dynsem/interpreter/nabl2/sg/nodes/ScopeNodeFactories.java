package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.source.SourceSection;

public final class ScopeNodeFactories {

	private ScopeNodeFactories() {
	}

	public static TypeOfDec createTypeOfDec(SourceSection source, TermBuild dec) {
		return TypeOfDecNodeGen.create(source, dec);
	}

	public static mkScopeIdentifier createMkScopeIdentifier(SourceSection source, TermBuild resource, TermBuild name) {
		return mkScopeIdentifierNodeGen.create(source, resource, name);
	}

	public static MakeOccurrence createMakeOccurrence(SourceSection source, TermBuild namespace, TermBuild name,
			TermBuild termindex) {
		return MakeOccurrenceNodeGen.create(source, namespace, name, termindex);
	}

	public static MakeFreshOccurrence createMakeFreshOccurrence(SourceSection source, TermBuild namespace,
			TermBuild name) {
		return MakeFreshOccurrenceNodeGen.create(source, namespace, name);
	}

	public static GetTopLevelTermIndex createGetTopLevelTermIndex(SourceSection source, TermBuild term) {
		return GetTopLevelTermIndexNodeGen.create(source, term);
	}

	public static GetScopeOfTerm createScopeOfTerm(SourceSection source, TermBuild t) {
		return GetScopeOfTermNodeGen.create(source, t);
	}

	public static DecOfRef createDecOfRef(SourceSection source, TermBuild ref) {
		return DecOfRefNodeGen.create(source, ref);
	}

	public static DeclsOfScope createDeclsOfScope(SourceSection source, TermBuild scope) {
		return DeclsOfScopeNodeGen.create(source, scope);
	}

	public static CreateScope createCreateScope(SourceSection source, TermBuild scopeIdent, TermBuild decs,
			TermBuild decTypes, TermBuild refs, TermBuild edges, TermBuild imports) {
		return CreateScopeNodeGen.create(source, scopeIdent, decs, decTypes, refs, edges, imports);
	}

	public static AssocScopeOf createAssocScopeOf(SourceSection source, TermBuild occurrence, TermBuild label) {
		return AssocScopeOfNodeGen.create(source, occurrence, label);
	}

	public static ScopesEqual createScopesEquals(SourceSection source, TermBuild s1, TermBuild s2) {
		return ScopesEqualNodeGen.create(source, s1, s2);
	}

	public static LinkedScopesOverLabel createLinkedScopesOverLabel(SourceSection source, TermBuild scope,
			TermBuild label) {
		return LinkedScopesOverLabelNodeGen.create(source, scope, label);
	}

}
