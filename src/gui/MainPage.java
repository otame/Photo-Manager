package gui;

import controller.ExceptionPrompter;
import controller.PropertiesStorage;
import controller.ReadConfig;
import controller.controlfiles.ControlFile;
import controller.controlfiles.Photo;
import controller.managers.FileManager;
import controller.managers.PhotoManager;
import controller.managers.VideoManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The MainPage box for this software GUI.
 *
 * @author Yu Feng Gan
 * @author Jin Cheng He
 * @author XIAWEI ZHANG
 */
public class MainPage implements Initializable {
  /** Remove button in Javafx. */
  @FXML public Button removeBtn;
  /** ControlFile tab in Javafx. */
  @FXML public TableView<ControlFile> fileTab;
  /** Name column in Javafx. */
  @FXML public TableColumn<ControlFile, String> fileNameCol;
  /** Location column in Javafx. */
  @FXML public TableColumn<ControlFile, String> locationCol;
  /** Tags column in Javafx. */
  @FXML public TableColumn<ControlFile, String> tagsCol;
  /** Image view in load Javafx. */
  @FXML public ImageView imageView;
  /** Image view in view Javafx. */
  @FXML public ImageView displayPhoto;
  /** Load button in Javafx. */
  @FXML public Button loadBtn;
  /** Directory label in Javafx. */
  @FXML public Label directLab;
  /** View file tab in Javafx. */
  @FXML public Tab viewFile;
  /** Tag list view in Javafx. */
  @FXML public ListView<String> avaTagListView;
  /** ControlFile tableview in Javafx. */
  @FXML public TableView<ControlFile> fileTagTable;
  /** ControlFile name column in Javafx. */
  @FXML public TableColumn<ControlFile, String> fileTabCol;
  /** Path column in Javafx. */
  @FXML public TableColumn<ControlFile, String> pathTableCol;
  /** Tags column in Javafx. */
  @FXML public TableColumn<ControlFile, String> tagsInTableCol;
  /** Tag choice box in Javafx. */
  @FXML public ComboBox<String> tagChoiceBox;
  /** ControlFile plane in Javafx. */
  @FXML public TabPane filePlane;
  /** radio button to be selected when user want to work with video files under the view menu. */
  @FXML public RadioButton videoRaView;
  /** radio button to be selected when user want to work with photo files under the view menu. */
  @FXML public RadioButton photoRaView;
  /** radio button to be selected when user want to work with video files under the load menu. */
  @FXML public RadioButton videoRaLoad;
  /** radio button to be selected when user want to work with photo files under the load menu. */
  @FXML public RadioButton photoRaLoad;
  /** displays the selected video */
  @FXML public MediaView displayVideo;
  /** the video player for display Photo */
  @FXML public MediaView mediaView;
  /** Button that opens up filterAddOn.fxml */
  @FXML public Button addFilter;
  /** Button that can starting play video in load menu. */
  @FXML public Button startBtnLoad;
  /** Button that can pause video in load menu. */
  @FXML public Button pauseBtnLoad;
  /** Button that can starting play video in view menu. */
  @FXML public Button startBtnView;
  /** Button that can pause video in view menu. */
  @FXML public Button pauseBtnView;
  /** Toggle group in load menu */
  private final ToggleGroup fileTypeLoad = new ToggleGroup();
  /** Toggle group in view menu */
  private final ToggleGroup fileTypeView = new ToggleGroup();
  /** String storing the current working path that the user had chosen */
  private String curDir;
  /** boolean to check the program has been through initialize or not */
  private boolean initialized = false;
  /** manager that currently used */
  private FileManager manager;
  /** manager links photo gui to the background classes */
  private PhotoManager photoManager;
  /** manager links video gui to the background classes */
  private VideoManager videoManager;

