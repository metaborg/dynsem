package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ListBuildFactory.ConsListBuildNodeGen;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_lang.IPersistentStack;
import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListBuild extends TermBuild {

	public ListBuild(SourceSection source) {
		super(source);
	}

	public static final class NilListBuild extends ListBuild {

		public NilListBuild(SourceSection source) {
			super(source);
		}

		public static NilListBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "List", 1);
			assert Tools.isTermList(t.getSubterm(0))
					&& Tools.listAt(t, 0).size() == 0;

			return new NilListBuild(SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return executeList(frame);
		}

		@Override
		public IPersistentStack<?> executeList(VirtualFrame frame) {
			return PersistentList.EMPTY;
		}
	}

	@NodeChildren({ @NodeChild(value = "headNode", type = TermBuild.class),
			@NodeChild(value = "tailNode", type = TermBuild.class) })
	public abstract static class ConsListBuild extends ListBuild {

		public ConsListBuild(SourceSection source) {
			super(source);
		}

		public static ConsListBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "ListTail", 2);
			TermBuild headNode = TermBuild.create(Tools.applAt(t, 0), fd);
			TermBuild tailNode = TermBuild.create(Tools.applAt(t, 1), fd);

			return ConsListBuildNodeGen.create(
					SourceSectionUtil.fromStrategoTerm(t), headNode, tailNode);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Specialization
		public IPersistentStack execute(Object head, IPersistentStack tail) {
			return (IPersistentStack) tail.cons(head);
		}

	}

}
