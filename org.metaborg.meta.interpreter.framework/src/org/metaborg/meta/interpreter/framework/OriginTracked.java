/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 *
 */
@Deprecated
public interface OriginTracked {
	public void setSourceInfo(INodeSource src);

	public INodeSource getSourceInfo();
}
