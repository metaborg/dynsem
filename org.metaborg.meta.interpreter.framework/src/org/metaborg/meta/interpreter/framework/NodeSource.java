/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.OriginAttachment;

/**
 * @author vladvergu
 * 
 */
public class NodeSource implements INodeSource {

	private final ImploderAttachment imploderInfo;
	private final OriginAttachment originInfo;

	public NodeSource(IStrategoTerm term) {
		this.imploderInfo = term.getAttachment(ImploderAttachment.TYPE);
		this.originInfo = term.getAttachment(OriginAttachment.TYPE);
	}

	@Override
	public void apply(IStrategoTerm term) {
		if ( imploderInfo != null ) {
			term.putAttachment(imploderInfo);
		}
		if ( originInfo != null ) {
			term.putAttachment(originInfo);
		}
	}
}
