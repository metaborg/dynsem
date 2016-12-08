package dynsem.strategies;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.resource.IResourceService;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import com.google.inject.Injector;

public class fmakeexecutable_0_0 extends Strategy {
	public static fmakeexecutable_0_0 instance = new fmakeexecutable_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		if (!(current instanceof IStrategoString)) {
			return null;
		}

		final IContext mbContext = (IContext) context.contextObject();
		final Injector injector = mbContext.injector();
		final IResourceService resources = injector.getInstance(IResourceService.class);
		final FileObject fileObj = resources.resolve(Tools.asJavaString(current));
		try {
			fileObj.setExecutable(true, false);
		} catch (FileSystemException e1) {
			return null;
		}

		return current;
	}
}
