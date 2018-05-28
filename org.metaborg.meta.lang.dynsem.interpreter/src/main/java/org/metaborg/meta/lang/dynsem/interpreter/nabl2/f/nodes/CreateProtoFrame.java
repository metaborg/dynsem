package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.List;

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
		this.defaultValueNode = FrameNodeFactories.createDefaultValue(source);
	}

	public DynamicObject execute(VirtualFrame frame, DynamicObject scopeEntry) {
		DynSemContext ctx = getContext();

		ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;
		assert scopeLayout.isScopeEntry(scopeEntry);

		DynamicObject types = NaBL2LayoutImpl.INSTANCE.getTypes(ctx.getNaBL2Solution());
		Shape protoShape = FrameLayoutImpl.INSTANCE
				.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)).getShape();
		Allocator allocator = protoShape.allocator();

		Occurrence[] decs = scopeLayout.getDeclarations(scopeEntry);
		Property[] decProps = new Property[decs.length];
		Object[] decPropVals = new Object[decs.length];
		for (int i = 0; i < decs.length; i++) {
			Occurrence dec = decs[i];
			decPropVals[i] = defaultValueNode.execute(frame, types.get(dec));
			Property prop = Property.create(dec, allocator.locationForType(ValSort.class), 0);
			protoShape = protoShape.addProperty(prop);
			decProps[i] = prop;
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

		for (int i = 0; i < decProps.length; i++) {
			try {
				decProps[i].set(protoFrame2, decPropVals[i], null);
			} catch (IncompatibleLocationException | FinalLocationException e) {
				throw new IllegalStateException(e);
			}
		}

		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame2);

		return protoFrame2;
	}

}
