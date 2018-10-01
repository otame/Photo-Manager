import controller.controlfiles.ControlFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlFileTest {

  private ArrayList<String> tempDirs = new ArrayList<>();

  @BeforeEach
  void setUp() {
    String tempDir;

    try {
      tempDir = Files.createTempDirectory("temp").toString();
      System.setProperty("java.io.tmpdir", tempDir);
      tempDirs.add(tempDir);
      out.println("Create TempDir: " + tempDir);
      tempDir = Files.createTempDirectory("temp").toString();
      tempDirs.add(tempDir);
      out.println("Create TempDir: " + tempDir);
    } catch (IOException e) {
      e.printStackTrace(out);
    }
  }

  @AfterEach
  void tearDown() {
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
  void getFileName() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    assertEquals("non-exist", cf.getFileName());
  }

  @Test
  void getLocation() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    assertEquals("/", cf.getLocation());
  }

  @Test
  void getTags() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    assertEquals("@tag1 @tag2 @tag3", cf.getTags());
  }

  @Test
  void getTagsList() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    ArrayList<String> tagsList = new ArrayList<>(Arrays.asList("@tag1", "@tag2", "@tag3"));
    assertTrue(cf.getTagsList().equals(tagsList));
  }

  @Test
  void getFullName() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    assertEquals("@tag1 @tag2 @tag3 non-exist", cf.getFullName());
  }

  @Test
  void getDate() {
    ControlFile cf = new ControlFile("non-exist", "/", "@tag1 @tag2 @tag3");
    try {
      Date cfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cf.getDate());
      Date currDate = new Date();
      assertTrue(currDate.getTime() > cfDate.getTime());
      // difference of at most 2 seconds (2000 milliseconds) are allowed
      assertTrue(currDate.getTime() - cfDate.getTime() < 2000);
    } catch (ParseException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void setLocation() {
    try {
      File tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
      String filename = tempFile.getName();
      ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
      cf.setLocation(tempDirs.get(1), tempDirs.get(0), "photo");
      assertEquals(tempDirs.get(1), cf.getLocation());
    } catch (IOException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void addTag() {
    try {
      File tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
      String filename = tempFile.getName();
      ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
      cf.addTag("@tag1");
      assertEquals("@tag1", cf.getTags());
      cf.addTag("@tag2");
      assertEquals("@tag1 @tag2", cf.getTags());
      cf.addTag("@tag3");
      assertEquals("@tag1 @tag2 @tag3", cf.getTags());
    } catch (IOException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void deleteTag() {
    try {
      File tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
      String filename = tempFile.getName();
      ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
      cf.addTag("@tag1");
      cf.addTag("@tag2");
      cf.addTag("@tag3");
      cf.deleteTag("@tag3");
      assertEquals("@tag1 @tag2", cf.getTags());
      cf.addTag("@tag3");
      cf.deleteTag("@tag2");
      assertEquals("@tag1 @tag3", cf.getTags());
      cf.addTag("@tag2");
      cf.deleteTag("@tag1");
      assertEquals("@tag3 @tag2", cf.getTags());
    } catch (IOException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }

  @Test
  void renamePhoto() {
    try {
      File tempFile = File.createTempFile("photo", ".png", new File(tempDirs.get(0)));
      String filename = tempFile.getName();
      ControlFile cf = new ControlFile(filename, tempDirs.get(0), "");
      cf.addTag("@tag1");
      out.println(cf.renameControlFile());
      cf.addTag("@tag2");
      out.println(cf.renameControlFile());
      cf.addTag("@tag3");
      out.println(cf.renameControlFile());
      assertTrue(
          new File(tempDirs.get(0) + java.io.File.separator + "@tag1 @tag2 @tag3 " + filename)
              .exists());
    } catch (IOException e) {
      e.printStackTrace(out);
      assertTrue(false);
    }
  }
}
