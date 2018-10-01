package controller.managers;

/**
 * The VideoManager class is responsible to manage all video files.
 *
 * @author XIAWEI ZHANG
 * @author Jiyuan Cheng
 */
public class VideoManager extends FileManager {
  /**
   * The constructor of FileManager class @param rootPath String the WorkingDirectory.
   *
   * @param rootPath the workingDirectory
   */
  public VideoManager(String rootPath) {
    super(rootPath, "video", new String[] {"flc", "mp4"});
  }
}
