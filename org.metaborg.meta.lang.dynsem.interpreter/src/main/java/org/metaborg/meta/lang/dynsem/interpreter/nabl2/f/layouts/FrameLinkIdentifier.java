package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;

public abstract class FrameLinkIdentifier {
	protected final ALabel linkLabel;

	public FrameLinkIdentifier(ALabel label) {
		this.linkLabel = label;
	}

}
