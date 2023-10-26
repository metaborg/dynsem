package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.core.editor.IEditorRegistry;
import org.metaborg.core.editor.NullEditorRegistry;
import org.metaborg.core.project.ConfigBasedProjectService;
import org.metaborg.core.project.IProjectService;
import org.metaborg.spoofax.core.SpoofaxModule;

import com.google.inject.Singleton;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class DynSemRunnerModule extends SpoofaxModule {

	@Override
	@TruffleBoundary
	protected void bindProject() {
		bind(IProjectService.class).to(ConfigBasedProjectService.class).in(Singleton.class);
	}

	@Override
	@TruffleBoundary
	protected void bindEditor() {
		bind(IEditorRegistry.class).to(NullEditorRegistry.class).in(Singleton.class);
	}

}