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

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Corresponds to the class with the same name in compiler/precompile.ts.
 *
 * @author tyleradams
 */
public final class CompilationSettings {

    @JsonProperty("propagateEnumConstants")
    private boolean propagateEnumConstants;

    @JsonProperty("removeComments")
    private boolean removeComments;

    @JsonProperty("watch")
    private boolean watch;

    @JsonProperty("noResolve")
    private boolean noResolve;

    @JsonProperty("allowAutomaticSemicolonInsertion")
    private boolean allowAutomaticSemicolonInsertion;

    @JsonProperty("noImplicitAny")
    private boolean noImplicitAny;

    @JsonProperty("noLib")
    private boolean noLib;

    @JsonProperty("codeGenTarget")
    private LanguageVersion codeGenTarget;

    @JsonProperty("moduleGenTarget")
    private ModuleGenTarget moduleGenTarget;

    @JsonProperty("outFileOption")
    private String outFileOption;

    @JsonProperty("outDirOption")
    private String outDirOption;

    @JsonProperty("mapSourceFiles")
    private boolean mapSourceFiles;

    @JsonProperty("mapRoot")
    private String mapRoot;

    @JsonProperty("sourceRoot")
    private String sourceRoot;

    @JsonProperty("generateDeclarationFiles")
    private boolean generateDeclarationFiles;

    @JsonProperty("useCaseSensitiveFileResolution")
    private boolean useCaseSensitiveFileResolution;

    @JsonProperty("gatherDiagnostics")
    private boolean gatherDiagnostics;

    @JsonProperty("updateTC")
    private boolean updateTC;

    @JsonProperty("codepage")
    private Integer codepage;

    public CompilationSettings() {
        this.propagateEnumConstants = false;
        this.removeComments = false;
        this.watch = false;
        this.noResolve = false;
        this.allowAutomaticSemicolonInsertion = true;
        this.noImplicitAny = false;

        this.noLib = false;

        this.codeGenTarget = LanguageVersion.ECMASCRIPT3;
        this.moduleGenTarget = ModuleGenTarget.UNSPECIFIED;

        this.outFileOption = "";
        this.outDirOption = "";
        this.mapSourceFiles = false;
        this.mapRoot = "";
        this.sourceRoot = "";
        this.generateDeclarationFiles = false;

        this.useCaseSensitiveFileResolution = false;
        this.gatherDiagnostics = false;

        this.updateTC = false;

        this.codepage = null;
    }

    public void setPropagateEnumConstants(boolean propagateEnumConstants) {
        this.propagateEnumConstants = propagateEnumConstants;
    }

    public void setRemoveComments(boolean removeComments) {
        this.removeComments = removeComments;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public void setNoResolve(boolean noResolve) {
        this.noResolve = noResolve;
    }

    public void setAllowAutomaticSemicolonInsertion(boolean allowAutomaticSemicolonInsertion) {
        this.allowAutomaticSemicolonInsertion = allowAutomaticSemicolonInsertion;
    }

    public void setNoImplicitAny(boolean noImplicitAny) {
        this.noImplicitAny = noImplicitAny;
    }

    public void setNoLib(boolean noLib) {
        this.noLib = noLib;
    }

    public void setCodeGenTarget(LanguageVersion codeGenTarget) {
        checkNotNull(codeGenTarget);

        this.codeGenTarget = codeGenTarget;
    }

    public void setModuleGenTarget(ModuleGenTarget moduleGenTarget) {
        checkNotNull(moduleGenTarget);

        this.moduleGenTarget = moduleGenTarget;
    }

    public void setOutFileOption(String outFileOption) {
        checkNotNull(outFileOption);

        this.outFileOption = outFileOption;
    }

    public void setOutDirOption(String outDirOption) {
        checkNotNull(outDirOption);

        this.outDirOption = outDirOption;
    }

    public void setMapSourceFiles(boolean mapSourceFiles) {
        this.mapSourceFiles = mapSourceFiles;
    }

    public void setMapRoot(String mapRoot) {
        checkNotNull(mapRoot);

        this.mapRoot = mapRoot;
    }

    public void setSourceRoot(String sourceRoot) {
        checkNotNull(sourceRoot);

        this.sourceRoot = sourceRoot;
    }

    public void setGenerateDeclarationFiles(boolean generateDeclarationFiles) {
        this.generateDeclarationFiles = generateDeclarationFiles;
    }

    public void setUseCaseSensitiveFileResolution(boolean useCaseSensitiveFileResolution) {
        this.useCaseSensitiveFileResolution = useCaseSensitiveFileResolution;
    }

    public void setGatherDiagnostics(boolean gatherDiagnostics) {
        this.gatherDiagnostics = gatherDiagnostics;
    }

    public void setUpdateTC(boolean updateTC) {
        this.updateTC = updateTC;
    }

    public void setCodepage(Integer codepage) {
        this.codepage = codepage;
    }
}
