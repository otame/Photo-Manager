package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * The GUI box for viewHistory for this GUI mainPage.
 *
 * @author Yu Feng Gan
 * @author Jin Cheng He
 * @author Jiyuan Cheng
 */
public class ViewHistory {
  /** shows the historical tag of a certain photo */
  @FXML public ListView<String> previousTags;
  /** A button that performs the clickedSelectedTag method */
  @FXML public Button backToTag;
  /** A button that performs the cancelBtn method when it is clicked */
  @FXML public Button cancelBtn;
  /** returns a list of previous tags of the selected photo */
  private ArrayList<String> historyTags;
  /** returns the selected Historical tag the user had chosen */
  private static String selectedTag;

  /**
   * Initialize the History box in Gui.
   *
   * @param historyTags ArrayList the history Tags.
   */
  void initHistory(ArrayList<String> historyTags) {
    this.historyTags = historyTags;
  }

  /** Sets up the basic properties for the ViewHistory gui */
  void initStage() {
    for (String history : historyTags) {
      if (history.equals(" ")) {
        previousTags.getItems().add("Empty");
      } else {
        previousTags.getItems().add(history);
      }
    }
  }

  /** set the selected Tag into null */
  void cleanTag() {
    selectedTag = null;
  }

  /** records the historical tag selected and close the stage */
  public void clickSelectedTag() {
    selectedTag = (String) previousTags.getSelectionModel().getSelectedItem();
    Stage stage = (Stage) backToTag.getScene().getWindow();
    stage.close();
  }

  /**
   * returns the tag that is currently selected
   *
   * @return String the history of the Selected Control File
   */
  String getSelectedTag() {
    return selectedTag;
  }

  /** closes the stage */
  public void cancelBtn() {
    selectedTag = null;
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }
}
