package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
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
		DynamicObject protoFrame = createProtoFrame(scopeEntry);
		// populate it with default values
		Occurrence[] declarations = ScopeEntryLayoutImpl.INSTANCE.getDeclarations(scopeEntry);
		DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(getContext().getNaBL2Solution());
		for (Occurrence dec : declarations) {
			protoFrame.set(dec, defaultValueNode.execute(frame, types.get(dec)));
		}

		return protoFrame;
	}

	@TruffleBoundary
	private DynamicObject createProtoFrame(DynamicObject scopeEntry) {
		/*
		 * We're going to build shapes for a prototype frame, instantiate it
		 */
		ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;
		assert scopeLayout.isScopeEntry(scopeEntry);

		// create initial shape for the prototype frame
		Shape protoShape = FrameLayoutImpl.INSTANCE
				.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)).getShape();
		Allocator allocator = protoShape.allocator();

		/* Create and populate slots (for declarations) */

		// temporary mapping from in-frame property to (default) value
		Occurrence[] declarations = scopeLayout.getDeclarations(scopeEntry);
		Property[] declarationProps = new Property[declarations.length];
		for (int i = 0; i < declarations.length; i++) {
			Property prop = Property.create(declarations[i], allocator.locationForType(Object.class), 0);
			protoShape = protoShape.addProperty(prop);
			declarationProps[i] = prop;
		}

		// create props for every edge. NB: we're flattening the structure by encoding
		// multiplicity in the property name (edgeIdentifier)
		DynamicObject scopeEdges = scopeLayout.getEdges(scopeEntry);
		List<Object> edgeLinkLabels = scopeEdges.getShape().getKeyList();
		for (Object edgeLinkLabelObj : edgeLinkLabels) {
			ALabel linkLabel = (ALabel) edgeLinkLabelObj;
			ScopeIdentifier[] linkedScopes = (ScopeIdentifier[]) scopeEdges.get(linkLabel);
			for (ScopeIdentifier linkedScope : linkedScopes) {
				FrameEdgeIdentifier edgeIdentifier = new FrameEdgeIdentifier(linkLabel, linkedScope);
				Property edgeProp = Property.create(edgeIdentifier,
						allocator.locationForType(FrameUtils.layout().getType()), 0);
				protoShape = protoShape.addProperty(edgeProp);
			}
		}

		// create props for every import edge. these are imports of scopes associated with
		// declarations (identified by reference). NB: we are flattening the representation by encoding
		// the label and via occurrence in the link identifier
		DynamicObject scopeImports = scopeLayout.getImports(scopeEntry);
		List<Object> importLinkLabels = scopeImports.getShape().getKeyList();
		for (Object importLinkLabelObj : importLinkLabels) {
			ALabel linkLabel = (ALabel) importLinkLabelObj;
			Occurrence[] viaOccurrences = (Occurrence[]) scopeImports.get(linkLabel);
			for (Occurrence viaOccurrence : viaOccurrences) {
				FrameImportIdentifier importIdentifier = new FrameImportIdentifier(linkLabel, viaOccurrence);
				Property importProp = Property.create(importIdentifier,
						allocator.locationForType(FrameUtils.layout().getType()), 0);
				protoShape = protoShape.addProperty(importProp);
			}
		}

		// instantiate a prototype frame from the shape
		return protoShape.newInstance();
	}

}
