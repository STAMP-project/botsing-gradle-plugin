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

    @Getter @Setter
    private String output ="result/";
    @Getter @Setter
    private String logPath ="src/main/resources/Fraction.logPath";
    @Getter @Setter
    private String libsPath ="src/main/resources/Fraction_libraries.jar";
    @Getter @Setter
    private String frameLevel="1";


    private List<String> commands;

    public BotsingGradlePluginExtension(){
        commands = new ArrayList<>();
    }

    public void listProperties(){

        System.out.println("Project libraries will be taken in the directory: "+ libsPath);
        System.out.println("The logPath file: "+ logPath +" will be taken");
        System.out.println("The output will be generated in the directory: " + output);
    }

    public void create() {

        checkIfDirectoryExists(output);
        checkIfDirectoryExists(libsPath);
        checkIfDirectoryExists(logPath);

        String logPath = this.logPath;

        //required parameters
        addRequiredParameter("target_frame",frameLevel);
        addRequiredParameter("crash_log",logPath);

        System.out.println(String.format("Commands used without dependencies and class: %s",commands.toString()));
        addRequiredParameter("projectCP", libsPath);

        //optional parameters
        commands.add(String.format("-Dtest_dir=%s",Paths.get(output).toString()));
        commands.add(String.format("-Drandom_seed=%s",1));

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

}
