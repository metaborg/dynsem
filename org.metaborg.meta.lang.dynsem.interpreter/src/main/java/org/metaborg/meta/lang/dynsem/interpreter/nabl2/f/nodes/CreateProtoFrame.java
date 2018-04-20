package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

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
		this.defaultValueNode = DefaultValueNodeGen.create(source);
	}

	public DynamicObject execute(VirtualFrame frame, DynamicObject scopeEntry) {
		DynSemContext ctx = getContext();

		ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;
		assert scopeLayout.isScopeEntry(scopeEntry);

		DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(ctx.getNaBL2Solution());
		/*
		 * 1. create a shape with declarations & edges
		 * 
		 * 2. compute default types for each of the declarations
		 * 
		 * 3. create an array with declaration values and NO-LINK for links
		 * 
		 * 4. instantiate the proto-frame with the values from 3) and !! the reference to the frame which created it
		 */
		// DynamicObject protoFrame = FrameLayoutImpl.INSTANCE.createFrame(
		// FrameLayoutImpl.INSTANCE.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)));
		Shape protoShape = FrameLayoutImpl.INSTANCE
				.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)).getShape();
		// Shape protoShape = protoFrame.getShape();
		Allocator allocator = protoShape.allocator();

		Occurrence[] decs = scopeLayout.getDeclarations(scopeEntry);
		Map<Property, Object> propVals = new HashMap<>();
		for (Occurrence dec : decs) {
			Object val = defaultValueNode.execute(frame, types.get(dec));
			Property prop = Property.create(dec, allocator.locationForType(ValSort.class), 0);
			propVals.put(prop, val);
			protoShape = protoShape.addProperty(prop);
		}

		DynamicObject edges = scopeLayout.getEdges(scopeEntry);
		List<Object> labels = edges.getShape().getKeyList();
		for (Object keyObj : labels) {
			Label label = (Label) keyObj;
			ScopeIdentifier[] scopes = (ScopeIdentifier[]) edges.get(label);
			for (ScopeIdentifier scope : scopes) {
				FrameLinkIdentifier linkIdent = new FrameLinkIdentifier(label, scope);
				Property prop = Property.create(linkIdent,
						allocator.locationForType(FrameLayoutUtil.layout().getType()), 0);
				protoShape = protoShape.addProperty(prop);
			}
		}

		DynamicObject imports = scopeLayout.getImports(scopeEntry);
		// TODO: add imports into proto frame

		DynamicObject protoFrame2 = protoShape.newInstance();

		for (Entry<Property, Object> propEntry : propVals.entrySet()) {
			try {
				propEntry.getKey().set(protoFrame2, propEntry.getValue(), null);
			} catch (IncompatibleLocationException | FinalLocationException e) {
				throw new IllegalStateException(e);
			}
		}
		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame2);

		return protoFrame2;
	}

}
