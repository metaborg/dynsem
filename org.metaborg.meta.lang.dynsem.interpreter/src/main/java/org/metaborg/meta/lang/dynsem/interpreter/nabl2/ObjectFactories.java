package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import java.util.EnumSet;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.DeclEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.DeclarationsLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.EdgesType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ImportsType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopesLayoutImpl;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.LocationModifier;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;

public class ObjectFactories {

	public ObjectFactories() {
	}

	public static DynamicObject createNaBL2(IStrategoAppl solution) {
		assert Tools.isTermAppl(solution);
		IStrategoAppl nabl2Appl = solution;
		assert Tools.hasConstructor(nabl2Appl, "NaBL2", 3);

		// DynamicObject scopes = ScopesLayoutImpl.INSTANCE.createScopes();
		DynamicObject scopeGraph = createScopeGraph(Tools.applAt(solution, 0));
		// DynamicObject nameResolution = null;
		// DynamicObject types = null;
		return NaBL2LayoutImpl.INSTANCE.createNaBL2(scopeGraph);
	}

	public static DynamicObject createScopeGraph(IStrategoAppl graph) {
		assert Tools.hasConstructor(graph, "G", 3);

		DynamicObject scopes = createScopes(Tools.listAt(graph, 0));
		DynamicObject decls = createDeclarations(Tools.listAt(graph, 1));
		return ScopeGraphLayoutImpl.INSTANCE.createScopeGraph(scopes, decls);
	}

	private static DynamicObject createDeclarations(IStrategoList declsTerm) {
		DynamicObject declarations = DeclarationsLayoutImpl.INSTANCE.createDeclarations();
		for (IStrategoTerm declTerm : declsTerm) {
			assert Tools.isTermTuple(declTerm) && declTerm.getSubtermCount() == 2;
			DynamicObject declaration = createDeclarationEntry(Tools.applAt(declTerm, 1));
			declarations.define(Occurrence.create(Tools.applAt(declTerm, 0)), declaration);
		}
		return declarations;

	}

