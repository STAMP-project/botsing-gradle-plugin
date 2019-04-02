package eu.stamp_project.botsing;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;


public class BotsingGradlePluginExtension {

    private static Logger logger = LoggerFactory.getLogger(BotsingRunner.class.getName());

    private final String botsingConfigName = "botsingConfig";

    //Required parameters
    @Getter
    @Setter
    private String logPath = null;

    @Getter
    @Setter
    private String targetFrame = null;

    //Optional parameters
    @Getter
    @Setter
    private String output = null;

    @Getter
    @Setter
    private String searchBudget = null;

    @Getter
    @Setter
    private String population = null;

    private List<String> commands;

    public BotsingGradlePluginExtension() {
        commands = new ArrayList<>();
    }

    public void checkProperties() {

        logger.info("The following parameters will be used: ");
        checkRequiredParameterContent("logPath", logPath);
        checkIfDirectoryExists(logPath);
        checkRequiredParameterContent("targetFrame", targetFrame);

        displayOptionalParameterIfNotNull("output", output);
        Optional.ofNullable(output).ifPresent(path -> checkIfDirectoryExists(path));
        displayOptionalParameterIfNotNull("searchBudget", searchBudget);
        displayOptionalParameterIfNotNull("population", population);
    }

    public void create(Project project) {

        //required parameters
        addRequiredParameter("target_frame", targetFrame);
        addRequiredParameter("crash_log", logPath);
        addRequiredParameter("project_cp", getDependencies(project));

        //optional parameters
        Optional.ofNullable(output)
                .ifPresent(value -> commands.add(String.format("-Dtest_dir=%s", Paths.get(output).toString())));
        Optional.ofNullable(searchBudget)
                .ifPresent(val -> commands.add(String.format("-Dsearch_budget=%s", searchBudget)));
        Optional.ofNullable(population).ifPresent(val -> commands.add(String.format("-Dpopulation=%s", population)));

        String[] args = commands.toArray(new String[0]);

        for (int i =0 ;i<args.length;i++) {
            logger.debug("commands : " + args[i]);
        }

        try {
            File botsingReproductionJar = addBotsingDependencies(project);
            BotsingRunner.executeBotsing(project.getBuildDir(), botsingReproductionJar, commands);
        } catch (Throwable e) {
            logger.error("An error happened while running Botsing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void addRequiredParameter(String parameter, String value) {
        commands.add(String.format("-%s", parameter));
        commands.add(value);
    }

    private void checkIfDirectoryExists(String directoryPath) {
        if (!new File(directoryPath).exists()) {
            throw new RuntimeException(String.format("Bad path %s", directoryPath));
        }
    }

    private void checkRequiredParameterContent(String parameterName, String parameter) {
        logger.info(String.format(" - %s: %s",
                parameterName,
                Optional.ofNullable(parameter)
                        .orElseThrow(() -> new RuntimeException(String.format("Impossible to run Botsing, %s is not set",
                                parameterName)))));
    }

    private void displayOptionalParameterIfNotNull(String parameterName, String parameter) {
        Optional.ofNullable(parameter)
                .ifPresent(content -> logger.info(String.format("%s: %s", parameterName, content)));
    }

    private String getDependencies(Project project) {

        return project.getConfigurations()
                .stream()
                .filter(configuration -> ! configuration.getName().equals(botsingConfigName))
                .map(configuration -> configuration.resolve()
                        .stream()
                        .map(File::getAbsolutePath)
                        .filter(file -> file.endsWith(".jar"))
                        .collect(Collectors.joining(File.pathSeparator)))
                .collect(Collectors.joining(File.pathSeparator))
                .substring(1);

    }

    private File addBotsingDependencies(Project project){
        Configuration botsingConfig = project.getConfigurations().create(botsingConfigName);
        botsingConfig.getDependencies().add(project.getDependencies().create("eu.stamp-project:botsing-reproduction:1.0.4"));
        return project.getConfigurations().getByName(botsingConfigName).resolve()
                .stream()
                .collect(Collectors.toList())
                .get(0);
    }
}
