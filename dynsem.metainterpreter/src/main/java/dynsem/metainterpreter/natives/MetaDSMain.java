package dynsem.metainterpreter.natives;

import org.metaborg.dynsem.metainterpreter.generated.dsMain;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

public class MetaDSMain {

	public static ITerm evaluate(IStrategoTerm programTerm, IStrategoTerm specTerm) throws Exception {
		ITermFactory factory = new TermFactory();
		IStrategoConstructor evalConstr = factory.makeConstructor("Eval", 2);
		IStrategoTerm evalTerm = factory.makeAppl(evalConstr, programTerm, specTerm);
		return (ITerm) dsMain.evaluate(evalTerm).result;
	}
	
}
