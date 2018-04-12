package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.DeclEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.DeclarationsLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.EdgesType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ImportsType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.NameResolutionLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.PathStep;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ReferencesLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.ScopesLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.scopegraph.TypesLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
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

	public static DynamicObject createNaBL2(IStrategoAppl solution, DynSemContext ctx) {
		assert Tools.isTermAppl(solution);
		IStrategoAppl nabl2Appl = solution;
		assert Tools.hasConstructor(nabl2Appl, "NaBL2", 3);

		DynamicObject scopeGraph = createScopeGraph(Tools.applAt(solution, 0));
		DynamicObject nameResolution = createNameResolution(Tools.listAt(solution, 1));
		DynamicObject types = createTypes(Tools.listAt(solution, 2), ctx);
		return NaBL2LayoutImpl.INSTANCE.createNaBL2(scopeGraph, nameResolution, types);
	}

	private static DynamicObject createTypes(IStrategoList typesTerm, DynSemContext ctx) {
		ITermRegistry termRegistry = ctx.getTermRegistry();
		DynamicObject types = TypesLayoutImpl.INSTANCE.createTypes();
		for (IStrategoTerm typeEntry : typesTerm) {
			assert Tools.isTermTuple(typeEntry);
			Occurrence decl = Occurrence.create(Tools.applAt(typeEntry, 0));
			IStrategoAppl typeTerm = Tools.applAt(typeEntry, 1);
			IStrategoConstructor typeConstructor = typeTerm.getConstructor();
			Class<?> typeTermClass = termRegistry.getConstructorClass(typeConstructor.getName(),
					typeConstructor.getArity());
			try {
				Method creationMethod = typeTermClass.getDeclaredMethod("create", IStrategoTerm.class);
				ITerm type = (ITerm) creationMethod.invoke(null, typeTerm);
				types.define(decl, type);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException("Failed to construct type term", e);
			}
		}
		return types;
	}

	private static DynamicObject createNameResolution(IStrategoList resolutionsTerm) {
		DynamicObject resolution = NameResolutionLayoutImpl.INSTANCE.createNameResolution();
		for (IStrategoTerm resolutionTerm : resolutionsTerm) {
			assert Tools.isTermTuple(resolutionTerm);
			Occurrence ref = Occurrence.create(Tools.applAt(resolutionTerm, 0));
			PathStep[] path = PathStep.createPath(Tools.listAt(resolutionTerm.getSubterm(1), 1));
			resolution.define(ref, path);
		}
		return resolution;
	}

	public static DynamicObject createScopeGraph(IStrategoAppl graph) {
		assert Tools.hasConstructor(graph, "G", 3);

		DynamicObject scopes = createScopes(Tools.listAt(graph, 0));
		DynamicObject decls = createDeclarations(Tools.listAt(graph, 1));
		DynamicObject refs = createReferences(Tools.listAt(graph, 2));
		return ScopeGraphLayoutImpl.INSTANCE.createScopeGraph(scopes, decls, refs);
	}

	private static DynamicObject createReferences(IStrategoList refsTerm) {
		DynamicObject references = ReferencesLayoutImpl.INSTANCE.createReferences();
		for (IStrategoTerm refTerm : refsTerm) {
			assert Tools.isTermTuple(refTerm) && refTerm.getSubtermCount() == 2;
			IStrategoAppl refEntryTerm = Tools.applAt(refTerm, 1);
			assert Tools.hasConstructor(refEntryTerm, "RE", 1);
			IStrategoList refEntryScopesTerm = Tools.listAt(refEntryTerm, 0);
			ScopeIdentifier[] scopes = new ScopeIdentifier[refEntryScopesTerm.size()];
			for (int i = 0; i < scopes.length; i++) {
				scopes[i] = ScopeIdentifier.create(Tools.applAt(refEntryScopesTerm, i));
			}
			references.define(Occurrence.create(Tools.applAt(refTerm, 0)), scopes);
		}
		return references;
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
