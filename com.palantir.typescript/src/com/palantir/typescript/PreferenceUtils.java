/*
 * Copyright 2013 Palantir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.typescript;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Utility class with preference management functions.
 *
 * @author rserafin
 */
public final class PreferenceUtils {
    public static String getProjectPreference(IProject project, String key) {
        IScopeContext projectScope = new ProjectScope(project);
        IEclipsePreferences projectPreferences = projectScope.getNode(TypeScriptPlugin.ID);

        return projectPreferences.get(key, "");
    }

    /**
     * Returns a preference store that contains the build path preferences (include and exclude
     * filters) for the given project, or the plugin build path preferences if none has been
     * configured for the project.
     *
     * @param project
     *            the project
     * @return a project-scoped {@link IPreferenceStore} to be used to read and write the build path
     *         preferences for the given project
     */
    public static IPreferenceStore getPreferenceStore(final IProject project) {
        final IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), TypeScriptPlugin.ID);
        store.setDefault(IPreferenceConstants.COMPILER_INCLUSION_PATTERNS, TypeScriptPlugin.getDefault()
            .getPreferenceStore().getString(IPreferenceConstants.COMPILER_INCLUSION_PATTERNS));
        store.setDefault(IPreferenceConstants.COMPILER_EXCLUSION_PATTERNS, TypeScriptPlugin.getDefault()
            .getPreferenceStore().getString(IPreferenceConstants.COMPILER_EXCLUSION_PATTERNS));
        return store;
    }

    private PreferenceUtils(){
        // hiding constructor.
    }
}
