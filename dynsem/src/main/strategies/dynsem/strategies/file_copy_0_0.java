package dynsem.strategies;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class file_copy_0_0 extends Strategy {

	public static file_copy_0_0 instance = new file_copy_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		if (Tools.isTermTuple(current) && current.getSubtermCount() == 2) {
			IStrategoTuple tup = (IStrategoTuple) current;
			if (Tools.isTermString(tup.getSubterm(0))
					&& Tools.isTermString(tup.getSubterm(1))) {
				String srcF = Tools.stringAt(tup, 0).stringValue();
				String tgtF = Tools.stringAt(tup, 1).stringValue();
				try {
					FileUtils.copyFile(new File(srcF), new File(tgtF));
					return current;
				} catch (IOException e) {
					context.getIOAgent().printError(
							"Failed to copy: " + e.getMessage());
					return null;
				}
			}
		}
		context.getIOAgent().printError(
				"Failed to copy. Malformed term: " + current);
		return null;
	}

}
