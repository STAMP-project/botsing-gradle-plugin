package eu.stamp_project.evocrash;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class EvocrashPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

	    EvocrashPluginExtension extension = project.getExtensions().create("evocrash", EvocrashPluginExtension.class);

	    project.task("evocrash").doLast(task -> {
		    System.out.println("Hello, " + extension.getGreeter());
		    System.out.println("I have a message for you: " + extension.getMessage()); }
		);
	}
}
