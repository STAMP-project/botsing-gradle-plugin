package eu.stamp_project.botsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

@Log4j
public class BotsingRunner {

	public static boolean executeBotsing(File basedir, long timeout, File botsingReproductionJar, List<String> properties)
			throws InterruptedException, IOException {

		final String JAVA_CMD = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar
				+ "java";

		ArrayList<String> jarCommand = new ArrayList<>();
		jarCommand.add(JAVA_CMD);
		jarCommand.add("-jar");

		jarCommand.add(botsingReproductionJar.getAbsolutePath());

		jarCommand.addAll(properties);

		return BotsingRunner.executeProcess(basedir,timeout,jarCommand.toArray(new String[0]));
	}

	public static boolean executeProcess(File workDir,long timeout, String... command) throws InterruptedException, IOException {
		Process process = null;

		try {
			ProcessBuilder builder = new ProcessBuilder(command);

			builder.directory(workDir.getAbsoluteFile());
			builder.redirectErrorStream(true);

			process = builder.start();
			handleProcessOutput(process);

			boolean exitResult = process.waitFor(timeout, TimeUnit.SECONDS);

			if (! exitResult) {
				log.error("Error executing botsing-reproduction");
				return false;
			} else {
				log.debug("botsing-reproduction terminated");
			}

		} catch (InterruptedException e) {
			if (process != null) {

				try {
					// be sure streamers are closed, otherwise process might hang on Windows
					process.getOutputStream().close();
					process.getInputStream().close();
					process.getErrorStream().close();

				} catch (Exception t) {
					log.error("Failed to close process stream: " + t.toString());
				}

				process.destroy();

			}
			return false;

		}

		return true;
	}

	private static void handleProcessOutput(final Process process) {

		Thread reader = new Thread() {
			@Override
			public void run() {
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

					while (!this.isInterrupted()) {
						String line = in.readLine();
						if (line != null && !line.isEmpty()) {
							log.info(line);
						}
					}
				} catch (Exception e) {
					log.debug("Exception while reading spawn process output: " + e.toString());
				}
			}
		};

		reader.start();
		log.debug("Started thread to read spawn process output");
	}
}