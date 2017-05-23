package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.core.editor.IEditorRegistry;
import org.metaborg.core.editor.NullEditorRegistry;
import org.metaborg.core.project.ConfigBasedProjectService;
import org.metaborg.core.project.IProjectService;
import org.metaborg.spoofax.core.SpoofaxModule;

import com.google.inject.Singleton;

public class DynSemRunnerModule extends SpoofaxModule {

    @Override
    protected void bindProject() {
        bind(IProjectService.class).to(ConfigBasedProjectService.class).in(Singleton.class);
    }

    @Override
    protected void bindEditor() {
        bind(IEditorRegistry.class).to(NullEditorRegistry.class).in(Singleton.class);
    }

}