  /**
   * Sets up the basic properties for the Graphics User Interface and resumes User's work if they
   * had already worked with our program
   *
   * @param location the url of mainpage
   * @param resources the resource of mainpage
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // setting table view and list view to multiple selection mode.
    fileTab.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    avaTagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fileTagTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fileTab.setEditable(true);
    fileTagTable.setEditable(true);
    // setting table colum
    fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
    locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));
    fileTabCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
    pathTableCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    tagsInTableCol.setCellValueFactory(new PropertyValueFactory<>("tags"));
    // setting radio group
    videoRaView.setToggleGroup(fileTypeView);
    photoRaView.setToggleGroup(fileTypeView);
    photoRaView.setSelected(true);
    displayVideo.setVisible(false);
    startBtnView.setVisible(false);
    pauseBtnView.setVisible(false);
    videoRaLoad.setToggleGroup(fileTypeLoad);
    photoRaLoad.setToggleGroup(fileTypeLoad);
    photoRaLoad.setSelected(true);
    mediaView.setVisible(false);
    startBtnLoad.setVisible(false);
    pauseBtnLoad.setVisible(false);
    // setting config
    ReadConfig.loadProperties();
    if (ReadConfig.getFirstTime()) {
      viewFile.setDisable(true);
      removeBtn.setDisable(true);
      loadBtn.setDisable(true);
      photoRaLoad.setDisable(true);
      videoRaLoad.setDisable(true);
    } else {
      curDir = ReadConfig.getDirectory();
      directLab.setText(curDir);
      photoManager = new PhotoManager(curDir);
      videoManager = new VideoManager(curDir);
      manager = photoManager;
      viewFile.setDisable(ReadConfig.getViewTab());
      if (ReadConfig.getSelectedTab().equals("ViewTab")) {
        filePlane.getSelectionModel().select(viewFile);
      }
    }
  }

  /**
   * Function for chooseBtn Button in gui. Allows User to pick a working directory, finds and
   * display a list of all required files under the path
   */
  public void chooseBtnAction() {
    DirectoryChooser dc = new DirectoryChooser();
    java.io.File mainDirectory = dc.showDialog(null);
    if (mainDirectory != null) {
      curDir = mainDirectory.getAbsolutePath();
      directLab.setText(curDir);
      java.io.File[] listFiles = mainDirectory.listFiles();
      photoManager = new PhotoManager(curDir);
      videoManager = new VideoManager(curDir);
      raLoadAction();
      removeBtn.setDisable(false);
      loadBtn.setDisable(false);
      viewFile.setDisable(false);
      photoRaLoad.setDisable(false);
      videoRaLoad.setDisable(false);
      PropertiesStorage.setDirectory(curDir);
      PropertiesStorage.setViewTab(false);
      PropertiesStorage.lastSelectedTab("LoadPhoto");
      PropertiesStorage.runConfig();
    }
  }

  /**
   * Function that help chooseBtnAction to import all required files under a directory and add these
   * files to the table.
   *
   * @param listFiles the listFiles that need to import.
   */
  private void importFile(File[] listFiles) {
    for (java.io.File file : listFiles) {
      if (file.isDirectory() && file.listFiles() != null) {
        importFile(file.listFiles());
      } else if (!file.getName().contains(",")) {
        String[] extensions = manager.getExtensions();
        for (String ext : extensions) {
          String[] temp = file.getName().split("\\.");
          importHelper(temp, ext, file);
        }
      }
    }
  }

  /**
   * import helper function that will import file with required extensions. If the file has tags
   * already, it will import the tags from the file.
   *
   * @param temp the name of file that split by space
   * @param ext the required extension
   * @param file the target file
   */
  private void importHelper(String[] temp, String ext, File file) {
    if (temp[temp.length - 1].equals(ext)) {
      String filename = "";
      StringBuilder tags = new StringBuilder();
      String path = file.getParent();
      if (file.getName().contains("@")) {
        String[] arr = temp[0].split(" +");
        ArrayList<String> name = new ArrayList<>(Arrays.asList(arr));
        for (String tag : name) {
          if (!tag.contains("@")) {
            filename = tag + "." + temp[temp.length - 1];
          } else {
            tags.append(tag).append(" ");
          }
        }
        tags.deleteCharAt(tags.length() - 1);
      } else {
        filename = file.getName();
      }
      ControlFile photo = new ControlFile(filename, path, tags.toString());
      file.renameTo(new File(file.getParent() + File.separator + photo.getFullName()));
      fileTab.getItems().add(photo);
    }
  }

  /**
   * Function for removeBtn Button in Load ControlFile Tab of the gui. Allows User to remove photos
   * they do not want to work with under the directory
   */
  public void removeBtnAction() {
    ObservableList<ControlFile> photosSelected, allControlFiles;
    photosSelected = fileTab.getSelectionModel().getSelectedItems();
    allControlFiles = fileTab.getItems();
    allControlFiles.removeAll(photosSelected);
  }

  /**
   * Read and Loads in all the photo under the working directory into our database , has the ability
   * to determine whether the photo had already added or not
   */
  public void loadBtnAction() {
    ObservableList<ControlFile> controlFiles = fileTab.getItems();
    for (ControlFile controlFile : controlFiles) {
      manager.loadPictures(controlFile);
    }
    fileTab.getItems().clear();
    PropertiesStorage.setViewTab(false);
    if (mediaView.getMediaPlayer() != null) {
      mediaView.getMediaPlayer().stop();
    }
  }

