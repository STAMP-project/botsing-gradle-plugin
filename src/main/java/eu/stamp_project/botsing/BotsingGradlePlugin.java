package eu.stamp_project.botsing;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BotsingGradlePlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

	    BotsingGradlePluginExtension extension = project.getExtensions().create("botsing", BotsingGradlePluginExtension.class);

	    project.task("botsing").doLast(task -> {
	        extension.checkProperties();
			try {
				 extension.create();
			}catch (Exception e){
				System.err.println("An exception occured during the generation: \n"+e.getMessage());
			}
	    });
	}
}
