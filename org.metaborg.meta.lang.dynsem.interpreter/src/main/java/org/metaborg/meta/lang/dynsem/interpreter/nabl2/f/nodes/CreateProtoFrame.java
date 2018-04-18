package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.EnumSet;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameType;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectFactory;
import com.oracle.truffle.api.object.LocationModifier;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;
import com.oracle.truffle.api.source.SourceSection;

public class CreateProtoFrame extends DynSemNode {

	// @Child private

	public CreateProtoFrame(SourceSection source) {
		super(source);
	}

	public DynamicObject execute(VirtualFrame frame, DynamicObject scopeEntry) {
		DynSemContext ctx = getContext();

		ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;
		assert scopeLayout.isScopeEntry(scopeEntry);

		/*
		 * 1. create a shape with declarations & edges
		 * 
		 * 2. compute default types for each of the declarations
		 * 
		 * 3. create an array with declaration values and NO-LINK for links
		 * 
		 * 4. instantiate the proto-frame with the values from 3) and !! the reference to the frame which created it
		 */

		Shape frameShape = ctx.getFrameLayout().createShape(FrameType.INSTANCE)
				.addProperty(ctx.SCOPE_OF_FRAME_PROPERTY);
		Property[] decs = getDeclarationProperties(scopeLayout.getDeclarations(scopeEntry));
		for (Property decProp : decs) {
			frameShape = frameShape.addProperty(decProp);
		}
		Property[] edges = getEdgeProperties(scopeLayout.getEdges(scopeEntry));
		for (Property edgeProp : edges) {
			frameShape = frameShape.addProperty(edgeProp);
		}
		Property[] imports = getImportProperties(scopeLayout.getImports(scopeEntry));
		for (Property importProp : imports) {
			frameShape = frameShape.addProperty(importProp);
		}

		DynamicObjectFactory protoFrameFactory = frameShape.createFactory();

		Object[] decTypes = null; // TODO
		Object[] decVals = null; // TODO

		Object[] initArgs = new Object[1 + decs.length + edges.length + imports.length];
		// set reference to scope
		initArgs[0] = scopeLayout.getIdentifier(scopeEntry);
		// copy default values for decs
		System.arraycopy(decVals, 0, initArgs, 1, decVals.length);
		// instantiate the protoframe and return
		return protoFrameFactory.newInstance(initArgs);
	}

	private DynamicObjectFactory createProtoFrame(DynSemContext ctx, DynamicObject scopeEntry) {
		ScopeEntryLayout layout = ScopeEntryLayoutImpl.INSTANCE;

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
		// FIXME: handle import edges!!

		return frameShape.createFactory();
	}

	private Property[] getDeclarationProperties(Occurrence[] decs) {
		// TODO Auto-generated method stub
		return null;
	}

	private Property[] getEdgeProperties(DynamicObject edges) {
		// TODO Auto-generated method stub
		return null;
	}

	private Property[] getImportProperties(DynamicObject imports) {
		// FIXME: implement import property support
		return new Property[0];
	}

	public Object defaultValueForType(Object ty) {
		throw new IllegalStateException("default value not implemented");
	}

}
