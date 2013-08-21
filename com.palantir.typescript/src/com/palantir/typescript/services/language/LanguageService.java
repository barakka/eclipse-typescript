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

package com.palantir.typescript.services.language;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.palantir.typescript.IPreferenceConstants;
import com.palantir.typescript.TypeScriptPlugin;
import com.palantir.typescript.services.Bridge;
import com.palantir.typescript.services.Request;

/**
 * The language service.
 * <p>
 * This service provides code completion, formatting, compiling, etc...
 *
 * @author tyleradams
 */
public final class LanguageService {

    private static final String SERVICE = "language";

    private final Bridge bridge;
    private final MyPropertyChangeListener preferencesListener;

    public LanguageService(String fileName) {
        this(ImmutableList.of(fileName));
    }

    public LanguageService(IProject project) {
        this(getProjectFiles(project));
    }

    private LanguageService(List<String> fileNames) {
        checkNotNull(fileNames);

        this.bridge = new Bridge();
        this.preferencesListener = new MyPropertyChangeListener();

        this.addDefaultLibrary();
        this.addFiles(fileNames);
        this.updateCompilationSettings();

        TypeScriptPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this.preferencesListener);
    }

    public CompletionInfo getCompletionsAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getCompletionsAtPosition", fileName, position);
        return this.bridge.call(request, CompletionInfo.class);
    }

    public List<DefinitionInfo> getDefinitionAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getDefinitionAtPosition", fileName, position);
        CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, DefinitionInfo.class);
        return this.bridge.call(request, resultType);
    }

    public Map<String, List<Diagnostic>> getAllDiagnostics() {
        Request request = new Request(SERVICE, "getAllDiagnostics");
        JavaType stringType = TypeFactory.defaultInstance().uncheckedSimpleType(String.class);
        CollectionType diagnosticListType = TypeFactory.defaultInstance().constructCollectionType(List.class, Diagnostic.class);
        MapType returnType = TypeFactory.defaultInstance().constructMapType(Map.class, stringType, diagnosticListType);
        return LanguageService.this.bridge.call(request, returnType);
    }

    public List<Diagnostic> getDiagnostics(String fileName) {
        checkNotNull(fileName);

        Request request = new Request(SERVICE, "getDiagnostics", fileName);
        CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, Diagnostic.class);
        return this.bridge.call(request, resultType);
    }

    public List<String> getEmitOutput(String fileName) {
        checkNotNull(fileName);

        Request request = new Request(SERVICE, "getEmitOutput", fileName);
        CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);
        return this.bridge.call(request, resultType);
    }

    public List<TextEdit> getFormattingEditsForRange(String fileName, int minChar, int limChar, FormatCodeOptions options) {
        checkNotNull(fileName);
        checkArgument(minChar >= 0);
        checkArgument(limChar >= 0);
        checkNotNull(options);

        Request request = new Request(SERVICE, "getFormattingEditsForRange", fileName, minChar, limChar, options);
        CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, TextEdit.class);
        return this.bridge.call(request, resultType);
    }

    public int getIndentationAtPosition(String fileName, int offset, EditorOptions options) {
        checkNotNull(fileName);
        checkArgument(offset >= 0);
        checkNotNull(options);

        Request request = new Request(SERVICE, "getIndentationAtPosition", fileName, offset, options);
        return this.bridge.call(request, Integer.class);
    }

    public SpanInfo getNameOrDottedNameSpan(String fileName, int startPos, int endPos) {
        checkNotNull(fileName);
        checkArgument(startPos >= 0);
        checkArgument(endPos >= 0);

        Request request = new Request(SERVICE, "getNameOrDottedNameSpan", fileName, startPos, endPos);
        return this.bridge.call(request, SpanInfo.class);
    }

    public List<ReferenceEntry> getOccurrencesAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getOccurrencesAtPosition", fileName, position);
        CollectionType returnType = TypeFactory.defaultInstance().constructCollectionType(List.class, ReferenceEntry.class);
        return this.bridge.call(request, returnType);
    }

    public List<ReferenceEntry> getReferencesAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getReferencesAtPosition", fileName, position);
        CollectionType returnType = TypeFactory.defaultInstance().constructCollectionType(List.class, ReferenceEntry.class);
        return this.bridge.call(request, returnType);
    }

    public List<NavigateToItem> getScriptLexicalStructure(String fileName) {
        checkNotNull(fileName);

        Request request = new Request(SERVICE, "getScriptLexicalStructure", fileName);
        CollectionType returnType = TypeFactory.defaultInstance().constructCollectionType(List.class, NavigateToItem.class);
        return this.bridge.call(request, returnType);
    }

    public SignatureInfo getSignatureAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getSignatureAtPosition", fileName, position);
        return this.bridge.call(request, SignatureInfo.class);
    }

    public TypeInfo getTypeAtPosition(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "getTypeAtPosition", fileName, position);
        return this.bridge.call(request, TypeInfo.class);
    }

    public List<Reference> findReferences(String fileName, int position) {
        checkNotNull(fileName);
        checkArgument(position >= 0);

        Request request = new Request(SERVICE, "findReferences", fileName, position);
        CollectionType returnType = TypeFactory.defaultInstance().constructCollectionType(List.class, Reference.class);
        return this.bridge.call(request, returnType);
    }

    public void editFile(String fileName, int offset, int length, String replacementText) {
        checkNotNull(fileName);
        checkArgument(offset >= 0);
        checkArgument(length >= 0);
        checkNotNull(replacementText);

        Request request = new Request(SERVICE, "editFile", fileName, offset, length, replacementText);
        this.bridge.call(request, Void.class);
    }

    public void setFileOpen(String fileName, boolean open) {
        checkNotNull(fileName);

        Request request = new Request(SERVICE, "setFileOpen", fileName, open);
        this.bridge.call(request, Void.class);
    }

    public void updateFiles(List<FileDelta> fileDeltas) {
        checkNotNull(fileDeltas);

        if (!fileDeltas.isEmpty()) {
            Request request = new Request(SERVICE, "updateFiles", fileDeltas);

            LanguageService.this.bridge.call(request, Void.class);
        }
    }

    public void dispose() {
        TypeScriptPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this.preferencesListener);
        this.bridge.dispose();
    }

    private void addDefaultLibrary() {
        String libraryContents;
        try {
            libraryContents = Resources.toString(LanguageService.class.getResource("lib.d.ts"), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Request request = new Request(SERVICE, "addDefaultLibrary", libraryContents);
        this.bridge.call(request, Void.class);
    }

    private void addFiles(List<String> fileNames) {
        Request request = new Request(SERVICE, "addFiles", fileNames);
        this.bridge.call(request, Void.class);
    }

    private static List<String> getProjectFiles(final IProject project) {
        final ImmutableList.Builder<String> fileNames = ImmutableList.builder();

        try {
            project.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) throws CoreException {
                    if (resource.getType() == IResource.FILE && resource.getName().endsWith((".ts"))) {
                        String fileName = resource.getRawLocation().toOSString();

                        fileNames.add(fileName);
                    }

                    return true;
                }
            });
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }

        return fileNames.build();
    }

    private void updateCompilationSettings() {
        IPreferenceStore preferenceStore = TypeScriptPlugin.getDefault().getPreferenceStore();
        CompilationSettings compilationSettings = new CompilationSettings(
            preferenceStore.getBoolean(IPreferenceConstants.COMPILER_NO_LIB),
            LanguageVersion.valueOf(preferenceStore.getString(IPreferenceConstants.COMPILER_CODE_GEN_TARGET)),
            ModuleGenTarget.valueOf(preferenceStore.getString(IPreferenceConstants.COMPILER_MODULE_GEN_TARGET)),
            preferenceStore.getBoolean(IPreferenceConstants.COMPILER_MAP_SOURCE_FILES),
            preferenceStore.getBoolean(IPreferenceConstants.COMPILER_REMOVE_COMMENTS));

        Request request = new Request(SERVICE, "setCompilationSettings", compilationSettings);
        this.bridge.call(request, Void.class);
    }

    private final class MyPropertyChangeListener implements IPropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String property = event.getProperty();

            if (IPreferenceConstants.COMPILER_PREFERENCES.contains(property)) {
                updateCompilationSettings();
            }
        }
    }
}
