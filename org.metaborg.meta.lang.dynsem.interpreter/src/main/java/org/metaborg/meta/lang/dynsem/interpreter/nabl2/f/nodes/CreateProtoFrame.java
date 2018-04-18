package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
		Property[] decs = getDeclarationProperties(scopeLayout.getDeclarations(scopeEntry), frameShape.allocator());
		for (Property decProp : decs) {
			frameShape = frameShape.addProperty(decProp);
		}
		Property[] edges = getEdgeProperties(scopeLayout.getEdges(scopeEntry), frameShape.allocator());
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

	private Property[] getDeclarationProperties(Occurrence[] decs, Allocator allocator) {
		Property[] props = new Property[decs.length];
		for (int i = 0; i < decs.length; i++) {
			props[i] = Property.create(decs[i],
					allocator.locationForType(Object.class, EnumSet.of(LocationModifier.NonNull)), 0);
		}
		return props;
	}

	private Property[] getEdgeProperties(DynamicObject edges, Allocator allocator) {
		List<Object> labels = edges.getShape().getKeyList();
		List<Property> res = new ArrayList<>();
		for (Object keyObj : labels) {
			Label label = (Label) keyObj;
			ScopeIdentifier[] scopes = (ScopeIdentifier[]) edges.get(label);
			for (ScopeIdentifier scope : scopes) {
				FrameLinkIdentifier linkIdent = new FrameLinkIdentifier(label, scope);
				res.add(Property.create(linkIdent, allocator.locationForType(FrameType.class), 0));
			}
		}
		return res.toArray(new Property[0]);
	}

	private Property[] getImportProperties(DynamicObject imports) {
		// FIXME: implement import property support
		return new Property[0];
	}

}
