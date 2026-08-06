package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import com.jetbrains.jsonSchema.remote.JsonFileResolver

/**
 * Applies the Chromia model schema to Chromia settings files.
 *
 * The schema is registered in the JSON Schema Store under the names `chromia.yml`/`chromia.yaml`,
 * so the platform's catalog already covers the default name. It matches on file name alone,
 * though, and `chr` reads any `-s/--settings` file: an `atbash-dev.yml` gets nothing unless the
 * user maps it by hand, which nobody does. This provider closes that gap by reusing the same
 * qualification the version resolver applies — a `.yml` with a top-level `blockchains` section
 * (see [ChromiaSettingsFiles]) — so alternate names validate exactly like `chromia.yml`.
 */
class ChromiaSchemaProviderFactory : JsonSchemaProviderFactory, DumbAware {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> =
        listOf(ChromiaSchemaFileProvider(project))
}

private class ChromiaSchemaFileProvider(private val project: Project) : JsonSchemaFileProvider {
    /**
     * Qualification parses the file, unlike the cache-only peek the icon provider settles for: a
     * settings file must validate the first time it is opened, not once something else has parsed
     * it. The name gate keeps that off every YAML in the project, and the resolver caches per path.
     */
    override fun isAvailable(file: VirtualFile): Boolean =
        !file.isDirectory &&
                ChromiaSettingsFiles.isYmlName(file.name) &&
                RellVersionResolver.getInstance(project).isSettingsCandidate(file)

    override fun getName(): String = SCHEMA_NAME

    override fun getPresentableName(): String = SCHEMA_NAME

    /** Resolves to the platform's downloaded-and-cached copy of [SCHEMA_URL]. */
    override fun getSchemaFile(): VirtualFile? = JsonFileResolver.urlToFile(SCHEMA_URL)

    override fun getSchemaType(): SchemaType = SchemaType.remoteSchema

    override fun getSchemaVersion(): JsonSchemaVersion = JsonSchemaVersion.SCHEMA_2020_12

    override fun getRemoteSource(): String = SCHEMA_URL

    private companion object {
        /** The name the JSON Schema Store catalog gives this schema; shown in the status bar. */
        const val SCHEMA_NAME = "Chromia Model"

        /**
         * The same URL the catalog entry points at, so a project pinning the schema by hand and one
         * relying on this provider validate against identical bytes. Served from the toolchain
         * repository rather than bundled: a copy in the distribution would freeze at release time
         * and reject keys `chr` had already learned.
         */
        const val SCHEMA_URL =
            "https://gitlab.com/chromaway/core-tools/chromia-cli-tools/-/raw/dev/" +
                    "chromia-build-tools/src/main/resources/chromia-model-schema.json"
    }
}
