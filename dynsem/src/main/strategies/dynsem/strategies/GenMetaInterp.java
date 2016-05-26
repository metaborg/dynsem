package dynsem.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;

import dynsem.trans.ds_to_interp_editor_0_0;
import dynsem.trans.parse_file_0_0;

public class GenMetaInterp {

	public static void main(String[] args) {

		Context ctx = dynsem.trans.Main.init();
		ITermFactory tf = ctx.getFactory();

		IStrategoTerm ast = parse_file_0_0.instance.invoke(ctx,
				tf.makeString(args[0]));
		IStrategoTerm position = tf.makeList();
		IStrategoTerm path = tf.makeString(args[0]);
		IStrategoTerm projectpath = tf.makeString(args[1]);

		IStrategoTerm tup = tf.makeTuple(ast, position, ast, path, projectpath);
		ds_to_interp_editor_0_0.instance.invoke(ctx, tup);
	}

}
