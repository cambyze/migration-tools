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
   * Write a SQL file to create the package
   * 
   * @param packageName name of the package
   * @param form structure of the form with its PL/SQL code
   * @throws IOException
   */
  private static void writeSqlFile(String packageName, Form form) throws IOException {
    PrintWriter sqlfile = new PrintWriter(DIRECTORY + "/sql/" + packageName + ".sql");

    // Package creation with the procedures declaration
    sqlfile.println("CREATE OR REPLACE PACKAGE " + packageName + " AS ");
    sqlfile.println();
    parseForm(form, sqlfile, false);
    sqlfile.println();
    sqlfile.println("END " + packageName + ";");
    sqlfile.println("/");
    sqlfile.println();

    // Package body creation with the procedures code
    sqlfile.println("CREATE OR REPLACE PACKAGE BODY " + packageName + " AS ");
    sqlfile.println();
    parseForm(form, sqlfile, true);
    sqlfile.println();
    sqlfile.println("END " + packageName + ";");
    sqlfile.println("/");
    sqlfile.println();
    sqlfile.close();
  }

  /**
   * Write the content of the package or package body by parsing the form
   * 
   * @param form form to parse
   * @param sqlfile SQL file to write
   * @param isPackageBody indicates if it is the package body to write
   */
  private static void parseForm(Form form, PrintWriter sqlfile, boolean isPackageBody) {

    // Write form triggers
    sqlfile.println("-- Form triggers");
    for (Trigger trigger : form.getTriggers()) {
      String name = "TFM_" + trigger.getName().replace('-', '_');
      if (name.length() > 30) {
        name = name.substring(0, 30);
      }
      if (!isPackageBody) {
        sqlfile.println("PROCEDURE " + name + ";");
      } else {
        sqlfile.println("PROCEDURE " + name + " IS");
        sqlfile.println("  -- " + trigger.getName());
        sqlfile.println("  BEGIN");
        for (String line : trigger.getCode()) {
          sqlfile.println("    " + line);
        }
        sqlfile.println("  END;");
        sqlfile.println();
      }
    }
    sqlfile.println();

    // Write blocks
    int blockNumber = 0;
    sqlfile.println("-- Blocks");
    for (Block block : form.getBlocks()) {
      blockNumber++;
      String name = block.getName().replace('-', '_');
      if (name.length() > 30) {
        name = name.substring(0, 30);
      }
      if (!isPackageBody) {
        sqlfile.println("PROCEDURE BK" + blockNumber + "_" + name + ";");

        // block triggers
        for (Trigger trigger : block.getTriggers()) {
          String triggerName = "TB" + blockNumber + "_" + trigger.getName().replace('-', '_');
          if (triggerName.length() > 30) {
            triggerName = triggerName.substring(0, 30);
          }
          sqlfile.println("PROCEDURE " + triggerName + ";");
        }

        // Block elements
        for (Element element : block.getElements()) {
          String elementName = element.getName().replace('-', '_');
          int i = 0;
          // Elements triggers
          for (Trigger trigger : element.getTriggers()) {
            i++;
            String triggerName = "TE" + blockNumber + "_" + elementName;
            if (triggerName.length() > 28) {
              triggerName = triggerName.substring(0, 28);
            }
            triggerName = triggerName + '#' + i;
            sqlfile.println("PROCEDURE " + triggerName + ";");
          }
        }
      } else {
        sqlfile.println("PROCEDURE BK" + blockNumber + "_" + name + " IS");
        sqlfile.println("  BEGIN");
        sqlfile.println("  -- block name = " + block.getName());
        sqlfile.println("  -- table name = " + block.getTable());
        for (Element element : block.getElements()) {
          sqlfile.println("  -- element : " + element.getName());
        }
        sqlfile.println("  END;");
        sqlfile.println();

        // Block triggers
        for (Trigger trigger : block.getTriggers()) {
          String triggerName = "TB" + blockNumber + "_" + trigger.getName().replace('-', '_');
          if (triggerName.length() > 30) {
            triggerName = triggerName.substring(0, 30);
          }
          sqlfile.println("PROCEDURE " + triggerName + " IS");
          sqlfile.println("  -- " + block.getName() + '.' + trigger.getName());
          sqlfile.println("  BEGIN");
          for (String line : trigger.getCode()) {
            sqlfile.println("    " + line);
          }
          sqlfile.println("  END;");
          sqlfile.println();
        }

        // Block elements
        for (Element element : block.getElements()) {
          String elementName = element.getName().replace('-', '_');
          int i = 0;
          // Elements triggers
          for (Trigger trigger : element.getTriggers()) {
            i++;
            String triggerName = "TE" + blockNumber + "_" + elementName;
            if (triggerName.length() > 28) {
              triggerName = triggerName.substring(0, 28);
            }
            triggerName = triggerName + '#' + i;
            sqlfile.println("PROCEDURE " + triggerName + " IS");
            sqlfile.println(
                "  -- " + block.getName() + "." + element.getName() + '.' + trigger.getName());
            sqlfile.println("  BEGIN");
            for (String line : trigger.getCode()) {
              sqlfile.println("    " + line);
            }
            sqlfile.println("  END;");
            sqlfile.println();
          }
        }
      }
      sqlfile.println();
    }

    // Write form procedures
    sqlfile.println("-- Procedures");
    for (Procedure procedure : form.getProcedures()) {
      String name = procedure.getName().replace('-', '_');
      if (!isPackageBody) {
        sqlfile.println("PROCEDURE " + name + ";");
      } else {
        for (String line : procedure.getCode()) {
          sqlfile.println("    " + line);
        }
        sqlfile.println();
      }
    }

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

      PrintWriter cmdfile = new PrintWriter(DIRECTORY + "/sql/run_sql.sql");
      cmdfile.println("/************************************************/");
      cmdfile.println("/* Execute sql converted forms files            */");
      cmdfile.println("/************************************************/");
      cmdfile.println();
      cmdfile.println("set serveroutput on size unlimited");
      cmdfile.println("spool run_sql.log");
      cmdfile.println();

      PrintWriter dropfile = new PrintWriter(DIRECTORY + "/sql/drop_sql.sql");
      dropfile.println("/************************************************/");
      dropfile.println("/* Drop sql converted forms packages            */");
      dropfile.println("/************************************************/");
      dropfile.println();
      dropfile.println("set serveroutput on size unlimited");
      dropfile.println("spool drop_sql.log");
      dropfile.println();

      // filter files with extension *.txt
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
        Element element = null;
        Procedure procedure = null;
        LOGGER.info("Analysis of the txt file " + file.getName());
        boolean isFormNameFound = false;
        boolean isFormData = false;
        boolean isBlockData = false;
        boolean isElementData = false;
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
                isFormData = true;
                isBlockData = false;
                isElementData = false;
                readforms++;
                step = "SEARCH_OBJECTS";
                logfile.println("Analysis of " + formsName);
                form = new Form(formsName);
              }
            }
          } else {
            switch (step) {
              case "SEARCH_OBJECTS":
                if (line.contains(" Nom                ")) {
                  // Search object name
                  objectName = lastString(line);
                  step = "SEARCH_OBJECT_TYPE";
                }
                break;
              case "SEARCH_OBJECT_TYPE":
                // Search object type : trigger, block, element or procedure
                if (line.contains(" Texte du déclencheur  ")) {
                  trigger = new Trigger(objectName);
                  if (isFormData) {
                    form.getTriggers().add(trigger);
                  } else if (isBlockData) {
                    block.getTriggers().add(trigger);
                  } else if (isElementData) {
                    element.getTriggers().add(trigger);
                  }
                  step = "TRIGGER_TEXT";
                } else if (line.contains(" Nom de source de données de requête      ")) {
                  isFormData = false;
                  isBlockData = true;
                  isElementData = false;
                  block = new Block(objectName);
                  block.setTable(lastString(line));
                  form.getBlocks().add(block);
                  step = "SEARCH_OBJECTS";
                } else if (line.contains(" Type d'élément                           ")) {
                  isFormData = false;
                  isBlockData = false;
                  isElementData = true;
                  element = new Element(objectName);
                  block.getElements().add(element);
                  step = "SEARCH_OBJECTS";
                } else if (line.contains(" Texte d'unité de programme                    ")) {
                  isFormData = true;
                  isBlockData = false;
                  isElementData = false;
                  procedure = new Procedure(objectName);
                  form.getProcedures().add(procedure);
                  step = "PROCEDURE_TEXT";
                } else if (line.contains(" Nom                ")) {
                  // The previous object does not need to be analysed
                  objectName = lastString(line);
                  step = "SEARCH_OBJECT_TYPE";
                }
                break;
              case "TRIGGER_TEXT":
                // within the code of the trigger
                if (line.contains(" Activer en mode Saisie Requête  ")) {
                  step = "SEARCH_OBJECTS";
                } else {
                  trigger.getCode().add(line);
                }
                break;
              case "PROCEDURE_TEXT":
                // within the code of the procedure
                if (line.contains(" Type d'unité de programme                     ")) {
                  step = "SEARCH_OBJECTS";
                } else {
                  procedure.getCode().add(line);
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

          // Creation of the "name of the form".sql file which contains all the PL/SQL code of the
          // form
          String packageName = formsName + "_0TNE";
          writeSqlFile(packageName, form);
          cmdfile.println("@" + packageName + ".sql");
          dropfile.println("drop package body " + packageName);
          dropfile.println("drop package " + packageName);
          msg = "End of creation of the sql file for the package " + packageName;
          LOGGER.info(msg);
          logfile.println(msg);
          logfile.println();
        }
      }
      msg = "End of the analysis of the " + readfiles + " txt files with " + readforms + " forms";
      LOGGER.info(msg);
      logfile.println(msg);
      logfile.close();

      cmdfile.println();
      cmdfile.println("spool off");
      cmdfile.println();
      cmdfile.close();

      dropfile.println();
      dropfile.println("spool off");
      dropfile.println();
      dropfile.close();


    } catch (

    IOException e) {
      LOGGER.error(e.toString());
    }
  }

}
