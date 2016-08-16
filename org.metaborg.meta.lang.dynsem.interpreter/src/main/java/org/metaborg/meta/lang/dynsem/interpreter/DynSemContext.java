package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;

/**
 * Interpreter context which maintains runtime-specific entities. Instances of {@link DynSemContext} are primarily
 * responsible with maintaining references to the active {@link IDynSemLanguageParser}, {@link ITermRegistry},
 * {@link RuleRegistry} and input and output streams.
 * 
 * To a particular interpreter instance corresponds exactly one {@link DynSemContext} instance.
 * 
 * @author vladvergu
 *
 */
public class DynSemContext {
	public static DynSemLanguage LANGUAGE;

	private final InputStream input;
	private final PrintStream output;

	private final IDynSemLanguageParser parser;
	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;

	private final Map<String, Object> config;

	public DynSemContext(IDynSemLanguageParser parser, ITermRegistry termRegistry, RuleRegistry ruleRegistry) {
		this(parser, termRegistry, ruleRegistry, System.in, System.out, new HashMap<String, Object>());
	}

	public DynSemContext(IDynSemLanguageParser parser, ITermRegistry termRegistry, RuleRegistry ruleRegistry,
			InputStream input, PrintStream output, Map<String, Object> config) {
		this.parser = parser;
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
		this.config = config;
	}

	/**
	 * Access the configured parser for the interpreter language
	 * 
	 * @return
	 */
	public IDynSemLanguageParser getParser() {
		return parser;
	}

	/**
	 * Access the active {@link RuleRegistry} for the interpreted language
	 * 
	 * @return
	 */
	public RuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}

	/**
	 * Access the active {@link ITermRegistry} for the interpreted language
	 * 
	 * @return
	 */
	public ITermRegistry getTermRegistry() {
		return termRegistry;
	}

	/**
	 * Read property from the custom property store maintained by this {@link DynSemContext}.
	 * 
	 * @param prop
	 *            The property to read
	 * @param defaultValue
	 *            The default value to return if the property is not mapped in the property store
	 * @return The value associated with this property in the property store, or <code>defaultValue</code>.
	 */
	public Object readProperty(String prop, Object defaultValue) {
		final Object val = config.get(prop);
		if (val != null) {
			return val;
		}
		return defaultValue;
	}

	/**
	 * Write the value for the property in the custom property store maintained by this instance of
	 * {@link DynSemContext}.
	 * 
	 * @param prop
	 *            The property to associate
	 * @param val
	 *            The value to associate with the property
	 * @return A reference to this {@link DynSemContext}
	 */
	public DynSemContext writeProperty(String prop, Object val) {
		config.put(prop, val);
		return this;
	}

	/**
	 * Remove the given property from the property store
	 * 
	 * @param prop
	 *            The property to remove from the property store
	 * @return A reference to this {@link DynSemContext}
	 */
	public DynSemContext deleteProperty(String prop) {
		config.remove(prop);
		return this;
	}

	/**
	 * 
	 * @return a reference to the {@link InputStream} configured for this {@link DynSemContext}
	 */
	public InputStream getInput() {
		return input;
	}

	/**
	 * 
	 * @return a reference to the {@link OutputStream} configured for this {@link DynSemContext}
	 */
	public PrintStream getOutput() {
		return output;
	}

}
