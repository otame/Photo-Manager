package controller.controlfiles;

import controller.observers.LocationObserver;
import controller.observers.TagsObserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Observable;

/**
 * The ControlFile class that used to manage.
 *
 * @author XIAWEI ZHANG
 * @author Jin Cheng He
 */
public class ControlFile extends Observable {

  /** fileName of a ControlFile */
  private String fileName;
  /** oldFileName of a ControlFile */
  private String oldName;
  /** Location of a ControlFile */
  private String location;
  /** Tags of a ControlFile */
  private String tags;
  /** Date of ControlFile */
  private String date;

  /**
   * The ControlFile constructor to store the file's name, location, and tags
   *
   * @param fileName a String containing the original file name of the Controlled File
   * @param location String containing the path to the designated the Controlled File
   * @param tags a String containing all the tags in the Controlled File
   */
  public ControlFile(String fileName, String location, String tags) {
    this.fileName = fileName;
    this.location = location;
    this.tags = tags;
    this.oldName = "";
    Date date1 = new Date();
    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1);
    // Location Observer added
    this.addObserver(new LocationObserver());
    // Tags Observer added
    this.addObserver(new TagsObserver());
  }

  /** getter method for the private variable fileName, provides a String containing fileName */
  public String getFileName() {
    return fileName;
  }

  /** getter method for the private variable location, provides a String containing location */
  public String getLocation() {
    return location;
  }

  /** getter method for the private variable tags, provides a String containing tags */
  public String getTags() {
    return tags;
  }

  /** setter method for the private variable location */
  public void setLocation(String location, String mainDirectory, String controlFileType) {
    setChanged();
    ArrayList<String> changes = new ArrayList<>();
    changes.add(location);
    changes.add(mainDirectory);
    changes.add(controlFileType);
    notifyObservers(changes);
    this.location = location;
  }

  /** setter method for the private variable tags */
  public void setTags(String tags) {
    this.tags = tags;
  }

  /** getter method for the current date */
  public String getDate() {
    return date;
  }

  /** add tags to pre-existing tags in variable tag */
  public void addTag(String tag) {
    oldName = getFullName();
    setChanged();
    notifyObservers("at:" + tag);
  }

  /** deletes the input tags from the private tag variable */
  public void deleteTag(String tag) {
    oldName = getFullName();
    setChanged();
    notifyObservers("dt:" + tag);
  }

  /**
   * returns the the Controlled File's most recent name with regard to the newly added or deleted
   * tags
   */
  public String getFullName() {
    if (!tags.equals("")) {
      return tags + " " + fileName;
    } else {
      return fileName;
    }
  }

  /** returns a String list of tags the Controlled File current has */
  public ArrayList<String> getTagsList() {
    String[] arr = tags.split(" +");
    return new ArrayList<>(Arrays.asList(arr));
  }

  /** Update the path of the Controlled File file after an adjustment is made */
  public boolean renameControlFile() {
    String newName = getFullName();
    java.io.File file = new java.io.File(location + java.io.File.separator + oldName);
    return file.renameTo(new java.io.File(location + java.io.File.separator + newName));
  }
}
