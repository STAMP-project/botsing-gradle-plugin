package eu.stamp_project.botsing;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import lombok.extern.log4j.Log4j;

@Log4j
public class BotsingGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        BotsingGradlePluginExtension extension = project.getExtensions().create("botsing",
                BotsingGradlePluginExtension.class);


        project.task("botsing").doLast(task -> {
            extension.checkProperties();
            try {
                extension.create(project);
            } catch (Exception e) {
                log.error("An exception occured during the generation: \n" + e.getMessage());
            }
        });

    }



}
