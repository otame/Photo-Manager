package controller.database;

import controller.controlfiles.ControlFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class DatabaseDownloader which is used to download data from database.
 *
 * @author XIAWEI ZHANG
 */
public class DataBaseDownLoader {

  /** The connection used to connect to database. */
  private Connection connection;

  /** Constructor of DataDownLoader. */
  public DataBaseDownLoader() {
    connection = DataBaseConnection.dbConnector();
  }

  /**
   * Get specific attribute for a ControlFile from database such as the name of the ControlFile and
   * the tags of the ControlFile.
   *
   * @param imageName String Name of the image.
   * @param property String The column.
   * @param fileLocation String The location of the ControlFile.
   * @return String the attribute we want.
   */
  public String getProperty(
      String imageName, String property, String fileLocation, String tableName) {
    String sql =
        "SELECT "
            + property
            + " FROM "
            + tableName
            + " WHERE name = '"
            + imageName
            + "'and location = '"
            + fileLocation
            + "';";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet rs = preparedStatement.executeQuery();
      return rs.getString(property);
    } catch (SQLException e) {
      return null;
    }
  }

  /**
   * Get Control Files in which the first index is ControlFile name and the second index is
   * ControlFile address in one certain tag from database.
   *
   * @param workingPath String The path we are working at.
   * @param tag String Tag of the file.
   * @return String ControlFile.
   */
  public String getFiles(String workingPath, String tag) {
    String sql =
        "SELECT files FROM tags WHERE name = '"
            + tag
            + "'and workingDirectory = '"
            + workingPath
            + "';";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      // update
      ResultSet rs = preparedStatement.executeQuery();
      return rs.getString("files");
    } catch (SQLException e) {
      return "";
    }
  }

  /**
   * Get all tags which contains the same rootPath stored in a String ArrayList from the database.
   *
   * @param rootPath String Root path of the file.
   * @return ArrayList Tags we want.
   */
  public ArrayList<String> getAllTags(String rootPath) {

    ArrayList<String> temp = new ArrayList<>();
    String sql = "SELECT name FROM tags WHERE workingDirectory = '" + rootPath + "';";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      // update
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        temp.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return temp;
  }

  /**
   * Generate ControlFile class with the same rootPath including subDirectory by downloading all
   * controlFiles information from the database.
   *
   * @param rootPath String main directory for those controlfiles that we want to construct.
   * @return ArrayList controlFiles.
   */
  public ArrayList<ControlFile> buildFiles(String rootPath, String type) {

    ArrayList<ControlFile> temp = new ArrayList<>();
    String sql =
        "SELECT name, location, tags FROM " + type + " WHERE location LIKE '" + rootPath + "%'; ";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        String name = rs.getString("name");
        String fileLocation = rs.getString("location");
        String tempTag = rs.getString("tags");
        if (tempTag.length() != 0 && tempTag.substring(tempTag.length() - 1).equals("|")) {
          temp.add(new ControlFile(name, fileLocation, ""));
        } else {
          String[] tags = tempTag.split("\\|");
          ControlFile controlFile = new ControlFile(name, fileLocation, tags[tags.length - 1]);
          temp.add(controlFile);
        }
      }
      return temp;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return temp;
  }
}
