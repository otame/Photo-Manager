package controller.observers;

import controller.ExceptionPrompter;
import controller.controlfiles.ControlFile;
import controller.database.DataBaseUpdater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The Location Observer to observe the location changes for the observable.
 *
 * @author Jin Cheng He
 * @author Yu Feng Gan
 * @author Jiyuan Cheng
 * @author XIAWEI ZHANG
 */
public class LocationObserver implements java.util.Observer {

  /** DataBaseDownLoader used to get ControlFile data from the database. */
  private DataBaseUpdater dataBaseUpdater;

  /** The location Observer constructor to observe all location changes for the Observable. */
  public LocationObserver() {
    dataBaseUpdater = new DataBaseUpdater();
  }

  /**
   * Update the Location changes to the database.
   *
   * @param arg Object The argument collected by the observer
   */
  @Override
  public void update(Observable observable, Object arg) {
    // updateLocation on dataBase
    ControlFile controlFile = (ControlFile) observable;
    if (arg instanceof ArrayList) {
      ArrayList temp = (ArrayList) arg;
      String newLocation = (String) temp.get(0);
      String mainDir = (String) temp.get(1);
      String fileType = (String) temp.get(2);
      Path mainPath = Paths.get(mainDir);
      Path sourcePath =
          Paths.get(controlFile.getLocation() + java.io.File.separator + controlFile.getFullName());
      Path newPath = Paths.get(newLocation + java.io.File.separator + controlFile.getFullName());
      if (!Files.exists(newPath)) {
        ArrayList<String> tags = controlFile.getTagsList();
        if (newPath.startsWith(mainPath)) {
          innerMoveHelper(tags, controlFile, mainDir, newLocation, fileType);
        } else {
          outMoveHelper(tags, controlFile, mainDir, newLocation, fileType);
        }
        try {
          Files.move(sourcePath, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
          ExceptionPrompter.showInvalidMove();
        }
      } else {
        ExceptionPrompter.showDuplicateFileMes();
      }
    }
  }

  /**
   * The help method to help the update method to move a ControlFile into a subdirectory
   * controlFile.
   *
   * @param tags ArrayList tags that this ControlFile contains
   * @param controlFile ControlFile The Selected ControlFile
   * @param mainDir String The old directory path
   * @param newLocation String The new directory path
   */
  private void innerMoveHelper(
      ArrayList<String> tags,
      ControlFile controlFile,
      String mainDir,
      String newLocation,
      String fileType) {
    if (!tags.get(0).equals("")) {
      for (String tag : tags) {
        dataBaseUpdater.updatePictures(controlFile, mainDir, tag, "cl:" + newLocation);
      }
    }
    dataBaseUpdater.update(controlFile, fileType, "location", newLocation);
  }

  /**
   * The help method to help the update method to move a ControlFile into outside directory
   * controlFile.
   *
   * @param tags ArrayList tags that this ControlFile contains
   * @param controlFile ControlFile The Selected ControlFile
   * @param mainDir String The Old directory path
   * @param newLocation String The new directory path
   */
  private void outMoveHelper(
      ArrayList<String> tags,
      ControlFile controlFile,
      String mainDir,
      String newLocation,
      String fileType) {
    if (!tags.get(0).equals("")) {
      for (String tag : tags) {
        dataBaseUpdater.updatePictures(controlFile, mainDir, tag, "delete");
        dataBaseUpdater.addTagData(tag, newLocation);
        dataBaseUpdater.updatePictures(controlFile, newLocation, tag, "add");
      }
    }
    dataBaseUpdater.update(controlFile, fileType, "location", newLocation);
  }
}
