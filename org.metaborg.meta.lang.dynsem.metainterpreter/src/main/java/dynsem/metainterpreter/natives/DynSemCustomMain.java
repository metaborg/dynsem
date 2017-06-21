package dynsem.metainterpreter.natives;

import org.metaborg.dynsem.metainterpreter.generated.dsMain;
import org.metaborg.dynsem.metainterpreter.generated.terms.ITTerm;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

public class DynSemCustomMain {

	public static ITTerm evaluate(IStrategoTerm programTerm, IStrategoTerm specTerm) throws Exception {
		ITermFactory factory = new TermFactory();
		IStrategoConstructor evalConstr = factory.makeConstructor("Eval", 2);
		IStrategoTerm evalTerm = factory.makeAppl(evalConstr, programTerm, specTerm);
		return (ITTerm) dsMain.evaluate(evalTerm).result;
	}
	
}
