package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class that initialize the gui of the program
 *
 * @author Jin Cheng He
 * @author Yu Feng Gan
 */
public class Control extends Application {

  /** The Main Stage for this program */
  private Stage primaryStage;

  /**
   * The main method for this program.
   *
   * @param args String[] the args in stack.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /** Sets up the layout of the main page */
  @Override
  public void start(Stage primaryStage) throws IOException {
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("ControlFile Manager");
    displayMainPage();
  }

  /** Calls the MainPage.fxml and start the gui */
  private void displayMainPage() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(Control.class.getResource("MainPage.fxml"));
    TabPane mainLayout1 = loader.load();
    Scene scene = new Scene(mainLayout1);
    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.setOnCloseRequest(e -> Platform.exit());
  }
}
