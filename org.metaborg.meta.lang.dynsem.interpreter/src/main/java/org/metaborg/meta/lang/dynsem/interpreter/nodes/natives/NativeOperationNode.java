package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.HandleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.RaiseNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
public abstract class NativeOperationNode extends DynSemNode {

	public NativeOperationNode(SourceSection source) {
		super(source);
	}

	public abstract Object execute(VirtualFrame frame, VirtualFrame components);

	public static NativeOperationNode create(IStrategoAppl t, FrameDescriptor ruleFD, FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "Raise", 1)) {
			return RaiseNode.create(t, ruleFD);
		}
		if (Tools.hasConstructor(t, "Handle2", 2) || Tools.hasConstructor(t, "Handle3", 3)) {
			return HandleNode.create(t, ruleFD, componentsFD);
		}
		throw new NotImplementedException("Unknown native operation term: " + t);
	}
}
