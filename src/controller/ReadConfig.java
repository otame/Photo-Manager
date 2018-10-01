package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A class that helps to read all the properties saved before the last close
 *
 * @author Yu Feng Gan
 * @author Jin Cheng He
 */
public class ReadConfig {

  /** The working directory. */
  private static String directory;

  /** The selected Tab by the clicker. */
  private static String selectedTab;

  /** The current Tab viewed. */
  private static String viewTab;

  /** The boolean value to check if user use this software in the first time. */
  private static boolean firstTime = true;

  /** Load in all the saved properties to the class from config.properties */
  public static void loadProperties() {
    Properties properties = new Properties();
    InputStream configSettings = null;
    Path conPath = Paths.get(System.getProperty("user.dir") + File.separator + "config.properties");
    firstTime = !Files.exists(conPath);
    if (!firstTime) {
      try {
        configSettings = new FileInputStream("config.properties");
        properties.load(configSettings);
        directory = properties.getProperty("PhotoDirectory");
        viewTab = properties.getProperty("viewTabState");
        selectedTab = properties.getProperty("lastSelectedTab");
      } catch (IOException exception) {
        exception.printStackTrace();
      } finally {
        if (configSettings != null) {
          try {
            configSettings.close();
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * returns a boolean variable containing information telling whether it is the first time running
   * the program
   *
   * @return boolean
   */
  public static boolean getFirstTime() {
    return firstTime;
  }

  /**
   * returns the most recent working directory user had chosen since the last program close
   *
   * @return String the directory
   */
  public static String getDirectory() {
    return directory;
  }

  /**
   * Return true iff viewTab is true.
   *
   * @return boolean
   */
  public static boolean getViewTab() {
    return viewTab.equals("true");
  }

  /**
   * Return selected Tab passing it to Gui.
   *
   * @return String selected Tag
   */
  public static String getSelectedTab() {
    if (selectedTab.equals("LoadPhoto")) {
      return "LoadPhoto";
    } else {
      return "ViewTab";
    }
  }
}
