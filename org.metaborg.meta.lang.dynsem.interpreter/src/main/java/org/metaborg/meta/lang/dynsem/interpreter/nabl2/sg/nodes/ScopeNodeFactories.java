package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.source.SourceSection;

public final class ScopeNodeFactories {

	private ScopeNodeFactories() {
	}

	public static TypeOfDec createTypeOfDec(SourceSection source, TermBuild dec) {
		return TypeOfDecNodeGen.create(source, dec);
	}

	public static NativeOpBuild createMkScopeIdentifier(SourceSection source, TermBuild resource, TermBuild name) {
		return mkScopeIdentifierNodeGen.create(source, resource, name);
	}

	public static NativeOpBuild createMkLabelP(SourceSection source) {
		return mkLabelPNodeGen.create(source);
	}

	public static NativeOpBuild createMkLabelI(SourceSection source) {
		return mkLabelINodeGen.create(source);
	}

	public static NativeOpBuild createMkLabel(SourceSection source, TermBuild labelstring) {
		return mkLabelNodeGen.create(source, labelstring);
	}

	public static MakeOccurrence createMakeOccurrence(SourceSection source, TermBuild namespace, TermBuild name,
			TermBuild termindex) {
		return MakeOccurrenceNodeGen.create(source, namespace, name, termindex);
	}

	public static NativeOpBuild createMakeFreshOccurrence(SourceSection source, TermBuild namespace, TermBuild name) {
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

	public static CreateScope createCreateScope(SourceSection source, TermBuild scopeIdent, TermBuild decs, TermBuild decTypes,
			TermBuild refs,
			TermBuild edges, TermBuild imports) {
		return ScopeNodeFactories.createCreateScope(source, scopeIdent, decs, decTypes, refs, edges, imports);
	}

}
