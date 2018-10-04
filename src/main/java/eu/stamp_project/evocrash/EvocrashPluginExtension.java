package eu.stamp_project.evocrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.crash.client.log.LogParser;
import org.crash.master.EvoSuite;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.testcase.TestChromosome;
import org.junit.Assert;

import lombok.Getter;
import lombok.Setter;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class EvocrashPluginExtension {

    @Getter @Setter
    private String test="src/GGA-tests/java/";
    @Getter @Setter
    private String log="src/resources/logs/Activeeon/8.1.0/SpaceNotFoundException.log";
    @Getter @Setter
    private String libs="src/resources/targetedSoftware/Activeeon-bins/8.1.0/dist/lib/";

    private String frameLevel="3";
    private String exceptionType="org.objectweb.proactive.extensions.dataspaces.exceptions.SpaceNotFoundException";

    private String criterion="CRASH";
    private String sandbox="TRUE";
    private String testRandom="0";
    private String functionalMockingP="0.8";
    private String functionalMockingPercent="0.5";
    private String minimize="TRUE";
    private String headlessChickenTest="FALSE";
    private String population="80";
    private String searchBudget="600";
    private String conditionStopping="";
    private String timeoutGlobal="3600";
    private String virtualFs="TRUE";
    private String classLoaderSeparate="FALSE";
    private String callsReplace="FALSE";
    private String recursionMax="50";
    private String jarToolsLocation="";
    private String fieldsStaticReset="FALSE";
    private String netVirtual="FALSE";

    private List<String> commands;

    public EvocrashPluginExtension(){
        commands = new ArrayList<>();
    }

    public void listProperties(){

        System.out.println("Project libraries will be taken in the directory: "+ libs);
        System.out.println("The log file: "+ log +" will be taken");
        System.out.println("The test will be generated in the directory: " + test );
    }

    public void create() {

        checkIfDirectoryExists(test);
        checkIfDirectoryExists(libs);
        checkIfDirectoryExists(log);


        String logPaths = Paths.get(log).toString();

        commands.add("-generateTests");
        addProperty("criterion",criterion);
        addProperty("sandbox",sandbox);
        commands.add(String.format("-Dtest_dir=%s",Paths.get(test).toString()));
        addProperty("random_tests",testRandom);
        addProperty("p_functional_mocking",functionalMockingP);
        addProperty("functional_mocking_percent",functionalMockingPercent);
        addProperty("minimize",minimize);
        addProperty("headless_chicken_test",headlessChickenTest);
        addProperty("population",population);
        addProperty("search_budget",searchBudget);
        addOptionalProperty("stopping_condition",conditionStopping);
        addProperty("global_timeout",timeoutGlobal);
        addProperty("target_frame",frameLevel);
        addProperty("virtual_fs",virtualFs);
        addProperty("use_separate_classloader",classLoaderSeparate);
        addProperty("replace_calls",callsReplace);
        addProperty("max_recursion",recursionMax);
        addOptionalProperty("tools_jar_location",jarToolsLocation);
        addProperty("reset_static_fields",fieldsStaticReset);
        addProperty("virtual_net",netVirtual);
        addProperty("target_exception_crash",exceptionType);
        commands.add(String.format("-DEXP=%s",logPaths));

        System.out.println(String.format("Commands used without dependencies and class: %s",commands.toString()));

        commands.add(String.format("-projectCP=%s",getDependencies(libs)));
        commands.add(String.format("-class=%s",LogParser.getTargetClass(logPaths, Integer.parseInt(frameLevel))));


        EvoSuite evosuite = new EvoSuite();

        try {
            Object result = evosuite.parseCommandLine(commands.stream().toArray(String[]::new));
            List<List<TestGenerationResult>> results = (List<List<TestGenerationResult>>)result;
            GeneticAlgorithm<?> ga = getGAFromResult(results);
            if (ga == null){
                // ga is null when during bootstrapping the ideal eu.stamp_project.evocrash is found!
                Assert.assertTrue(true);
            }
            else{
                TestChromosome best = (TestChromosome) ga.getBestIndividual();
                Assert.assertEquals(0.0, best.getFitness(), 0);
            }
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
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

    private GeneticAlgorithm<?> getGAFromResult(Object result) {
        assert(result instanceof List);
        List<List<TestGenerationResult>> results = (List<List<TestGenerationResult>>)result;
        if(results.size()>0) {
            return results.get(0).get(0).getGeneticAlgorithm();
        }else {
            return null;
        }

    }

}
