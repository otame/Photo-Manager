package controller.database;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Construct a connection to a database.
 *
 * @author XIAWEI ZHANG
 */
public class DataBaseConnection {

  /**
   * Connection for connecting to database.
   *
   * @return Connection
   */
  public static Connection dbConnector() {
    try {
      File directory =
          new File(System.getProperty("user.dir") + File.separator + "sqlite-jdbc-3.19.3.jar");
      URL url = directory.toURI().toURL();
      URLClassLoader jarLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      method.setAccessible(true);
      method.invoke(jarLoader, url);
      Class.forName("org.sqlite.JDBC");
      return DriverManager.getConnection("jdbc:sqlite:FileStorage.sqlite");

    } catch (Exception e) {
      System.out.println("There is no JDBC");
      return null;
    }
  }
}