	private static DynamicObject createDeclarationEntry(IStrategoAppl deTerm) {
		assert Tools.hasConstructor(deTerm, "DE", 2);
		ScopeIdentifier[] decs = createScopeIdentifiers(Tools.listAt(deTerm, 0));

		IStrategoList edgesTerm = Tools.listAt(deTerm, 1);
		Shape edgesShape = LAYOUT_EDGES.createShape(EdgesType.INSTANCE);
		ScopeIdentifier[][] edgeScopes = new ScopeIdentifier[edgesTerm.size()][];
		for (int i = 0; i < edgeScopes.length; i++) {
			IStrategoTerm edgeTerm = edgesTerm.getSubterm(i);
			assert Tools.isTermTuple(edgeTerm);
			Label edgeLabel = Label.create(Tools.applAt(edgeTerm, 0));
			edgesShape = edgesShape.addProperty(Property.create(edgeLabel, LAYOUT_EDGES_ALLOCATOR.locationForType(
					ScopeIdentifier[].class, EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)), 0));
			IStrategoList scopeListT = Tools.listAt(edgeTerm, 1);
			ScopeIdentifier[] scopes = new ScopeIdentifier[scopeListT.size()];
			for (int j = 0; j < scopes.length; j++) {
				scopes[j] = ScopeIdentifier.create(Tools.applAt(scopeListT, j));
			}
			edgeScopes[i] = scopes;
		}
		DynamicObject edges = edgesShape.createFactory().newInstance((Object[]) edgeScopes);
		return DeclEntryLayoutImpl.INSTANCE.createDeclEntry(decs, edges);
	}

	private static DynamicObject createScopes(IStrategoList scopesTerm) {
		DynamicObject scopes = ScopesLayoutImpl.INSTANCE.createScopes();
		for (IStrategoTerm scopeTerm : scopesTerm) {
			assert Tools.isTermTuple(scopeTerm) && scopeTerm.getSubtermCount() == 2;
			DynamicObject scope = createScopeEntry(Tools.applAt(scopeTerm, 1));
			scopes.define(ScopeIdentifier.create(Tools.applAt(scopeTerm, 0)), scope);
		}
		return scopes;
	}

	private final static Layout LAYOUT_EDGES = Layout.createLayout();
	private final static Allocator LAYOUT_EDGES_ALLOCATOR = LAYOUT_EDGES.createAllocator();

	private final static Layout LAYOUT_IMPORTS = Layout.createLayout();
	private final static Allocator LAYOUT_IMPORTS_ALLOCATOR = LAYOUT_IMPORTS.createAllocator();

	private static DynamicObject createScopeEntry(IStrategoAppl scopeTerm) {
		assert Tools.hasConstructor(scopeTerm, "SE", 4);
		Occurrence[] decs = createOccurrences(Tools.listAt(scopeTerm, 0));
		Occurrence[] refs = createOccurrences(Tools.listAt(scopeTerm, 1));

		IStrategoList edgesTerm = Tools.listAt(scopeTerm, 2);
		Shape edgesShape = LAYOUT_EDGES.createShape(EdgesType.INSTANCE);
		ScopeIdentifier[][] edgeScopes = new ScopeIdentifier[edgesTerm.size()][];
		for (int i = 0; i < edgeScopes.length; i++) {
			IStrategoTerm edgeTerm = edgesTerm.getSubterm(i);
			assert Tools.isTermTuple(edgeTerm);
			Label edgeLabel = Label.create(Tools.applAt(edgeTerm, 0));
			edgesShape = edgesShape.addProperty(Property.create(edgeLabel, LAYOUT_EDGES_ALLOCATOR.locationForType(
					ScopeIdentifier[].class, EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)), 0));
			IStrategoList scopeListT = Tools.listAt(edgeTerm, 1);
			ScopeIdentifier[] scopes = new ScopeIdentifier[scopeListT.size()];
			for (int j = 0; j < scopes.length; j++) {
				scopes[j] = ScopeIdentifier.create(Tools.applAt(scopeListT, j));
			}
			edgeScopes[i] = scopes;
		}
		DynamicObject edges = edgesShape.createFactory().newInstance((Object[]) edgeScopes);

		IStrategoList importsTerm = Tools.listAt(scopeTerm, 3);
		Shape importsShape = LAYOUT_IMPORTS.createShape(ImportsType.INSTANCE);
		Occurrence[][] importedOccs = new Occurrence[importsTerm.size()][];
		for (int i = 0; i < importedOccs.length; i++) {
			IStrategoTerm importTerm = importsTerm.getSubterm(i);
			assert Tools.isTermTuple(importTerm);
			Label importLabel = Label.create(Tools.applAt(importTerm, 0));
			importsShape = importsShape.addProperty(
					Property.create(importLabel, LAYOUT_IMPORTS_ALLOCATOR.locationForType(Occurrence[].class,
							EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)), 0));
			IStrategoList occListT = Tools.listAt(importsTerm, 1);
			Occurrence[] occs = new Occurrence[occListT.size()];
			for (int j = 0; j < occs.length; j++) {
				occs[j] = Occurrence.create(Tools.applAt(occListT, j));
			}
			importedOccs[i] = occs;
		}
		DynamicObject imports = importsShape.createFactory().newInstance((Object[]) importedOccs);
		return ScopeEntryLayoutImpl.INSTANCE.createScopeEntry(decs, refs, edges, imports);
	}

	private static Occurrence[] createOccurrences(IStrategoList occurrencesTerm) {
		Occurrence[] occurrences = new Occurrence[occurrencesTerm.size()];
		for (int i = 0; i < occurrences.length; i++) {
			occurrences[i] = Occurrence.create(Tools.applAt(occurrencesTerm, i));
		}
		return occurrences;
	}

	private static ScopeIdentifier[] createScopeIdentifiers(IStrategoList scopesTerm) {
		ScopeIdentifier[] scopes = new ScopeIdentifier[scopesTerm.size()];
		for (int i = 0; i < scopes.length; i++) {
			scopes[i] = ScopeIdentifier.create(Tools.applAt(scopesTerm, i));
		}
		return scopes;
	}

}
