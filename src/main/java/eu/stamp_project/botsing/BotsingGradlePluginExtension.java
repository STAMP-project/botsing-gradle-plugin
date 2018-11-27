package eu.stamp_project.botsing;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import eu.stamp.botsing.Botsing;
import lombok.Getter;
import lombok.Setter;

public class BotsingGradlePluginExtension {

    //Required parameters
    @Getter @Setter
    private String logPath = null;
    @Getter @Setter
    private String libsPath =null;
    @Getter @Setter
    private String targetFrame = null;

    //Optional parameters
    @Getter @Setter
    private String output= null;
    @Getter @Setter
    private String searchBudget= null;
    @Getter @Setter
    private String population= null;


    private List<String> commands;

    public BotsingGradlePluginExtension(){
        commands = new ArrayList<>();
    }

    public void checkProperties(){

        System.out.println("The following parameters will be used: ");
        checkRequiredParameterContent("logPath",logPath);
        checkIfDirectoryExists(logPath);
        checkRequiredParameterContent("libsPath",libsPath);
        checkIfDirectoryExists(libsPath);
        checkRequiredParameterContent("targetFrame",targetFrame);

        displayOptionalParameterIfNotNull("output",output);
        Optional.ofNullable(output).ifPresent(path -> checkIfDirectoryExists(path));
        displayOptionalParameterIfNotNull("searchBudget",searchBudget);
        displayOptionalParameterIfNotNull("population",population);
    }

    public void create() {

        String logPath = this.logPath;

        //required parameters
        addRequiredParameter("target_frame", targetFrame);
        addRequiredParameter("crash_log",logPath);
        addRequiredParameter("projectCP", libsPath);

        //optional parameters
        Optional.ofNullable(output).ifPresent(value -> commands.add(String.format("-Dtest_dir=%s",Paths.get(output).toString())));
        Optional.ofNullable(searchBudget).ifPresent(val -> commands.add(String.format("-Dsearch_budget=%s",searchBudget)));
        Optional.ofNullable(population).ifPresent(val -> commands.add(String.format("-Dpopulation=%s",population)));

        System.out.println(commands);

        try {
            new Botsing().parseCommandLine(commands.stream().toArray(String[]::new));
        }catch (Exception e){
            System.out.println("An error happened while generating a new output: "+e.getMessage());
        }
    }

    private void addRequiredParameter(String parameter, String value){
        commands.add(String.format("-%s",parameter));
        commands.add(value);
    }

    private void addProperty(String commandName, String property){
        commands.add(String.format("-D%s=%s",commandName,property));
    }

    private void addOptionalProperty(String commandName, String property){
        Optional.ofNullable(property).filter(command -> ! command.isEmpty()).ifPresent(command -> addProperty(commandName,command));
    }

    /**
     * Concatenate all the dependencies of the softwareLibsPath
     * @return
     */
    private String getDependencies(String libPath){
        String dep = "";
        File lib_dir = new File(libPath);
        File[] listOfFilesInSourceFolder = lib_dir.listFiles();
        for(int i = 0; i < listOfFilesInSourceFolder.length; i++){
            String lib_file_name = listOfFilesInSourceFolder[i].getName();
            // Do not consider non jar files
            if( listOfFilesInSourceFolder[i].getName().charAt(0) !='.' && FilenameUtils.getExtension(lib_file_name).equals("jar")) {
                Path depPath = Paths.get(lib_dir.getAbsolutePath(), lib_file_name);
                String dependency = depPath.toString();
                dep += (dependency+":");
            }

        }
        return dep.substring(0, dep.length() - 1);
    }


    private void checkIfDirectoryExists(String directoryPath){
        if(! new File(directoryPath).exists()){
            throw new RuntimeException(String.format("Bad path %s",directoryPath));
        }
    }

    private void checkRequiredParameterContent(String parameterName, String parameter){
        System.out.println(String.format(" - %s: %s",parameterName, Optional.ofNullable(parameter)
                .orElseThrow(() -> new RuntimeException(String.format("Impossible to run Botsing, %s is not set",parameterName)))));
    }

    private void displayOptionalParameterIfNotNull(String parameterName, String parameter){
        Optional.ofNullable(parameter).ifPresent(content -> System.out.println(String.format("%s: %s",parameterName,content)));
    }
}