  /** A refresh function created to refresh the tableView within the gui */
  public void viewImageBtnAction() {
    String curTag = tagChoiceBox.getSelectionModel().getSelectedItem();
    if (curTag != null) {
      fileTagTable.getItems().clear();
      ArrayList<ControlFile> controlFileInTag;
      manager.downloadPics();
      if (curTag.equals("All")) {
        controlFileInTag = manager.getCurrentControlFiles();
      } else {
        controlFileInTag = manager.viewOneTag(curTag, curDir);
      }
      fileTagTable.getItems().addAll(controlFileInTag);
    }
  }

  /** A helper function that refreshes the available tags whenever it is needed to be updated */
  private void refreshAvaTagAction() {
    avaTagListView.getItems().clear();
    avaTagListView.getItems().addAll(manager.viewAllTags(curDir));
  }

  /**
   * A helper function that refreshes the items within the combo box whenever the items were to be
   * updated
   */
  private void refreshChoiceAction() {
    tagChoiceBox.getItems().clear();
    tagChoiceBox.getItems().add("All");
    tagChoiceBox.getItems().addAll(manager.viewAllTags(curDir));
    tagChoiceBox.getSelectionModel().selectFirst();
    viewImageBtnAction();
  }

  /**
   * method to be activated when the TableView is clicked in the "Load Picture" Tab Displays the
   * Image of the selected ControlFile
   */
  public void photoTabClicked() {
    fileTab.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            ControlFile controlFileSelected = fileTab.getSelectionModel().getSelectedItem();
            if (controlFileSelected != null) {
              java.io.File file =
                  new java.io.File(
                      controlFileSelected.getLocation()
                          + java.io.File.separator
                          + controlFileSelected.getFullName());
              if (photoRaLoad.isSelected()) {
                imageView.setImage(new Image(file.toURI().toString()));
              } else {
                if (mediaView.getMediaPlayer() != null) {
                  mediaView.getMediaPlayer().stop();
                }
                MediaPlayer videoPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mediaView.setMediaPlayer(videoPlayer);
                videoPlayer.play();
              }
            }
          }
        });
  }

  /**
   * Creates a new Tag the user desired, passes the new Tag created to database and calls the
   * refresh helper methods to keep the lists and table views up to date
   *
   * <p>reference for dialog: http://code.makery.ch/blog/javafx-dialogs-official/
   */
  public void newTagBtnAction() {
    TextInputDialog newTag = new TextInputDialog();
    newTag.setTitle("Entering a new Tag");
    newTag.setContentText("Please Enter a new Tag: ");
    Optional<String> tagName = newTag.showAndWait();
    tagName.ifPresent(s -> manager.addNewTag(s, curDir));
    refreshAvaTagAction();
    refreshChoiceAction();
  }

  /**
   * Remove the selected tag out of the program. Rename any pictures that was previously in that tag
   */
  public void removeTagBtnAction() {
    ObservableList<String> tags = avaTagListView.getSelectionModel().getSelectedItems();
    if (tags.size() == 0) {
      ExceptionPrompter.showInvalidTagSelection();
    } else {
      for (String tag : tags) {
        photoManager.deleteTag(tag);
        videoManager.deleteTag(tag);
        manager.deleteTagTuple(tag);
      }
      refreshAvaTagAction();
      refreshChoiceAction();
    }
  }

  /**
   * methods for addTag Button in the gui. adds selected Tags to selected Photos. Able to add
   * multiple tags to multiple of photos in the same time or perform single tag add
   */
  public void addTagToPhoto() {
    ObservableList<String> tagsSelected = avaTagListView.getSelectionModel().getSelectedItems();
    ObservableList<ControlFile> controlFileNameSelected =
        fileTagTable.getSelectionModel().getSelectedItems();
    if (!tagsSelected.isEmpty()) {
      if (!controlFileNameSelected.isEmpty()) {
        for (String tag : tagsSelected) {
          for (ControlFile controlFile : controlFileNameSelected) {
            manager.addTagToFile(tag, controlFile);
          }
        }
      } else {
        ExceptionPrompter.showInvalidFileSelection();
      }
      viewImageBtnAction();
    } else {
      ExceptionPrompter.showInvalidTagSelection();
    }
  }

  /**
   * methods for removeTagsFromPhoto Button in the gui. able to delete selected Tags to selected
   * Photos. Able to perform both single and multiple delete
   */
  public void setRemoveTagFromPhoto() {
    ObservableList<String> tagsSelected = avaTagListView.getSelectionModel().getSelectedItems();
    ObservableList<ControlFile> controlFileNameSelected =
        fileTagTable.getSelectionModel().getSelectedItems();
    if (!tagsSelected.isEmpty()) {
      if (!controlFileNameSelected.isEmpty()) {
        for (String tag : tagsSelected) {
          for (ControlFile controlFile : controlFileNameSelected) {
            manager.deleteTagInFile(tag, controlFile);
          }
        }
      } else {
        ExceptionPrompter.showInvalidFileSelection();
      }
      viewImageBtnAction();
    } else {
      ExceptionPrompter.showInvalidTagSelection();
    }
  }

  /**
   * gui Actions performed when the Tab is changed from "Load ControlFile" to "ViewPhoto" Refreshes
   * items in "View ControlFile" Tab and updates properties for config
   */
  public void clickedViewTab() {
    refreshAvaTagAction();
    refreshChoiceAction();
    raViewAction();
    PropertiesStorage.setDirectory(curDir);
    PropertiesStorage.setViewTab(false);
    PropertiesStorage.lastSelectedTab("ViewTab");
    PropertiesStorage.runConfig();
    if (mediaView.getMediaPlayer() != null) {
      mediaView.getMediaPlayer().stop();
    }
  }

  /** Displays the corresponding Image when user clicks on a photo in the "View ControlFile" Tab */
  public void clickedPhotoTagTable() {
    fileTagTable.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            ControlFile controlFileSelected = fileTagTable.getSelectionModel().getSelectedItem();
            if (controlFileSelected != null) {
              java.io.File file =
                  new java.io.File(
                      controlFileSelected.getLocation()
                          + java.io.File.separator
                          + controlFileSelected.getFullName());
              if (fileTypeView.getSelectedToggle().equals(photoRaView)) {
                displayPhoto.setImage(new Image(file.toURI().toString()));
              } else {
                if (displayVideo.getMediaPlayer() != null) {
                  displayVideo.getMediaPlayer().stop();
                }
                MediaPlayer videoPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                displayVideo.setMediaPlayer(videoPlayer);
                videoPlayer.play();
              }
            }
          }
        });
  }

  /**
   * Function for the "viewTagHistory" Button in gui, opens a new fxml containing the all the
   * previous tags that the selected photo owned. User is allowed to replace current tags with
   * historical tags
   */
  public void clickViewHistory() throws Exception {
    ControlFile controlFile = fileTagTable.getSelectionModel().getSelectedItem();
    if (controlFile == null) {
      ExceptionPrompter.showInvalidFileSelection();
    } else {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ViewHistory.fxml"));
      Parent root2 = fxmlLoader.load();
      Stage newStage = new Stage();
      newStage.setTitle("View History");
      newStage.setScene(new Scene(root2));
      ViewHistory history = fxmlLoader.<ViewHistory>getController();
      history.initHistory(manager.viewHistoryTag(controlFile));
      history.initStage();
      newStage.setAlwaysOnTop(true);
      newStage.initModality(Modality.APPLICATION_MODAL);
      newStage.showAndWait();
      if (history.getSelectedTag() != null) {
        if (history.getSelectedTag().equals("Empty")) {
          manager.backHistory(fileTagTable.getSelectionModel().getSelectedItem(), " ");
          history.cleanTag();
        } else {
          manager.backHistory(
              fileTagTable.getSelectionModel().getSelectedItem(), history.getSelectedTag());
          history.cleanTag();
        }
      }
      refreshChoiceAction();
      refreshAvaTagAction();
    }
  }

  /**
   * Function for "viewTagHistory" Button in gui, allows user to move a photo under the current
   * working directory to an new path they selected.
   */
  public void movePhoto() {
    ObservableList<ControlFile> photosSelected =
        fileTagTable.getSelectionModel().getSelectedItems();
    if (!photosSelected.isEmpty()) {
      String newPath;
      DirectoryChooser dc = new DirectoryChooser();
      java.io.File mainDirectory = dc.showDialog(null);
      if (mainDirectory != null) {
        newPath = mainDirectory.getAbsolutePath();
        for (ControlFile controlFile : photosSelected) {
          manager.changePhotoLocation(controlFile, curDir, newPath);
        }
      }
      viewImageBtnAction();
    } else {
      ExceptionPrompter.showInvalidMove();
    }
  }

  /** Help update the configuration properties when "Load ControlFile" Tab is clicked */
  public void loadFileAction() {
    if (initialized) {
      PropertiesStorage.lastSelectedTab("LoadPhoto");
      PropertiesStorage.setDirectory(curDir);
      PropertiesStorage.setViewTab(false);
      PropertiesStorage.runConfig();
    } else {
      initialized = true;
    }
  }

  /** Pops the readme.txt when the Menu is clicked */
  public void helpBtnAction() {
    String text = System.getProperty("user.dir") + java.io.File.separator + "readme.txt";
    openHelper(text);
  }

  /** open the master log file if it is exist */
  public void logBtnAction() {
    String log = System.getProperty("user.dir") + File.separator + "FileManager.log";
    openHelper(log);
  }

  /** Switch between photo or video in load menu. */
  public void raLoadAction() {
    if (photoRaLoad.isSelected()) {
      imageView.setVisible(true);
      if (mediaView.getMediaPlayer() != null) {
        mediaView.getMediaPlayer().stop();
      }
      mediaView.setVisible(false);
      startBtnLoad.setVisible(false);
      pauseBtnLoad.setVisible(false);
      manager = photoManager;
    } else {
      imageView.setVisible(false);
      mediaView.setVisible(true);
      startBtnLoad.setVisible(true);
      pauseBtnLoad.setVisible(true);
      manager = videoManager;
    }
    fileTab.getItems().clear();
    File dirct = new File(curDir);
    importFile(dirct.listFiles());
  }

  /** Switch between photo or video in view menu. */
  public void raViewAction() {
    if (photoRaView.isSelected()) {
      displayPhoto.setVisible(true);
      addFilter.setVisible(true);
      if (displayVideo.getMediaPlayer() != null) {
        displayVideo.getMediaPlayer().stop();
      }
      displayVideo.setVisible(false);
      startBtnView.setVisible(false);
      pauseBtnView.setVisible(false);
      manager = photoManager;
    } else {
      displayPhoto.setVisible(false);
      addFilter.setVisible(false);
      displayVideo.setVisible(true);
      startBtnView.setVisible(true);
      pauseBtnView.setVisible(true);
      manager = videoManager;
    }
    viewImageBtnAction();
  }

  /** open the filter window that can add filter to a photo. */
  public void openFilter() {
    ControlFile controlFile = fileTagTable.getSelectionModel().getSelectedItem();

    try {
      Photo photo =
          new Photo(controlFile.getFileName(), controlFile.getLocation(), controlFile.getTags());
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FilterAddOn.fxml"));
      Parent root2 = fxmlLoader.load();
      Stage newStage = new Stage();
      newStage.setTitle("Add Filter");
      newStage.setScene(new Scene(root2));
      FilterBox filterAddOn = fxmlLoader.<FilterBox>getController();
      filterAddOn.initFilter(photo, new PhotoManager(curDir));
      filterAddOn.initStage();
      newStage.setAlwaysOnTop(true);
      newStage.initModality(Modality.APPLICATION_MODAL);
      newStage.showAndWait();
      viewImageBtnAction();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }

  /** open the directory of selected file */
  public void opendirAction() {
    ControlFile controlFile = fileTagTable.getSelectionModel().getSelectedItem();
    if (controlFile != null) {
      String file = controlFile.getLocation();
      openHelper(file);
    } else {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }

  /**
   * the helper function that can open the file or director in different os.
   *
   * @param file the location of file or director.
   */
  public void openHelper(String file) {
    String os = System.getProperty("os.name").toLowerCase();
    Runtime rt = Runtime.getRuntime();
    try {
      if (os.contains("win")) {
        rt.exec("explorer " + file);
      } else if (os.contains("mac")) {
        rt.exec("open " + file);
      } else {
        rt.exec("xdg-open " + file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** starting play video in load menu */
  public void setStartBtnLoad() {
    try {
      mediaView.getMediaPlayer().play();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }

  /** pause video in load menu */
  public void setPauseBtnLoad() {
    try {
      mediaView.getMediaPlayer().pause();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }

  /** starting play video in view menu */
  public void setStartBtnView() {
    try {
      displayVideo.getMediaPlayer().play();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }

  /** pause video in view menu */
  public void setPauseBtnView() {
    try {
      displayVideo.getMediaPlayer().pause();
    } catch (Exception e) {
      ExceptionPrompter.showInvalidFileSelection();
    }
  }
}
