package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLinkIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
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
		DynamicObject protoFrame = FrameLayoutImpl.INSTANCE.createFrame(
				FrameLayoutImpl.INSTANCE.createFrameShape(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry)));

		Occurrence[] decs = scopeLayout.getDeclarations(scopeEntry);
		for (Occurrence dec : decs) {
			protoFrame.define(dec, defaultValueNode.execute(frame, types.get(dec)));
		}

		DynamicObject edges = scopeLayout.getEdges(scopeEntry);
		List<Object> labels = edges.getShape().getKeyList();
		for (Object keyObj : labels) {
			Label label = (Label) keyObj;
			ScopeIdentifier[] scopes = (ScopeIdentifier[]) edges.get(label);
			for (ScopeIdentifier scope : scopes) {
				FrameLinkIdentifier linkIdent = new FrameLinkIdentifier(label, scope);
				protoFrame.define(linkIdent, null);
			}
		}

		DynamicObject imports = scopeLayout.getImports(scopeEntry);
		// TODO: add imports into proto frame

		return protoFrame;
	}

}
