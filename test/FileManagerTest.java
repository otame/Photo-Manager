import controller.controlfiles.ControlFile;
import controller.database.DataBaseConnection;
import controller.managers.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileManagerTest {

  private Connection connection;
  private ArrayList<String> tempDirs = new ArrayList<>();

  private boolean sameControlFile(ControlFile cf0, ControlFile cf1) {
    return cf0.getFileName().equals(cf1.getFileName())
        && cf0.getLocation().equals(cf1.getLocation())
        && cf0.getTags().equals(cf1.getTags());
  }

  private void cleanTables() {
    String sql = "DELETE FROM photo WHERE location LIKE ?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      int pos = tempDirs.get(0).lastIndexOf(java.io.File.separator);
      stmt.setString(1, tempDirs.get(0).substring(0, pos + 1) + "%");
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace(out);
    }

    sql = "DELETE FROM tags WHERE workingDirectory LIKE ?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      int pos = tempDirs.get(0).lastIndexOf(java.io.File.separator);
      stmt.setString(1, tempDirs.get(0).substring(0, pos + 1) + "%");
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace(out);
    }
  }

  @BeforeEach
  void setUp() {
    String tempDir;

    try {
      tempDir = Files.createTempDirectory("temp").toString();
      //      System.setProperty("java.io.tmpdir", tempDir);
      tempDirs.add(tempDir);
      out.println("Create TempDir: " + tempDir);
      tempDir = Files.createTempDirectory("temp").toString();
      tempDirs.add(tempDir);
      out.println("Create TempDir: " + tempDir);
    } catch (IOException e) {
      e.printStackTrace(out);
    }

    connection = DataBaseConnection.dbConnector();

    cleanTables();
  }

  @AfterEach
  void tearDown() {
    cleanTables();

    for (String tempDir : tempDirs) {
      File currDir = new File(tempDir);
      String[] currDirFiles = currDir.list();

      if (currDirFiles != null) {
        try {
          for (String currDirFile : currDirFiles) {
            File currFile = new File(currDir.getPath(), currDirFile);
            if (!currFile.delete()) {
              out.println("Failed to delete file: " + currFile.toString());
            }
          }
        } catch (NullPointerException e) {
          e.printStackTrace(out);
        }
      }

      if (!currDir.delete()) {
        out.println("Failed to delete directory: " + currDir.toString());
      }
      out.println("TearDown TempDir: " + tempDir);
    }
  }

  @Test
  void downloadPics() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    ArrayList<ControlFile> currentControlFiles = fm.getCurrentControlFiles();

    assertTrue(currentControlFiles == null);

    File tempFile;

    fm.addNewTag("@tag1", tempDirs.get(0));
    fm.addNewTag("@tag2", tempDirs.get(0));
    fm.addNewTag("@tag3", tempDirs.get(0));

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf0 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf0);

    fm.addTagToFile("@tag1", cf0);
    fm.addTagToFile("@tag2", cf0);

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    filename = tempFile.getName();
    ControlFile cf1 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf1);

    fm.addTagToFile("@tag2", cf1);
    fm.addTagToFile("@tag3", cf1);

    ArrayList<ControlFile> cfs = fm.viewOneTag("@tag2", tempDirs.get(0));
    assertTrue(sameControlFile(cfs.get(0), cf0));
    assertTrue(sameControlFile(cfs.get(1), cf1));
    assertEquals(cfs.size(), 2);
  }

  @Test
  void loadPictures() {
    ControlFile cf = new ControlFile("non-exist", tempDirs.get(0), "@tag1 @tag2 @tag3");
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});

    String sql = "DELETE FROM tags WHERE workingDirectory LIKE ?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      int pos = tempDirs.get(0).lastIndexOf(java.io.File.separator);
      stmt.setString(1, tempDirs.get(0).substring(0, pos + 1) + "%");
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace(out);
    }

    fm.loadPictures(cf);

    sql = "SELECT name FROM tags WHERE workingDirectory=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals("@tag1", rs.getString(1));
      rs.next();
      assertEquals("@tag2", rs.getString(1));
      rs.next();
      assertEquals("@tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void addNewTag() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.addNewTag("@tagX", tempDirs.get(0));

    String sql = "SELECT name FROM tags WHERE workingDirectory=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals("@tagX", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void deleteTag() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    File tempFile;

    fm.addNewTag("@tag1", tempDirs.get(0));
    fm.addNewTag("@tag2", tempDirs.get(0));
    fm.addNewTag("@tag3", tempDirs.get(0));

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf0 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf0);

    fm.addTagToFile("@tag1", cf0);
    fm.addTagToFile("@tag2", cf0);

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    filename = tempFile.getName();
    ControlFile cf1 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf1);

    fm.addTagToFile("@tag2", cf1);
    fm.addTagToFile("@tag3", cf1);

    fm.deleteTag("@tag2");

    String sql = "SELECT tags FROM photo WHERE location=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals(" |@tag1|@tag1 @tag2|@tag1", rs.getString(1));
      rs.next();
      assertEquals(" |@tag2|@tag2 @tag3|@tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void deleteTagTuple() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.addNewTag("@tag1", tempDirs.get(0));
    fm.addNewTag("@tag2", tempDirs.get(0));
    fm.addNewTag("@tag3", tempDirs.get(0));

    fm.deleteTagTuple("@tag2");

    String sql = "SELECT name FROM tags WHERE workingDirectory=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals("@tag1", rs.getString(1));
      rs.next();
      assertEquals("@tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void addTagToFile() {
    File tempFile;
    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.loadPictures(cf);

    fm.addTagToFile("@tag1", cf);
    fm.addTagToFile("@tag2", cf);
    fm.addTagToFile("@tag3", cf);

    String sql = "SELECT tags FROM photo WHERE location=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals(" |@tag1|@tag1 @tag2|@tag1 @tag2 @tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void deleteTagInFile() {
    File tempFile;
    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.loadPictures(cf);

    fm.addTagToFile("@tag1", cf);
    fm.addTagToFile("@tag2", cf);
    fm.addTagToFile("@tag3", cf);

    fm.deleteTagInFile("@tag1", cf);

    String sql = "SELECT tags FROM photo WHERE location=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals(" |@tag1|@tag1 @tag2|@tag1 @tag2 @tag3|@tag2 @tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }

    fm.deleteTagInFile("@tag2", cf);

    sql = "SELECT tags FROM photo WHERE location=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals(" |@tag1|@tag1 @tag2|@tag1 @tag2 @tag3|@tag2 @tag3|@tag3", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }

    fm.deleteTagInFile("@tag3", cf);

    sql = "SELECT tags FROM photo WHERE location=?;";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, tempDirs.get(0));
      ResultSet rs = stmt.executeQuery();
      rs.next();
      assertEquals(" |@tag1|@tag1 @tag2|@tag1 @tag2 @tag3|@tag2 @tag3|@tag3|", rs.getString(1));
    } catch (SQLException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void viewAllTags() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.addNewTag("@tag1", tempDirs.get(0));
    fm.addNewTag("@tag2", tempDirs.get(0));
    fm.addNewTag("@tag3", tempDirs.get(0));

    ArrayList<String> allTags = new ArrayList<>(Arrays.asList("@tag1", "@tag2", "@tag3"));

    assertTrue(fm.viewAllTags(tempDirs.get(0)).equals(allTags));
  }

  @Test
  void viewHistoryTag() {
    File tempFile;
    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.loadPictures(cf);

    fm.addTagToFile("@tag1", cf);
    fm.addTagToFile("@tag2", cf);
    fm.addTagToFile("@tag3", cf);
    fm.deleteTagInFile("@tag2", cf);

    ArrayList<String> historyTag =
        new ArrayList<>(Arrays.asList(" ", "@tag1", "@tag1 @tag2", "@tag1 @tag2 @tag3"));
    assertTrue(fm.viewHistoryTag(cf).equals(historyTag));
  }

  @Test
  void backHistory() {
    File tempFile;
    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    fm.loadPictures(cf);

    fm.addTagToFile("@tag1", cf);
    fm.addTagToFile("@tag2", cf);
    fm.addTagToFile("@tag3", cf);
    fm.deleteTagInFile("@tag2", cf);

    fm.backHistory(cf, "@tagX @tagY");
    fm.backHistory(cf, "@tagZ");

    ArrayList<String> historyTag =
        new ArrayList<>(
            Arrays.asList(
                " ", "@tag1", "@tag1 @tag2", "@tag1 @tag2 @tag3", "@tag1 @tag3", "@tagX @tagY"));
    assertTrue(fm.viewHistoryTag(cf).equals(historyTag));
  }

  @Test
  void viewOneTag() {
    FileManager fm = new FileManager(tempDirs.get(0), "photo", new String[] {"jpg", "jpeg", "png"});
    File tempFile;

    fm.addNewTag("@tag1", tempDirs.get(0));
    fm.addNewTag("@tag2", tempDirs.get(0));
    fm.addNewTag("@tag3", tempDirs.get(0));

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    String filename = tempFile.getName();
    ControlFile cf0 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf0);

    fm.addTagToFile("@tag1", cf0);
    fm.addTagToFile("@tag2", cf0);

    try {
      tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
    } catch (IOException e) {
      e.printStackTrace(out);
      return;
    }

    filename = tempFile.getName();
    ControlFile cf1 = new ControlFile(filename, tempDirs.get(0), "");

    fm.loadPictures(cf1);

    fm.addTagToFile("@tag2", cf1);
    fm.addTagToFile("@tag3", cf1);

    ArrayList<ControlFile> cfs = fm.viewOneTag("@tag2", tempDirs.get(0));
    assertTrue(sameControlFile(cfs.get(0), cf0));
    assertTrue(sameControlFile(cfs.get(1), cf1));
    assertEquals(cfs.size(), 2);
  }
}
