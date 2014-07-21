/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import org.spoofax.jsglr.client.imploder.IToken;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * @author vladvergu
 * 
 */
public class ImploderNodeSource implements INodeSource {

	private int line, column;
	private String fragment;
	private String filename;

	public ImploderNodeSource(ImploderAttachment imploderInfo) {
		final IToken leftToken = imploderInfo.getLeftToken();
		final IToken rightToken = imploderInfo.getRightToken();
		line = leftToken.getLine() + 1;
		column = leftToken.getColumn() + 1;
		fragment = leftToken.getTokenizer().toString(leftToken, rightToken);
		filename = leftToken.getTokenizer().getFilename();
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

}
