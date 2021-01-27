package com.cambyze.migration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final String DIRECTORY = "c:/TEMP/";


  public static void main(String[] args) {
    LOGGER.info("Files .o analysis in the directory " + DIRECTORY);


    // Directory
    File directory = new File(DIRECTORY);

    try {
      //
      PrintWriter logfile = new PrintWriter(DIRECTORY + "/result.csv");
      logfile.println("Filename;Parameters");
      // filter files with extension *.o
      File[] files = directory.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          String fileName = pathname.getName();
          return fileName.endsWith(".o");
        }
      });


      // Loop on the *.o of the directory
      for (File file : files) {
        boolean findExpression = false;
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
          String line = s.nextLine();
          if (line.contains("liste de param :")) {
            findExpression = true;
            logfile.println(file.getName() + ";" + line);
          }
        }
        s.close();

        if (findExpression) {
          LOGGER.info(
              "+++++++++++++++++++++++ " + file.getName() + " contains requested expressions");
        } else {
          LOGGER.info("----------------------- " + file.getName()
              + " does not contain the requested expressions");
        }
      }

      logfile.close();

    } catch (

    IOException e) {
      LOGGER.error(e.toString());
    }
  }
}
