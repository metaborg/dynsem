package dynsem.strategies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class read_property_0_3 extends Strategy {

	public static read_property_0_3 instance = new read_property_0_3();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			IStrategoTerm projectpath, IStrategoTerm tpropname,
			IStrategoTerm defaultvalue) {

		if (!(current instanceof IStrategoString)) {
			return null;
		}
		if (!(projectpath instanceof IStrategoString)) {
			return null;
		}
		if (!(tpropname instanceof IStrategoString)) {
			return null;
		}
		if (!(defaultvalue instanceof IStrategoString)) {
			return null;
		}

		Properties prop = new Properties();

		File propfile = new File(Tools.javaString(current));
		if (!(propfile.exists() && propfile.canRead())) {
			return defaultvalue;
		}

		try {
			prop.load(new FileInputStream(propfile));
		} catch (IOException e) {
			context.getIOAgent().printError(e.getMessage());
			return null;
		}

		String propval = prop.getProperty(Tools.javaString(tpropname));

		if (propval == null) {
			return defaultvalue;
		}

		File projectdir = new File(Tools.javaString(projectpath));

		if (!new File(propval).isAbsolute()) {
			propval = new File(projectdir, propval).getAbsolutePath();
		}

		return context.getFactory().makeString(propval);
	}

}
