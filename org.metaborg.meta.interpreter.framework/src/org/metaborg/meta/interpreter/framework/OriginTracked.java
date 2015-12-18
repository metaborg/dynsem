/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 *
 */
public interface OriginTracked {
	public void setSourceInfo(INodeSource src);

	public INodeSource getSourceInfo();
}
