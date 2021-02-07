package com.cambyze.migration.forms.analysis;

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
 * Oracle forms converted into txt format analysis
 * 
 * @author Thierry NESTELHUT
 *
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final String DIRECTORY = "C:\\DEV\\Projects\\Grilles SPEC PRODLR EKIP V5";

  /**
   * Retrieve the parameter just after the n occurrence of the label and before the next blank " "
   * 
   * @param text to analyse
   * @param label to find
   * @param occurrence nb of occurrence of the label
   * @return the parameter else empty string
   */
  private static String lastString(String text) {
    String result = "";
    int pos = text.lastIndexOf(' ');

    if (pos > 0 && pos < text.length() - 1) {
      result = text.substring(pos + 1);
    }
    return result;

  }

  /**
   * Analysis of converted forms files to find which PL/SQL code is executed
   * 
   * @param args
   */
  public static void main(String[] args) {
    LOGGER.info("Files .txt analysis in the directory " + DIRECTORY);


    // Directory
    File directory = new File(DIRECTORY);

    try {
      //
      String msg;
      int readfiles = 0;
      int readforms = 0;

      PrintWriter logfile = new PrintWriter(DIRECTORY + "/forms_analysis.log");
      // filter files with extension *.o
      File[] files = directory.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          String fileName = pathname.getName();
          return fileName.endsWith(".txt");
        }
      });


      // Loop on the *.txt of the directory
      for (File file : files) {
        Form form = null;
        Trigger trigger = null;
        Block block = null;
        LOGGER.info("Analysis of the txt file " + file.getName());
        boolean isFormNameFound = false;
        String formsName = "";
        String step = "";
        String objectName = "";
        readfiles++;
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
          String line = s.nextLine();
          // Search the name of the form
          if (!isFormNameFound) {
            if (line.startsWith(" * Nom                      ")) {
              formsName = lastString(line);
              if (!formsName.isEmpty()) {
                isFormNameFound = true;
                readforms++;
                step = "SEARCH_OBJECTS";
                logfile.println("Analysis of " + formsName);
                form = new Form(formsName);
              }
            }
          } else {
            switch (step) {
              case "SEARCH_OBJECTS":
                // Search object name : form triggers, blocks or procedures
                if (line.startsWith("   * Nom                ")) {
                  objectName = lastString(line);
                  step = "SEARCH_OBJECT_TYPE";
                }
                break;
              case "SEARCH_OBJECT_TYPE":
                // Search object type : forms triggers, blocks or procedures
                if (line.contains(" Texte du déclencheur  ")) {
                  trigger = new Trigger(objectName);
                  form.getTriggers().add(trigger);
                  step = "TRIGGER_TEXT";
                } else if (line.contains(" Nom de source de données de requête      ")) {
                  block = new Block(objectName);
                  block.setTable(lastString(line));
                  form.getBlocks().add(block);
                  step = "SEARCH_OBJECT_TYPE";
                } else if (line.startsWith("   * Nom                ")) {
                  objectName = lastString(line);
                  step = "SEARCH_OBJECT_TYPE";
                }
                break;
              case "TRIGGER_TEXT":
                // within the code of the trigger
                if (line.contains(" Activer en mode Saisie Requête  ")) {
                  step = "SEARCH_OBJECT_TYPE";
                } else {
                  trigger.getCode().add(line);
                }
                break;
            }
          }
        }
        s.close();
        if (!formsName.isEmpty() && form != null) {
          msg = "End of the analysis of the form " + formsName;
          LOGGER.info(msg);
          logfile.println(msg);
          logfile.println(form.toString());
        }
      }
      msg = "End of the analysis of the " + readfiles + " txt files with " + readforms + " forms";
      LOGGER.info(msg);
      logfile.println(msg);
      logfile.close();
    } catch (

    IOException e) {
      LOGGER.error(e.toString());
    }
  }

}
