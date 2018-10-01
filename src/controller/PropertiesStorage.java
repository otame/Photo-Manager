package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * PropertiesStorage Class that stores gui properties
 *
 * @author Yu Feng Gan
 */
public class PropertiesStorage {

  /** The working directory. */
  private static String directory;

  /** The selected Tab by the clicker. */
  private static String selectedTab;

  /** The current Tab viewed. */
  private static String viewTab;

  /** saving the most recent working directory. */
  public static void setDirectory(String dir) {
    directory = dir;
  }

  /** returns a boolean variable containing information telling whether it is the first */
  public static void lastSelectedTab(String tabName) {
    selectedTab = tabName;
  }

  /** update whether the viewTab has been disabled */
  public static void setViewTab(boolean state) {
    if (state) {
      viewTab = "true";
    } else {
      viewTab = "false";
    }
  }

  /**
   * Write all the stored properties into the config file for later use adapt the code from:
   * http://www.opencodez.com/java/write-config-file-in-java.htm
   */
  public static void runConfig() {
    Properties settings = new Properties();
    try {
      settings.setProperty("PhotoDirectory", directory);
      settings.setProperty("viewTabState", viewTab);
      settings.setProperty("lastSelectedTab", selectedTab);
      FileWriter writer = new FileWriter("config.properties");
      settings.store(writer, directory);
      writer.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
