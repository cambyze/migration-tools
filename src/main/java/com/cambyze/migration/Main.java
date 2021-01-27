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
  private static final String DIRECTORY = "C:/DEV/Projects/Logs";


  public static void main(String[] args) {
    LOGGER.info("Files .o analysis in the directory " + DIRECTORY);


    // Directory
    File directory = new File(DIRECTORY);

    try {
      //
      String msg;
      int readfiles = 0;
      int okfiles = 0;
      int nokfiles = 0;

      PrintWriter csvfile = new PrintWriter(DIRECTORY + "/analysis.csv");
      PrintWriter logfile = new PrintWriter(DIRECTORY + "/analysis.log");
      PrintWriter excludedfile = new PrintWriter(DIRECTORY + "/excludedfile.log");
      csvfile.println("Filename;Date;Time;Program;Company;Parameters;Line");
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
        String fileDate = "";
        String fileTime = "";
        String programName = "";
        String parameters = "";
        String parametersLine = "";
        String company = "";
        readfiles++;
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
          String line = s.nextLine();
          if (line.startsWith("Valeur de la date   : ")) {
            fileDate = line.substring(22);
          } else if (line.startsWith("Date d'exploitation ")) {
            int pos = line.lastIndexOf(" : ");
            if (pos > 0) {
              fileDate = line.substring(pos + 3);
            }
          } else if (line.contains("liste de param :")) {
            findExpression = true;
            fileTime = line.substring(0, 7);
            String upperLine = line.toUpperCase();
            int pos = upperLine.indexOf("XPRODLR5 ");
            if (pos > 0) {
              parameters = line.substring(pos + 9);
              int pos2 = parameters.indexOf(" ");
              if (pos2 > 0) {
                programName = parameters.substring(0, pos2);
              }
            }
            if (line.indexOf(" ETA ") > 0) {
              company = "ETA";
            } else if (line.indexOf(" UNI ") > 0) {
              company = "UNI";
            } else if (line.indexOf(" FIP ") > 0) {
              company = "FIP";
            } else if (line.indexOf(" UCB-ESP ") > 0) {
              company = "UCB-ESP";
            }
            parametersLine = line;
          }
        }
        s.close();

        if (findExpression) {
          msg = "+++++++++++++++++++++++ " + file.getName() + " contains requested expressions";
          csvfile.println(file.getName() + ";" + fileDate + ";" + fileTime + ";" + programName + ";"
              + company + ";" + parameters + ";" + parametersLine);
          okfiles++;

        } else {
          msg = "----------------------- " + file.getName()
              + " does not contain the requested expressions";
          nokfiles++;

          // Append the file in excludedfile for further analysis
          excludedfile.println("*********************************************");
          excludedfile.println(file.getName());
          excludedfile.println("*********************************************");
          Scanner s2 = new Scanner(file);
          while (s2.hasNextLine()) {
            String line = s2.nextLine();
            excludedfile.println(line);
          }
          s2.close();
        }
        LOGGER.info(msg);
        logfile.println(msg);

      }
      msg = readfiles + " files read with " + okfiles + " OK and " + nokfiles + " NOK";
      LOGGER.info(msg);
      logfile.println(msg);
      csvfile.close();
      logfile.close();
      excludedfile.close();

    } catch (

    IOException e) {
      LOGGER.error(e.toString());
    }
  }
}
