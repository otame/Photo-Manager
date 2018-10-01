package controller;

import controller.controlfiles.ControlFile;
import javafx.scene.control.Alert;

/**
 * Class that manages the exception prompts within the program
 *
 * @author Jiyuan Cheng
 * @author XIAWEI ZHANG
 */
public class ExceptionPrompter {
  /** methods that helps to create a prompt dialog when exception is raised */
  private static void showInvalidHelper(String message, String warningTitle) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(warningTitle);
    alert.setHeaderText(message);
    alert.showAndWait();
  }

  /** Exception to be called when the user trying to create a tag containing space */
  public static void showInvalidSpaceTagMes() {
    showInvalidHelper("The tag cannot contain space.", "Invalid Tag");
  }

  /** Exception to be called when the user trying to create an empty tag */
  public static void showInvalidEmptyTagMes() {
    showInvalidHelper("The tag cannot be Empty.", "Invalid Tag");
  }

  /** Exception to be called when the user trying delete a tag that the controlFile does not have */
  public static void showNotExistTagMes() {
    showInvalidHelper("The selected tag is not in this current file.", "Invalid Tag");
  }

  /**
   * Exception to be called when the user is trying to load in controlFile that has already been
   * loaded
   */
  public static void showInvalidLoad(ControlFile controlFile) {
    showInvalidHelper(
        "ControlFile " + controlFile.getFullName() + " has been loaded already.", "Invalid Load");
  }

  /**
   * Exception to be called when the user is trying to move a file to a directory that has another
   * file with the exact same name
   */
  public static void showDuplicateFileMes() {
    showInvalidHelper("Destination folder contains file with the same name.", "Invalid Move");
  }

  /** Exception to be called when the user is trying to perform an invalid move */
  public static void showInvalidMove() {
    showInvalidHelper("Invalid Move!", "Invalid Move");
  }

  /** Exception to be called when the user is trying to perform actions without selecting a file */
  public static void showInvalidFileSelection() {
    showInvalidHelper("Please select a file first.", "Invalid ControlFile Selection");
  }

  /** Exception to be called when the user is trying to perform actions without selecting a tag */
  public static void showInvalidTagSelection() {
    showInvalidHelper("Please select a Tag first", "Invalid Tag Selection");
  }

  public static void showInvalidTagCreation() {
    showInvalidHelper("New Tag should not cantain a '|' symbol ", "Invalid Tag Creation");
  }

  public static void showInvalidFilterColor() {
    showInvalidHelper(
        "Color is not selected Please select a Color first ", "Invalid Color Filtering");
  }

  public static void showFileNotExist() {
    showInvalidHelper("File not Found", "File Not found");
  }
}
