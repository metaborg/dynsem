/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.IToken;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.AbstractTermAttachment;
import org.spoofax.terms.attachments.OriginAttachment;

/**
 * @author vladvergu
 * 
 */
@Deprecated
public class NodeSource implements INodeSource {

	private final int line, column;
	private final String fragment;
	private final String filename;
	private final AbstractTermAttachment attachment;

	private NodeSource(ImploderAttachment imploderAttachment,
			AbstractTermAttachment attachment) {
		final IToken leftToken = imploderAttachment.getLeftToken();
		final IToken rightToken = imploderAttachment.getRightToken();
		line = leftToken.getLine() + 1;
		column = leftToken.getColumn() + 1;
		fragment = leftToken.getTokenizer().toString(leftToken, rightToken);
		filename = leftToken.getTokenizer().getFilename();
		this.attachment = attachment;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public String getCodeFragment() {
		return fragment;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void apply(IStrategoTerm term) {
		term.putAttachment(attachment);
	}

	public static INodeSource fromStrategoTerm(IStrategoTerm aterm) {
		AbstractTermAttachment attachment = null;
		ImploderAttachment imploderAttachment = null;

		if (aterm.getAttachment(OriginAttachment.TYPE) != null) {
			OriginAttachment originAttachment = aterm
					.getAttachment(OriginAttachment.TYPE);
			imploderAttachment = originAttachment.getOrigin().getAttachment(
					ImploderAttachment.TYPE);
			attachment = originAttachment;
		} else if (aterm.getAttachment(ImploderAttachment.TYPE) != null) {
			imploderAttachment = aterm.getAttachment(ImploderAttachment.TYPE);
			attachment = imploderAttachment;
		}

		return imploderAttachment != null ? new NodeSource(imploderAttachment,
				attachment) : null;
	}

}
