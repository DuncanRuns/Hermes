# stonecutter-imports

Per-version import management for Stonecutter projects. Write simple class names in every version branch;
the plugin imports whichever classes exist in the active version and are actually used there.

## What's verified and what isn't

| Part | Status |
|---|---|
| `ImportProcessor` (block parsing, sync, apply, lexer) | Compiled and tested (`SelfCheck`), including a round trip on a real 550-line mod file |
| `ClassIndex` (jar/dir/source indexing) | Compiled and exercised against Loom classpaths; indexed 29,404-42,834 classes across versions. |
| `ImportsPlugin` / `ApplyImportsTask` (Gradle layer) | Compiled and run with Gradle 9.9.1; classpath indexing verified against Loom. |
| Wiring to Stonecutter 0.10 | Verified: active sources use shared roots; non-active version builds compile generated sources. |

## The format

Directly below the imports:

```java
import com.google.gson.Gson;
import net.minecraft.registry.RegistryOps;

/*!imports
com.google.gson.Gson
net.minecraft.registry.RegistryOps
net.minecraft.util.dynamic.RegistryOps
*/
```

On each run (`applyImports`):

1. **sync**: single-type imports currently in the import section are merged into the block (union, sorted).
2. **apply**: block entries are kept only if (a) the class exists on this version's classpath or in the
   project's own sources, and (b) its simple name appears in live code (outside comments, strings, and
   not after a `.`).
3. The import section is regenerated: third-party, blank line, `java`/`javax`, blank line, static.

Wildcard (`a.b.*`) and static imports are never removed and never added to the block.

## Rules the plugin enforces

- The import section may contain **only** import statements and blank lines. A comment or Stonecutter
  condition between imports is an error (with a line number). Put conditional imports in the block instead.
- Do not nest the `/*!imports` block inside another block comment. For a Java file wrapped in a
  Stonecutter conditional comment, place the managed block after the outer `*///?}` closer.
- If two entries with the same simple name are both available and used in one version, it's an error
  rather than a silent pick.
- Files without a block are untouched, unless you run `applyImports --create-blocks` once to bootstrap.
- `java.*` and `javax.*` are always treated as available.
- A source file in your own project counts as available only if its class name appears in live code,
  so a file Stonecutter has fully commented out is treated as absent.

## Setup

`settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("stonecutter-imports") }
```

Version `build.gradle.kts` (next to loom):
```kotlin
plugins { id("dev.stonecutterimports") }
```

Bootstrap once, with the version you currently have active:
```
./gradlew :<active-version>:applyImports --create-blocks
```

Stonecutter 0.10 creates root tasks named `Set active project to <version>` and direct switch tasks
named `stonecutterSwitchTo<version>`. Both finalize the target project's `applyImports` task:
```kotlin
tasks.matching {
    it.name.startsWith("Set active project to ") || it.name.startsWith("stonecutterSwitchTo")
}.configureEach {
    val version = if (name.startsWith("Set active project to ")) {
        name.removePrefix("Set active project to ").trim()
    } else {
        name.removePrefix("stonecutterSwitchTo")
    }
    finalizedBy(":$version:applyImports")
}
```

For non-active version projects, Stonecutter generates processed sources under
`build/generated/stonecutter/<source set>`. The plugin makes `applyImports` depend on the matching
`stonecutterGenerate` task and makes `compileJava` depend on `applyImportsForCompile`, so those
generated copies receive version-correct imports before compilation. `applyImports` always targets
the shared `src/main/java` tree, allowing the switch finalizer to use the target version's classpath
even though Gradle configured the source set before the switch executes.

## Stonecutter 0.10 behavior

- The active version reads shared source roots; non-active versions compile generated copies at
   `build/generated/stonecutter/<source set>`.
- A standalone `/*!imports` is preserved as a normal Java block comment. It must not be nested inside
  another block comment, such as a Java file wrapped in `/*...*///?}`.
- Switch tasks are `Set active project to <version>`; version task paths are `:<version>`.
- The Loom compile classpath includes the mapped Minecraft jar (the bootstrap indexed 30,662 classes).

**Workflow note:** a new version's classes only enter the block once they've been imported once while that
   version is active (IDE auto-import, then switch away and back), or you add them to the block by hand.

Run `./gradlew buildAndCollect` from the root to build and collect artifacts for all 43 configured
version projects; the imports task runs before each Java compilation.

## Running the checks

```
./gradlew selfCheck
```
