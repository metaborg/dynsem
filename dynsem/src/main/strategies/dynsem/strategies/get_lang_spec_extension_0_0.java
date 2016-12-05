package dynsem.strategies;

import org.metaborg.core.config.ConfigException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.language.ILanguageService;
import org.metaborg.core.language.ResourceExtensionFacet;
import org.metaborg.spoofax.core.syntax.SyntaxFacet;
import org.metaborg.spoofax.meta.core.config.ISpoofaxLanguageSpecConfig;
import org.metaborg.spoofax.meta.core.project.ISpoofaxLanguageSpec;
import org.metaborg.spoofax.meta.core.project.ISpoofaxLanguageSpecService;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import com.google.inject.Injector;

public class get_lang_spec_extension_0_0 extends Strategy {
    public static get_lang_spec_extension_0_0 instance = new get_lang_spec_extension_0_0();

    
    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current) {
        final IContext mbContext = (IContext) context.contextObject();
        final Injector injector = mbContext.injector();

        try {
            final ISpoofaxLanguageSpecService langSpecService = injector.getInstance(ISpoofaxLanguageSpecService.class);
            final ISpoofaxLanguageSpec langSpec = langSpecService.get(mbContext.project());
            final ISpoofaxLanguageSpecConfig config = langSpec.config();

            // final LangSpecCommonPaths paths = new LangSpecCommonPaths(mbContext.location());
            // paths.targetMetaborgDir().resolveFile("sdf.tbl");

            final ILanguageService langService = injector.getInstance(ILanguageService.class);
            final ILanguageImpl langImpl = langService.getImpl(config.identifier());
            if(langImpl == null) {
                // Language is not loaded yet.
            }
            final ResourceExtensionFacet extFacet = langImpl.facet(ResourceExtensionFacet.class);
            if(extFacet == null) {
                // Language has no extensions.
            }
            final Iterable<String> ext = extFacet.extensions();
            
            final SyntaxFacet facet = langImpl.facet(SyntaxFacet.class);
        } catch(ConfigException e) {
            // Invalid configuration of language specification.
        }

        return null;
    }
}
