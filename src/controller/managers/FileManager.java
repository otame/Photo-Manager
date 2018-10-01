package controller.managers;

import controller.ExceptionPrompter;
import controller.controlfiles.ControlFile;
import controller.database.DataBaseDownLoader;
import controller.database.DataBaseUpdater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The FileManager used to Manage all ControlFile class to manage the Tag editing functionality.
 *
 * @author Jiyuan Cheng
 * @author XIAWEI ZHANG
 */
public class FileManager {

  /** Logger to record the changes for photoManager. */
  private Logger logger = Logger.getLogger(FileManager.class.getName());

  /** The container to store all photo class. */
  private ArrayList<ControlFile> currentControlFiles;

  /** Class of DataBaseUpdater to modify database. */
  private DataBaseUpdater dataBaseUpdater;

  /** Class DatabaseDownloader which is used to download data from database. */
  private DataBaseDownLoader dataBaseDownLoader;

  /** The working directory of the photoManager. */
  private String rootPath;

  /** The File Type of this ControlFile. */
  private String controlFileType;

  /** ArrayList of String containing all the possible file extension formats. */
  private String[] extensions;

  /** The master Log file. */
  private File file = new File("FileManager.log");

  /**
   * The constructor of FileManager class storing the workingDirectory, the Type of ControlFile and
   * the extension name of the File.
   *
   * @param rootPath String the WorkingDirectory
   * @param controlFileType the Type of ControlFile
   * @param extensions String[] The String Array contains all possible extension name for a File
   */
  public FileManager(String rootPath, String controlFileType, String[] extensions) {
    dataBaseUpdater = new DataBaseUpdater();
    dataBaseDownLoader = new DataBaseDownLoader();
    this.rootPath = rootPath;
    this.controlFileType = controlFileType;
    this.extensions = extensions;
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileHandler fileHandler = new FileHandler("FileManager.log", true);
      logger = Logger.getLogger("test");
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
      logger.setLevel(Level.ALL);
    } catch (IOException e) {
      ExceptionPrompter.showFileNotExist();
    }
  }

  /**
   * Return the extension names of a ControlFile Class.
   *
   * @return String[] The possible extension names of a ControlFile
   */
  public String[] getExtensions() {
    return extensions;
  }

  /** Get all controlFiles from database in this workingDirectory in this type of ControlFile. */
  public void downloadPics() {
    currentControlFiles = dataBaseDownLoader.buildFiles(rootPath, controlFileType);
  }

  /**
   * Return the current ControlFile Classes
   *
   * @return ArrayList current controlFiles.
   */
  public ArrayList<ControlFile> getCurrentControlFiles() {
    return currentControlFiles;
  }

  /**
   * Update the selected ControlFile class to database.
   *
   * @param controlFile ControlFile
   */
  public void loadPictures(ControlFile controlFile) {
    if (controlFile.getTags().equals("")) {
      dataBaseUpdater.addFileData(controlFile, controlFileType);
    } else {
      for (String tag : controlFile.getTagsList()) {
        dataBaseUpdater.addTagData(tag, rootPath);
        dataBaseUpdater.updatePictures(controlFile, rootPath, tag, "add");
      }
      dataBaseUpdater.addFileData(controlFile, controlFileType);
    }
  }

  /**
   * Add an New Tag in this ControlFile Manager System.
   *
   * @param newTag String The new Tag.
   * @param rootPath String The workingDirectory.
   */
  public void addNewTag(String newTag, String rootPath) {

    if (!newTag.equals("")) {
      if (!newTag.contains("|")) {
        if (!newTag.contains(" ")) {

          if (!newTag.substring(0, 1).equals("@")) {
            newTag = "@" + newTag;
          }
          dataBaseUpdater.addTagData(newTag, rootPath);

          logger.info("The Tag: " + newTag + " has been created");

        } else {
          ExceptionPrompter.showInvalidSpaceTagMes();
        }
      } else {
        ExceptionPrompter.showInvalidTagCreation();
      }
    } else {
      ExceptionPrompter.showInvalidEmptyTagMes();
    }
  }

  /**
   * Delete the selected Tag.
   *
   * @param tag String The Tag we want to delete.
   */
  public void deleteTag(String tag) {

    ArrayList<ControlFile> controlFiles = dataBaseDownLoader.buildFiles(rootPath, controlFileType);
    for (ControlFile oneControlFile : controlFiles) {
      deleteTagInFile(tag, oneControlFile);
    }
  }

  /**
   * Delete Tag Tuple in the dataBase.
   *
   * @param tag The Tag user selected
   */
  public void deleteTagTuple(String tag) {
    dataBaseUpdater.deleteTagTuple(rootPath, tag);
  }

  /**
   * View all current Tags under this workingDirectory.
   *
   * @param rootPath String The working Directory in the photo Manager System.
   * @return ArrayList All tags.
   */
  public ArrayList<String> viewAllTags(String rootPath) {
    return dataBaseDownLoader.getAllTags(rootPath);
  }

  /**
   * Add a Tag in a ControlFile Class.
   *
   * @param tag String The selected Tag.
   * @param controlFile ControlFile The controlFile that we want to add tag into.
   */
  public void addTagToFile(String tag, ControlFile controlFile) {
    if (!(controlFile.getTagsList().contains(tag))) {
      controlFile.addTag(tag);
      controlFile.renameControlFile();
      dataBaseUpdater.updateHistory(controlFile, controlFileType);
      // need change the data in tags table as well and change the real PhotoName as well in tags
      // observer
      dataBaseUpdater.updatePictures(controlFile, rootPath, tag, "add");
      logger.info(
          "The Tag: " + tag + " has been added into the ControlFile: " + controlFile.getFileName());
    }
  }

  /**
   * Delete the selected tag in the selected ControlFile class.
   *
   * @param tag String The Selected Tag.
   * @param controlFile ControlFile The selected ControlFile.
   */
  public void deleteTagInFile(String tag, ControlFile controlFile) {

    if (controlFile.getTagsList().contains(tag)) {
      controlFile.deleteTag(tag);
      controlFile.renameControlFile();
      dataBaseUpdater.updateHistory(controlFile, controlFileType);
      // delete the pictures in tags table as well and change the real PhotoName as well in tags
      // observer
      dataBaseUpdater.updatePictures(controlFile, rootPath, tag, "delete");
      logger.info(
          "The Tag: "
              + tag
              + "has been deleted from the ControlFile: "
              + controlFile.getFileName());
    }
  }

  /**
   * Return the selected ControlFile history.
   *
   * @param controlFile ControlFile The selected ControlFile.
   * @return ArrayList The history of a ControlFile.
   */
  public ArrayList<String> viewHistoryTag(ControlFile controlFile) {
    ArrayList<String> historyTags = new ArrayList<>();
    String history =
        dataBaseDownLoader.getProperty(
            controlFile.getFileName(), "tags", controlFile.getLocation(), controlFileType);
    if (history.length() > 0) {
      if (history.substring(history.length() - 1).equals("|")) {
        history = history + " ";
      }
      String[] temp = history.split("\\|");
      historyTags.addAll(Arrays.asList(temp).subList(0, temp.length - 1));
    }
    logger.info(
        "The user has viewed the Tags with: "
            + historyTags.toString()
            + " for ControlFile: "
            + controlFile.getFileName());
    return historyTags;
  }

  /**
   * Back the selected ControlFile into a previous Tag that it contains.
   *
   * @param controlFile ControlFile The Selected ControlFile.
   * @param tags String The Tag wanted to go back
   */
  public void backHistory(ControlFile controlFile, String tags) {

    String oldTag = controlFile.getTags();
    ArrayList<String> temp = controlFile.getTagsList();
    for (String tag : temp) {
      controlFile.deleteTag(tag);
      controlFile.renameControlFile();
      dataBaseUpdater.updatePictures(controlFile, rootPath, tag, "delete");
    }
    if (!tags.equals(" ")) {
      String[] split = tags.split(" +");
      List<String> splitTags = new ArrayList<>(Arrays.asList(split));
      for (String item : splitTags) {
        controlFile.addTag(item);
        controlFile.renameControlFile();
        dataBaseUpdater.addTagData(item, rootPath);
        dataBaseUpdater.updatePictures(controlFile, rootPath, item, "add");
      }
    }

    logger.info(
        "The ControlFile with controlFile name: "
            + controlFile.getFileName()
            + " has change to Tags: FROM "
            + oldTag
            + " TO "
            + controlFile.getTags());
    dataBaseUpdater.updateHistory(controlFile, controlFileType);
  }

  /**
   * Return a ArrayList of ControlFile class in order to view all ControlFile class under this
   * working Directory and Selected Tag in the same type of Control File Type.
   *
   * @param tag String The selected Tag
   * @param rootPath String The working Directory
   * @return ArrayList The ControlFile Classes under this workingDirectory and Tag.
   */
  public ArrayList<ControlFile> viewOneTag(String tag, String rootPath) {
    String temp = dataBaseDownLoader.getFiles(rootPath, tag);
    ArrayList<ControlFile> controlFiles = new ArrayList<>();
    String[] split = temp.split("\\|");
    if (!split[0].equals("")) {
      for (String pic : split) {
        String[] tempPic = pic.split(",");
        String allTagHistory =
            dataBaseDownLoader.getProperty(tempPic[0], "tags", tempPic[1], controlFileType);
        if (allTagHistory == null) {
          return controlFiles;
        }
        String[] allTagArray = allTagHistory.split("\\|");
        String currentTag = allTagArray[allTagArray.length - 1];
        controlFiles.add(new ControlFile(tempPic[0], tempPic[1], currentTag));
      }
    }
    return controlFiles;
  }

  /**
   * Move the ControlFile from the old directory to a new Directory.
   *
   * @param controlFile ControlFile The selected ControlFile
   * @param mainDirectory String The old Directory
   * @param newDirectory String The new Directory
   */
  public void changePhotoLocation(
      ControlFile controlFile, String mainDirectory, String newDirectory) {

    controlFile.setLocation(newDirectory, mainDirectory, controlFileType);
  }
}
