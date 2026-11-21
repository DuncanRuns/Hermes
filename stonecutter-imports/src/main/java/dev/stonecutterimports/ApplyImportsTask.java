package dev.stonecutterimports;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Syncs and applies managed imports to every .java file under {@link #getSourceDirs()}, using
 * {@link #getClasspath()} (this version's Minecraft + libraries) plus the source dirs themselves
 * to decide which imports are available.
 */
public abstract class ApplyImportsTask extends DefaultTask {

    /** Compile classpath of the version this task runs for. */
    @Classpath
    public abstract ConfigurableFileCollection getClasspath();

    /** Directories whose .java files are rewritten in place. Not tracked: the task edits its own inputs. */
    @Internal
    public abstract ConfigurableFileCollection getSourceDirs();

    @Input
    @Option(option = "create-blocks",
            description = "Add an /*!imports block to files that have imports but no block yet (bootstrap).")
    public abstract Property<Boolean> getCreateMissingBlocks();

    @TaskAction
    public void run() throws IOException {
        ClassIndex index = new ClassIndex();
        for (File f : getClasspath()) index.addClasspathEntry(f.toPath());
        for (File d : getSourceDirs()) index.addSourceRoot(d.toPath());

        ImportProcessor processor = new ImportProcessor(getCreateMissingBlocks().getOrElse(false));
        int scanned = 0, changed = 0;
        List<String> errors = new ArrayList<>();

        for (File dir : getSourceDirs()) {
            if (!dir.isDirectory()) continue;
            List<Path> files;
            try (Stream<Path> s = Files.walk(dir.toPath())) {
                files = s.filter(f -> f.toString().endsWith(".java")).collect(Collectors.toList());
            }
            for (Path file : files) {
                scanned++;
                String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                String out;
                try {
                    out = processor.process(text, index);
                } catch (IllegalStateException e) {
                    errors.add(file + ": " + e.getMessage());
                    continue;
                }
                if (!out.equals(text)) {
                    Files.write(file, out.getBytes(StandardCharsets.UTF_8));
                    changed++;
                }
            }
        }

        getLogger().lifecycle("applyImports [{}]: {} classes indexed, {} files scanned, {} updated",
                getProject().getPath(), index.size(), scanned, changed);
        if (!errors.isEmpty()) {
            throw new GradleException("Import management failed for " + errors.size() + " file(s):\n"
                    + String.join("\n", errors));
        }
    }
}
