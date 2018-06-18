package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.Shape.Allocator;
import com.oracle.truffle.api.source.SourceSection;

public class CreateProtoFrame extends DynSemNode {

	@Child private DefaultValue defaultValueNode;

	public CreateProtoFrame(SourceSection source) {
		super(source);
		this.defaultValueNode = FrameNodeFactories.createDefaultValue(source);
	}

	public DynamicObject execute(VirtualFrame frame, DynamicObject scopeEntry) {
		/*
		 * We're going to build shapes for a prototype frame, instantiate it and populate it with default values
		 */
		DynSemContext ctx = getContext();

		ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;
		assert scopeLayout.isScopeEntry(scopeEntry);

		// create initial shape for the prototype frame
		Shape protoShape = FrameLayoutImpl.INSTANCE
				.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)).getShape();
		Allocator allocator = protoShape.allocator();

		// retrieve the types, required to compute default values
		DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(ctx.getNaBL2Solution());

		/* Create and populate slots (for declarations) */

		// temporary mapping from occurrence to in-frame property
		// Map<Occurrence, Property> occ2prop = new HashMap<>();
		// temporary mapping from in-frame property to (default) value
		Map<Property, Object> prop2val = new HashMap<>();

		// allocate properties and compute default values
		for (Occurrence dec : scopeLayout.getDeclarations(scopeEntry)) {
			// allocate property
			Property prop = Property.create(dec, allocator.locationForType(Object.class), 0);
			// add to shape, yielding new shape
			protoShape = protoShape.addProperty(prop);
			// compute and memo default value for this property
			prop2val.put(prop, defaultValueNode.execute(frame, types.get(dec)));
		}

		// Occurrence[] decs = scopeLayout.getDeclarations(scopeEntry);
		// Property[] decProps = new Property[decs.length];
		// Object[] decPropVals = new Object[decs.length];
		// for (int i = 0; i < decs.length; i++) {
		// Occurrence dec = decs[i];
		// decPropVals[i] = defaultValueNode.execute(frame, types.get(dec));
		// Property prop = Property.create(dec, allocator.locationForType(Object.class), 0);
		// protoShape = protoShape.addProperty(prop);
		// decProps[i] = prop;
		// }


		// create props for every edge. NB: we're flattening the structure by encoding
		// multiplicity in the property name (edgeIdentifier)
		DynamicObject scopeEdges = scopeLayout.getEdges(scopeEntry);
		List<Object> edgeLinkLabels = scopeEdges.getShape().getKeyList();
		for (Object edgeLinkLabelObj : edgeLinkLabels) {
			Label linkLabel = (Label) edgeLinkLabelObj;
			ScopeIdentifier[] linkedScopes = (ScopeIdentifier[]) scopeEdges.get(linkLabel);
			for (ScopeIdentifier linkedScope : linkedScopes) {
				FrameEdgeIdentifier edgeIdentifier = new FrameEdgeIdentifier(linkLabel, linkedScope);
				Property edgeProp = Property.create(edgeIdentifier,
						allocator.locationForType(FrameLayoutUtil.layout().getType()), 0);
				protoShape = protoShape.addProperty(edgeProp);
			}
		}

		// create props for every import edge. these are imports of scopes associated with
		// declarations (identified by reference). NB: we are flattening the representation by encoding
		// the label and via occurrence in the link identifier
		DynamicObject scopeImports = scopeLayout.getImports(scopeEntry);
		List<Object> importLinkLabels = scopeImports.getShape().getKeyList();
		for (Object importLinkLabelObj : importLinkLabels) {
			Label linkLabel = (Label) importLinkLabelObj;
			Occurrence[] viaOccurrences = (Occurrence[]) scopeImports.get(linkLabel);
			for (Occurrence viaOccurrence : viaOccurrences) {
				FrameImportIdentifier importIdentifier = new FrameImportIdentifier(linkLabel, viaOccurrence);
				Property importProp = Property.create(importIdentifier,
						allocator.locationForType(FrameLayoutUtil.layout().getType()), 0);
				protoShape = protoShape.addProperty(importProp);
			}
		}

		// instantiate a prototype frame from the shape
		DynamicObject protoFrame = protoShape.newInstance();

		// set declaration to their computed values
		for (Entry<Property, Object> p2v : prop2val.entrySet()) {
			try {
				p2v.getKey().set(protoFrame, p2v.getValue(), null);
			} catch (IncompatibleLocationException | FinalLocationException e) {
				throw new IllegalStateException(e);
			}
		}

		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame);

		return protoFrame;
	}

}
