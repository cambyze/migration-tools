package com.cambyze.migration.log.analysis;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration tools
 * <p>
 * Log files analysis to determine launched batch
 * 
 * @author Thierry NESTELHUT
 *
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final String DIRECTORY = "C:/DEV/Projects/Logs";

  /**
   * Retrieve the parameter just after the n occurrence of the label and before the next blank " "
   * 
   * @param text to analyse
   * @param label to find
   * @param occurrence nb of occurrence of the label
   * @return the parameter else empty string
   */
  private static String getParameter(String text, String label, int occurrence) {
    String result = "";
    String temp = text;
    int pos = 0;

    for (int i = 1; i <= occurrence; i++) {
      pos = temp.indexOf(label);
      if (pos > 0) {
        temp = temp.substring(pos + label.length());
      }
    }

    int pos2 = temp.indexOf(" ");
    if (pos2 > 0) {
      result = temp.substring(0, pos2);
    } else {
      result = temp;
    }

    return result;

  }

  /**
   * Analysis of log files to find which programs are launched
   * 
   * @param args
   */
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
      csvfile.println("Filename;Date;Time;Program;Company;Info;Function;Parameters;Line");
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
        String info = "";
        String functionId = "";
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

            String upperLine = line.toUpperCase();
            int pos = upperLine.indexOf("XPRODLR5 ");
            if (pos > 0) {
              parameters = line.substring(pos + 9);
              functionId = getParameter(parameters, "EUR ", 1);
              int pos2 = parameters.indexOf(" ");
              if (pos2 > 0) {
                programName = parameters.substring(0, pos2);
                switch (programName) {
                  case "CPT171":
                    // Retrieve the parameter date
                    info = "Parameter Date = " + '"' + getParameter(parameters, "@@@", 1) + '"';
                    break;
                  case "CPTFLX":
                  case "CPT369":
                    // Retrieve function id
                    info = "Function id = " + '"' + functionId + '"';
                    break;
                  case "FLX013":
                    // Retrieve function id & payment mode
                    info = "Function id = " + '"' + functionId + '"' + " & payment mode = "
                        + getParameter(parameters, "EUR ", 2);
                    break;
                  case "FLX018":
                    // Retrieve function id & campaign type
                    info = "Function id = " + '"' + functionId + '"' + " & campaign type = "
                        + getParameter(parameters, company + " ", 2);
                    break;
                  case "DOC001":
                    // Retrieve document name
                    info =
                        "Document name = " + '"' + getParameter(parameters, "BATCDOCADM ", 1) + '"';
                    break;
                }

              }
            }
          }
        }
        s.close();

        if (findExpression) {
          msg = "+++++++++++++++++++++++ " + file.getName() + " contains requested expressions";
          csvfile.println(file.getName() + ";" + fileDate + ";" + fileTime + ";" + programName + ";"
              + company + ";" + info + ";" + functionId + ";" + parameters + ";" + parametersLine);
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
