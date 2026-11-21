package dev.stonecutterimports;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * Apply to each version subproject (the same place you apply loom). Registers {@code applyImports}.
 * Deliberately independent of Stonecutter's internals; see the README for how to trigger it on version switch.
 */
public class ImportsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            SourceSet main = project.getExtensions().getByType(SourceSetContainer.class).getByName("main");

            registerApplyImportsTask(project, main, "applyImports", true);

            TaskProvider<ApplyImportsTask> compileImports =
                    registerApplyImportsTask(project, main, "applyImportsForCompile", false);
            project.getTasks().matching(task -> task.getName().equals("compileJava"))
                    .configureEach(compileJava -> compileJava.dependsOn(compileImports));
        });
    }

    private static TaskProvider<ApplyImportsTask> registerApplyImportsTask(
            Project project, SourceSet main, String taskName, boolean sharedSources) {
        return project.getTasks().register(taskName, ApplyImportsTask.class, task -> {
            task.setGroup("stonecutter imports");
            task.setDescription(sharedSources
                    ? "Syncs and applies imports to shared sources for this version."
                    : "Syncs and applies imports to this version's compile sources.");
            task.getClasspath().from(main.getCompileClasspath());
            if (sharedSources) {
                task.getSourceDirs().from(project.getRootProject().file("src/main/java"));
            } else {
                // lazy: Stonecutter may adjust source dirs after this plugin is applied
                task.getSourceDirs().from(project.provider(() -> main.getJava().getSrcDirs()));
                task.dependsOn("stonecutterGenerate");
            }
            task.getCreateMissingBlocks().convention(false);
            task.doNotTrackState("Rewrites its own source inputs in place");
        });
    }
}
