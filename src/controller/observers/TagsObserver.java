package controller.observers;

import controller.controlfiles.ControlFile;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The Tag Observer to observe the Tags changes in the ControlFile.
 *
 * @author XIAWEI ZHANG
 * @author Jin Cheng He
 */
public class TagsObserver implements java.util.Observer {

  /**
   * The add Helper to help the update method to add Tag in this observable.
   *
   * @param temp String Tag
   * @param controlFile ControlFile the selected ControlFile
   */
  private void addHelper(String temp, ControlFile controlFile) {
    String addTag = temp.substring(3, temp.length());
    if (controlFile.getTags().equals("")) {
      controlFile.setTags(addTag);
    } else {
      String tag = controlFile.getTags();
      controlFile.setTags(tag + " " + addTag);
    }
  }

  /**
   * The add Helper to help the update method to delete Tag in this observable.
   *
   * @param temp String Tag
   * @param controlFile ControlFile the selected ControlFile
   */
  private void deleteHelper(String temp, ControlFile controlFile) {
    String deleteTag = temp.substring(3, temp.length());
    ArrayList<String> arrS = controlFile.getTagsList();
    arrS.remove(deleteTag);
    StringBuilder stringBuilder = new StringBuilder();
    if (arrS.size() != 0) {
      for (String arr : arrS) {
        stringBuilder.append(arr).append(" ");
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }
    controlFile.setTags(stringBuilder.toString());
  }

  /**
   * Update the changes of Tags in this observable and update the changes in database.
   *
   * @param observable ControlFile selected ControlFile
   * @param arg Object The argument collected by the observer
   */
  @Override
  public void update(Observable observable, Object arg) {
    if (arg instanceof String) {
      String temp = (String) arg;
      ControlFile controlFile = (ControlFile) observable;
      // adding tag
      if (temp.substring(0, 3).equals("at:")) {
        addHelper(temp, controlFile);
      } else if (temp.substring(0, 3).equals("dt:")) {
        deleteHelper(temp, controlFile);
      }
    }
  }
}
