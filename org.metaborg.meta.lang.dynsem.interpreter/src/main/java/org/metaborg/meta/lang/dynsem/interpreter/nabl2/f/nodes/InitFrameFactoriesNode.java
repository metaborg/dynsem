package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.EnumSet;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.LayoutUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectFactory;
import com.oracle.truffle.api.object.LocationModifier;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;
import com.oracle.truffle.api.source.SourceSection;

public class InitFrameFactoriesNode extends DynSemNode {

	public InitFrameFactoriesNode(SourceSection source) {
		super(source);
	}

	public void execute(VirtualFrame frame) {
		DynSemContext ctx = getContext();
		if (!ctx.hasNaBL2()) {
			return;
		}
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(ctx.getNaBL2());
		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);
		Class<? extends DynamicObject> scopeEntryClass = LayoutUtils.getScopeEntryLayout().getType();

		// for all scopes
		for (Property scopeProperty : scopes.getShape().getProperties()) {
			DynamicObject scopeEntry = scopeEntryClass.cast(scopes.get(scopeProperty.getKey()));

			ctx.addFrameFactory((ScopeIdentifier) scopeProperty.getKey(), frameFactoryForScopeEntry(scopeEntry));
		}
	}

	private DynamicObjectFactory frameFactoryForScopeEntry(DynamicObject scopeEntry) {
		ScopeEntryLayout layout = ScopeEntryLayoutImpl.INSTANCE;
		// ScopeIdentifier identifier = layout.getIdentifier(scopeEntry);
		DynSemContext ctx = getContext();

		Occurrence[] decs = layout.getDeclarations(scopeEntry);
		DynamicObject edges = layout.getEdges(scopeEntry);

		Allocator allocator = ctx.getFrameAllocator();
		Shape frameShape = ctx.getFrameLayout().createShape(FrameType.INSTANCE)
				.addProperty(ctx.SCOPE_OF_FRAME_PROPERTY);
		// register declaration properties -- what about default values?
		for (Occurrence dec : decs) {
			Property decProp = Property.create(dec,
					allocator.locationForType(Object.class, EnumSet.of(LocationModifier.NonNull)), 0);
			frameShape = frameShape.addProperty(decProp);
		}
		// register link properties
		for (Property edgeProp : edges.getShape().getProperties()) {
			Label label = (Label) edgeProp.getKey();
			ScopeIdentifier[] scopes = (ScopeIdentifier[]) edges.get(label);
			for (ScopeIdentifier scope : scopes) {
				FrameLinkIdentifier flk = new FrameLinkIdentifier(label, scope);
				Property linkProp = Property.create(flk, allocator.locationForType(FrameType.class), 0);
				frameShape = frameShape.addProperty(linkProp);
			}
		}
		return frameShape.createFactory();
	}

}
