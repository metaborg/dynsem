package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermTransformer;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

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
// FIXME: the DynSemContext should be split in (1) static context --- reusable across multiple programs and (2) a
// dynamic (per-program run) context
public class DynSemContext {

	public static final String CONFIG_DSSPEC = "DYNSEMSPEC";
	public static final String CONFIG_STDIN = "STDIN";
	public static final String CONFIG_STDOUT = "STDOUT";
	public static final String CONFIG_STDERR = "STDERR";
	public static final String CONFIG_DEBUG = "DEBUG";
	public static final String CONFIG_BACKTRACK = "BACKTRACK";
	public static final String CONFIG_SAFECOMPS = "SAFECOMPS";
	public static final String CONFIG_TERMCACHE = "TERMCACHE";
	public static final String CONFIG_PARSER = "PARSER";
	public static final String CONFIG_TERMREGISTRY = "TERMREG";
	public static final String CONFIG_TERMTRANSFORMER = "TERMTRANSFORM";
	public static final String CONFIG_RULEREG = "RULEREG";
	public static final String CONFIG_MIMETYPE = "MIMETYPEOBJLANG";

	private final InputStream specification;

	private final InputStream input;
	private final PrintStream output;
	private final PrintStream err;

	private final IDynSemLanguageParser parser;
	private ITermTransformer termTransformer;
	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;

	private final String mimetype_lang;

	private Map<String, Object> properties;

	@CompilationFinal private boolean initialized;
	private final boolean safecomponents;
	private final boolean caching;
	private final boolean debug;

	public DynSemContext(Map<String, Object> config) {
		// TODO: there must be a smell here...
		this((IDynSemLanguageParser) config.get(CONFIG_PARSER), (ITermTransformer) config.get(CONFIG_TERMTRANSFORMER),
				(ITermRegistry) config.get(CONFIG_TERMREGISTRY), (RuleRegistry) config.get(CONFIG_RULEREG),
				(InputStream) config.get(CONFIG_STDIN), (PrintStream) config.get(CONFIG_STDOUT),
				(PrintStream) config.get(CONFIG_STDERR), (InputStream) config.get(CONFIG_DSSPEC),
				(String) config.get(CONFIG_MIMETYPE), (boolean) config.get(CONFIG_BACKTRACK),
				(boolean) config.get(CONFIG_SAFECOMPS), (boolean) config.get(CONFIG_TERMCACHE),
				(boolean) config.get(CONFIG_DEBUG), config);
	}

	private DynSemContext(IDynSemLanguageParser parser, ITermTransformer transformer, ITermRegistry termRegistry,
			RuleRegistry ruleRegistry, InputStream input, PrintStream output, PrintStream err,
			InputStream specification, String mimetype_lang, boolean backtracking, boolean safecomponents,
			boolean caching, boolean debug, Map<String, Object> config) {
		this.parser = parser;
		this.termTransformer = transformer;
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
		this.err = err;
		this.specification = specification;
		this.mimetype_lang = mimetype_lang;
		this.safecomponents = safecomponents;
		this.caching = caching;
		this.debug = debug;
		this.properties = new HashMap<>();
	}

	public void initialize(DynSemLanguage lang) {
		if (initialized)
			return;
		ruleRegistry.setLanguage(lang);
		ruleRegistry.populate(specification);
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
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

	public ITermTransformer getTermTransformer() {
		return termTransformer;
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
		final Object val = properties.get(prop);
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
		properties.put(prop, val);
		return this;
	}

	public DynSemContext writeProperties(Map<String, Object> props) {
		for (Entry<String, Object> prop : props.entrySet()) {
			properties.put(prop.getKey(), prop.getValue());
		}
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
		properties.remove(prop);
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

	/**
	 * 
	 * @return a reference to the error {@link OutputStream} configured for this {@link DynSemContext}
	 */
	public PrintStream getErr() {
		return err;
	}

	public String getMimeTypeObjLanguage() {
		return mimetype_lang;
	}

	public boolean isSafeComponentsEnabled() {
		return safecomponents;
	}

	public boolean isTermCachingEnabled() {
		return caching;
	}

	public boolean isDEBUG() {
		return debug;
	}

}
