package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.metaborg.core.MetaborgException;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FramePrototypesLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermTransformer;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.shell.CLIUtils;

import com.google.inject.Module;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.object.DynamicObject;

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
public final class DynSemContext {


	private final InputStream specification;

	private final InputStream input;
	private final PrintStream output;
	private final PrintStream err;

	private final IDynSemLanguageParser parser;
	private ITermTransformer termTransformer;
	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;

	private Map<String, Object> properties;

	@CompilationFinal private boolean initialized;
	private final boolean nativeframes;

	private Spoofax S;
	private CLIUtils cli;

	public DynSemContext(IDynSemLanguageParser parser, ITermTransformer transformer, ITermRegistry termRegistry,
			RuleRegistry ruleRegistry, InputStream input, OutputStream output, OutputStream err,
			InputStream specification, boolean nativeframes) {
		this.parser = parser;
		this.termTransformer = transformer;
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = new PrintStream(output);
		this.err = new PrintStream(err);
		this.specification = specification;
		this.nativeframes = nativeframes;
		this.properties = new HashMap<>();
	}

	public synchronized void initialize(DynSemLanguage lang) throws MetaborgException {
		if (initialized)
			return;
		ruleRegistry.setLanguage(lang);
		ruleRegistry.populate(specification, getTermRegistry());
		initializeSpoofax();
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	@TruffleBoundary
	private void initializeSpoofax() throws MetaborgException {
		S = new Spoofax(new DynSemRunnerModule(), new Module[0]);
		cli = new CLIUtils(S);
		cli.loadLanguagesFromPath();
	}

	@TruffleBoundary
	private void closeSpoofax() {
		if (S != null) {
			S.close();
		}
	}

	public Spoofax getSpoofax() {
		return S;
	}

	public CLIUtils getSpoofaxCLIUtils() {
		return cli;
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
	public IRuleRegistry getRuleRegistry() {
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
	@CompilationFinal private DynamicObject nabl2solution;

	public boolean hasNaBL2Solution() {
		return nabl2solution != null;
	}

	public void setNabl2Solution(DynamicObject nabl2) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		assert NaBL2LayoutImpl.INSTANCE.isNaBL2(nabl2);
		this.nabl2solution = nabl2;
	}

	public DynamicObject getNaBL2Solution() {
		return Objects.requireNonNull(nabl2solution,
				"No NaBL2 context available. Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
	}

	private final DynamicObject protoFrames = FramePrototypesLayoutImpl.INSTANCE.createFramePrototypes();

	public void addProtoFrame(ScopeIdentifier ident, DynamicObject frameProto) {
		assert FrameLayoutImpl.INSTANCE.isFrame(frameProto);
		protoFrames.define(ident, frameProto);
	}

	public DynamicObject getProtoFrame(ScopeIdentifier ident) {
		return (DynamicObject) protoFrames.get(ident);
	}

	public boolean isNativeFramesEnabled() {
		return nativeframes;
	}

}
