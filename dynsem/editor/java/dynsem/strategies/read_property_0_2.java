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

public class read_property_0_2 extends Strategy {

	public static read_property_0_2 instance = new read_property_0_2();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			IStrategoTerm tpropname, IStrategoTerm defaultvalue) {

		if (!(current instanceof IStrategoString)) {
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

		return context.getFactory().makeString(propval);
	}

}
