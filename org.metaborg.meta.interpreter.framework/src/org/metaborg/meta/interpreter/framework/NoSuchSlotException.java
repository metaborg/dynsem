/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public class NoSuchSlotException extends InterpreterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -422199585469350776L;

	public NoSuchSlotException(Object slotName) {
		super("No such slot: " + slotName);
	}

}
