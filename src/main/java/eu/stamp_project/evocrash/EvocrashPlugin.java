package eu.stamp_project.evocrash;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class EvocrashPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

	    EvocrashPluginExtension extension = project.getExtensions().create("evocrash", EvocrashPluginExtension.class);

	    project.task("evocrash").doLast(task -> {
			System.out.println("Project libraries will be taken in the directory: "+extension.getLibs());
            System.out.println("The log file: "+extension.getLog() +" will be taken");
            System.out.println("The test will be generated in the directory: "+extension.getTest());
	    });
	}
}
