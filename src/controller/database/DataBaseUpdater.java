package controller.database;

import controller.ExceptionPrompter;
import controller.controlfiles.ControlFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class of DataBaseUpdater to modify database.
 *
 * @author XIAWEI ZHANG
 */
public class DataBaseUpdater {

  /** The connection used to connect to database. */
  private Connection connection;

  /** DataBaseDownLoader used to get ControlFile data from the database. */
  private DataBaseDownLoader dataBaseDownLoader;

  /** The constructor of DatabaseUpdater. */
  public DataBaseUpdater() {
    connection = DataBaseConnection.dbConnector();
    dataBaseDownLoader = new DataBaseDownLoader();
  }

  /**
   * Update the ControlFile attributes in database.
   *
   * @param controlFile String The controlFile we choose.
   * @param property String the attribute we choose.
   * @param content String the new content that we want to update.
   */
  public void update(ControlFile controlFile, String table, String property, String content) {
    String sql =
        "UPDATE "
            + table
            + " SET "
            + property
            + " = ? WHERE name = '"
            + controlFile.getFileName()
            + "' and location = '"
            + controlFile.getLocation()
            + "';";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, content);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Update the pictures in tags table including changing location, adding tag and deleting tags.
   *
   * @param controlFile String The controlFile we choose.
   * @param workingPath String The path we will work at.
   * @param tag String the tag we need to update.
   * @param command String The operation code.
   */
  public void updatePictures(
      ControlFile controlFile, String workingPath, String tag, String command) {
    String sql =
        "UPDATE tags SET files = ? WHERE workingDirectory = '"
            + workingPath
            + "' and name = '"
            + tag
            + "';";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      String temp = "";
      String oldPictures = dataBaseDownLoader.getFiles(workingPath, tag);
      // add tag to tags table.
      if (command.equals("add")) {
        temp = addHelper(oldPictures, controlFile);
      }
      // delete Tag in tags table
      else if (command.equals("delete")) {
        temp = deleteHelper(oldPictures, controlFile);
      } else if (command.substring(0, 3).equals("cl:")) {
        temp = clHelper(command, oldPictures, controlFile);
      }
      // set the corresponding param
      preparedStatement.setString(1, temp);
      // update
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Helper method to help updatePictures in order to add controlFile info in tags in database.
   *
   * @param oldPictures String the old controlFile fileName and old controlFile location
   * @param controlFile ControlFile that we want to add into tags table in database
   * @return String template we wanted
   */
  private String addHelper(String oldPictures, ControlFile controlFile) {
    if (oldPictures.equals("")) {
      // update pictures when there is no a picture
      return controlFile.getFileName() + "," + controlFile.getLocation() + "|";
    } else {
      // update pictures when there is already a picture
      return oldPictures + controlFile.getFileName() + "," + controlFile.getLocation() + "|";
    }
  }

  /**
   * Helper method to help updatePictures in order to delete controlFile info in tags in database.
   *
   * @param oldPictures String the old controlFile fileName and old controlFile location
   * @param controlFile ControlFile that we want to add into tags table in database
   * @return String template we wanted
   */
  private String deleteHelper(String oldPictures, ControlFile controlFile) {
    StringBuilder temp = new StringBuilder();
    String needDeletePicture = controlFile.getFileName() + "," + controlFile.getLocation();
    String[] splitPics = oldPictures.split("\\|");
    for (String picture : splitPics) {
      if (!picture.equals(needDeletePicture)) {
        temp.append(picture).append("|");
      }
    }
    return temp.toString();
  }

  /**
   * Helper method to help updatePictures in order to change controlFile location in tags in
   * database.
   *
   * @param oldPictures String the old controlFile fileName and old controlFile location
   * @param controlFile ControlFile that we want to add into tags table in database
   * @return String template we wanted
   */
  private String clHelper(String command, String oldPictures, ControlFile controlFile) {
    String newLocation = command.substring(3, command.length() - 1);
    String oldLocation = controlFile.getLocation();
    return oldPictures.replace(oldLocation, newLocation);
  }

  /**
   * Record the ControlFileData into the controlFile table in database.
   *
   * @param controlFile ControlFile that we want to add into controlFile table in database
   */
  public void addFileData(ControlFile controlFile, String fileType) {
    String sql = "INSERT INTO " + fileType + "(name,location,tags,date) VALUES(?,?,?,?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, controlFile.getFileName());
      preparedStatement.setString(2, controlFile.getLocation());
      if (controlFile.getTags().equals("")) {
        preparedStatement.setString(3, controlFile.getTags());
      } else {
        preparedStatement.setString(3, controlFile.getTags());
      }
      preparedStatement.setString(4, controlFile.getDate());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      ExceptionPrompter.showInvalidLoad(controlFile);
    }
  }

  /**
   * Record the new Tag data info inserting into the tag table in database.
   *
   * @param tag String The tag we want to add into tag table in database
   * @param workingPath String that we want to store in which workingPath
   */
  public void addTagData(String tag, String workingPath) {
    if (!tagExist(tag, workingPath)) {
      String sql = "INSERT INTO tags(name, workingDirectory,files) VALUES(?,?,?);";
      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, tag);
        preparedStatement.setString(2, workingPath);
        preparedStatement.setString(3, "");
        preparedStatement.executeUpdate();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Return True iff the selected Tag is in the tags table in the database.
   *
   * @param tag String The Tag we selected
   * @param workingPath String the workingPath the selected located
   * @return boolean
   */
  private boolean tagExist(String tag, String workingPath) {
    String sql = "SELECT name,workingDirectory FROM tags;";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        if (rs.getString("name").equals(tag)
            && rs.getString("workingDirectory").equals(workingPath)) {
          return true;
        }
      }
      return false;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return true;
  }

  /**
   * Delete the Tag tuple in the tags table in database.
   *
   * @param workingPath String the workingPath the selected located
   * @param tag String The Tag we selected
   */
  public void deleteTagTuple(String workingPath, String tag) {
    if (tagExist(tag, workingPath)) {
      String sql =
          "DELETE FROM tags WHERE name = '"
              + tag
              + "' AND workingDirectory = '"
              + workingPath
              + "';";
      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.executeUpdate();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Update the tag history for selected ControlFile into the tags table in database.
   *
   * @param controlFile ControlFile that we selected for updating History.
   */
  public void updateHistory(ControlFile controlFile, String fileType) {
    String currentTag =
        dataBaseDownLoader.getProperty(
            controlFile.getFileName(), "tags", controlFile.getLocation(), fileType);
    if (currentTag.length() == 0 || currentTag.substring(currentTag.length() - 1).equals("|")) {
      update(controlFile, fileType, "tags", currentTag + " |" + controlFile.getTags());
    } else {
      update(controlFile, fileType, "tags", currentTag + "|" + controlFile.getTags());
    }
  }
}
