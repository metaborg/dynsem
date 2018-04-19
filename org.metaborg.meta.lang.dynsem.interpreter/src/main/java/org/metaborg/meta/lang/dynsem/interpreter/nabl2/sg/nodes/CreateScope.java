package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import java.util.EnumSet;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.InitProtoFrame;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.InitProtoFrameNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEdges;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeImports;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.github.krukow.clj_lang.IPersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.LocationModifier;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scopeIdent", type = TermBuild.class),
		@NodeChild(value = "decs", type = TermBuild.class), @NodeChild(value = "decTypes", type = TermBuild.class),
		@NodeChild(value = "refs", type = TermBuild.class), @NodeChild(value = "edges", type = TermBuild.class),
		@NodeChild(value = "imports", type = TermBuild.class) })
public abstract class CreateScope extends NativeOpBuild {

	@Child private InitProtoFrame protoFrameInit;

	public CreateScope(SourceSection source) {
		super(source);
		this.protoFrameInit = InitProtoFrameNodeGen.create(source);
	}

	// FIXME: drop VirtualFrame from method (will require changes downstream)
	@Specialization
	public ScopeIdentifier executeCreate(VirtualFrame frame, ScopeIdentifier scopeIdent, IListTerm<?> _decs,
			IListTerm<?> decTypes, IListTerm<?> _refs, IPersistentMap<?, ?> edgesMap, IPersistentMap<?, ?> importsMap) {
		DynSemContext ctx = getContext();

		IListTerm<Occurrence> decs = ctx.getTermRegistry().getListClass(Occurrence.class).cast(_decs);
		IListTerm<Occurrence> refs = ctx.getTermRegistry().getListClass(Occurrence.class).cast(_refs);

		Shape edgesShape = ScopeEdges.SINGLETON.createShape();
		Allocator edgeAllocator = ScopeEdges.SINGLETON.allocator();
		ScopeIdentifier[][] edgeScopes = new ScopeIdentifier[edgesMap.count()][];
		int i = 0;
		for (Entry<?, ?> edgeEntry : edgesMap) {
			Label edgeLabel = (Label) edgeEntry.getKey();
			edgesShape = edgesShape
					.addProperty(Property.create(edgeLabel, edgeAllocator.locationForType(ScopeIdentifier[].class,
							EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)), 0));
			edgeScopes[i] = (ScopeIdentifier[]) edgeEntry.getValue();
			i++;
		}
		DynamicObject edges = edgesShape.createFactory().newInstance((Object[]) edgeScopes);

		Shape importsShape = ScopeImports.SINGLETON.createShape();
		Allocator importAllocator = ScopeImports.SINGLETON.allocator();
		Occurrence[][] importedOccs = new Occurrence[importsMap.count()][];
		int j = 0;
		for (Entry<?, ?> importEntry : importsMap) {
			Label importLabel = (Label) importEntry.getKey();
			importsShape = importsShape
					.addProperty(Property.create(importLabel, importAllocator.locationForType(Occurrence[].class,
							EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)), 0));
			importedOccs[j] = (Occurrence[]) importEntry.getValue();
			j++;
		}
		DynamicObject imports = importsShape.createFactory().newInstance((Object[]) importedOccs);

		DynamicObject scopeEntry = ScopeEntryLayoutImpl.INSTANCE.createScopeEntry(scopeIdent, decs.toArray(),
				refs.toArray(), edges, imports);

		DynamicObject nabl2 = ctx.getNaBL2Solution();
		DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(nabl2);
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(nabl2);
		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);

		scopes.define(scopeIdent, scopeEntry);

		IListTerm<?> decTypesHead = decTypes;
		for (Occurrence dec : decs) {
			types.define(dec, decTypesHead.head());
			decTypesHead = decTypesHead.tail();
		}

		protoFrameInit.execute(frame, scopeEntry);

		return scopeIdent;
	}

	public static CreateScope create(SourceSection source, TermBuild scopeIdent, TermBuild decs, TermBuild refs,
			TermBuild edges, TermBuild imports) {
		return CreateScopeNodeGen.create(source, scopeIdent, decs, refs, edges, imports);
	}
}
