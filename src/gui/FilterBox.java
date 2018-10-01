package gui;

import controller.ExceptionPrompter;
import controller.controlfiles.Photo;
import controller.managers.PhotoManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The GUI box for filter Color on a Photo Class.
 *
 * @author Yu Feng Gan
 * @author Jin Cheng He
 */
public class FilterBox {

  /** The frame of the FilterBox Panel */
  @FXML public ImageView frame;

  /** The view of an image */
  @FXML public ImageView imageView;

  /** The Button for cancel */
  @FXML public Button cancelBtn;

  /** The Button for save */
  @FXML public Button saveBtn;

  /** The Photo that is selected by user */
  private Photo photo;

  /** The current PhotoManger */
  private PhotoManager manager;

  /** The BufferedImage that is created by Photo class */
  private BufferedImage filterImage;

  /**
   * initialize this entire GUI box by constructing the Photo Class and PhotoManager.
   *
   * @param photo Photo user selected
   * @param manager PhotoManager The same workingDirectory PhotoManager
   */
  void initFilter(Photo photo, PhotoManager manager) {
    this.photo = photo;
    this.manager = manager;
  }

  /** Initialize the Stage for this Gui. */
  void initStage() {
    File file = new File(photo.getLocation() + File.separator + photo.getFullName());
    imageView.setImage(new Image(file.toURI().toString()));
  }

  /** The save Button for user to save the filtered file. */
  public void saveBtnAction() {
    try {
      manager.save(photo, filterImage);
      Stage stage = (Stage) saveBtn.getScene().getWindow();
      stage.close();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFilterColor();
    }
  }

  /** The cancel Button for user to cancel this filter Box. */
  public void cancelBtnAction() {
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }

  /** The red Button for user to paint red color on the selected photo. */
  public void redAction() {
    filterHelper("red");
  }

  /** The blue Button for user to paint blue color on the selected photo. */
  public void blueAction() {
    filterHelper("blue");
  }

  /** The green Button for user to paint green color on the selected photo. */
  public void greenAction() {
    filterHelper("green");
  }

  /**
   * The method to help redAction, blueAction, and greenAction to filter the color of the selected
   * Image.
   *
   * @param color String the color user wanted to filter
   */
  private void filterHelper(String color) {
    Photo photo1 = new Photo(photo.getFileName(), photo.getLocation(), photo.getTags());
    BufferedImage bufferedImage = manager.filterColor(photo1, color);
    filterImage = bufferedImage;
    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
    imageView.setImage(image);
  }
}
