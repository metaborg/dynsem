package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.GetScopeOfTermNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.source.SourceSection;

public final class FrameNodeFactories {

	private FrameNodeFactories() {
	}

	public static SetAtAddr createSetAddr(SourceSection source, TermBuild addr, TermBuild val) {
		return SetAtAddrNodeGen.create(source, addr, val);
	}

	public static ScopeOfFrame createScopeOfFrame(SourceSection source, TermBuild frm) {
		return ScopeOfFrameNodeGen.create(source, frm);
	}

	public static NewFrameLink createNewFrameLink(SourceSection source, TermBuild label, TermBuild frm) {
		return NewFrameLinkNodeGen.create(source, label, frm);
	}

	public static NewFrame createNewFrame(SourceSection source, TermBuild t, TermBuild links) {
		return NewFrameNodeGen.create(source, GetScopeOfTermNodeGen.create(source, t), links);
	}

	public static NewFrameAddr createNewFrameAddr(SourceSection source, TermBuild frm, TermBuild dec) {
		return NewFrameAddrNodeGen.create(source, frm, dec);
	}

	public static Lookup createLookup(SourceSection source, TermBuild frm, TermBuild occurrence) {
		return LookupNodeGen.create(source, frm, occurrence);
	}

	public static InitProtoFrame createInitProtoFrame(SourceSection source) {
		return InitProtoFrameNodeGen.create(source);
	}

	public static IdentFrames createIdentFrames(SourceSection source, TermBuild frm1, TermBuild frm2) {
		return IdentFramesNodeGen.create(source, frm1, frm2);
	}

	public static GetAtAddr createGetAtAddr(SourceSection source, TermBuild addr) {
		return GetAtAddrNodeGen.create(source, addr);
	}

	public static DefaultValue createDefaultValue(SourceSection source) {
		return DefaultValueNodeGen.create(source);
	}

	public static CloneFrame createCloneFrame(SourceSection source, TermBuild frm) {
		return CloneFrameNodeGen.create(source, frm);
	}

	public static AddFrameLink createAddFrameLink(SourceSection source, TermBuild frm, TermBuild link) {
		return AddFrameLinkNodeGen.create(source, frm, link);
	}

}
