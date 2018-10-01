package controller.managers;

import controller.controlfiles.ControlFile;
import controller.controlfiles.Photo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The PhotoManager class is responsible to manage all photo files.
 *
 * @author XIAWEI ZHANG
 */
public class PhotoManager extends FileManager {

  /**
   * The constructor of PhotoManager class.
   *
   * @param rootPath the working directory
   */
  public PhotoManager(String rootPath) {
    super(
        rootPath,
        "photo",
        new String[] {"jpg", "png", "bmp", "jpeg", "gif", "tiff", "swf", "svg", "psd"});
  }

  /**
   * The method to filter the Color in each pixel from each bufferedImage and change the color to
   * the color we wanted.
   *
   * @param photo Photo that we want to changed
   * @param color String the color that we want to change to
   * @return BufferedImage The image that reconstructed by the color we wanted
   */
  public BufferedImage filterColor(Photo photo, String color) {
    int width = photo.getWidth();
    int height = photo.getHeight();

    // convert to the color we wanted.
    for (int col = 0; col < height; col++) {
      for (int row = 0; row < width; row++) {
        switch (color) {
          case "green":
            greenHelper(photo, row, col);
            break;
          case "red":
            redHelper(photo, row, col);
            break;
          case "blue":
            blueHelper(photo, row, col);
            break;
        }
      }
    }

    return photo.getBufferedImage();
  }

  /**
   * The Helper method to help the filterColor method to filter the image into green color.
   *
   * @param photo Photo we selected
   * @param row the x-coordinate for the pixel in a image
   * @param col the y-coordinate for the pixel in a image
   */
  private void greenHelper(Photo photo, int row, int col) {
    // get the pixel from the bufferedImage
    int pixel = photo.getBufferedImage().getRGB(row, col);
    int alpha = (pixel >> 24) & 0xff;
    int green = (pixel >> 8) & 0xff;
    // set to green color for this image
    pixel = (alpha << 24) | (green << 8);
    photo.getBufferedImage().setRGB(row, col, pixel);
  }

  /**
   * The Helper method to help the filterColor method to filter the image into red color.
   *
   * @param photo Photo we selected
   * @param row the x-coordinate for the pixel in a image
   * @param col the y-coordinate for the pixel in a image
   */
  private void redHelper(Photo photo, int row, int col) {
    // get the pixel from the bufferedImage
    int pixel = photo.getBufferedImage().getRGB(row, col);
    int alpha = (pixel >> 24) & 0xff;
    int red = (pixel >> 16) & 0xff;
    // set to red color for this image
    pixel = (alpha << 24) | (red << 16);
    photo.getBufferedImage().setRGB(row, col, pixel);
  }

  /**
   * The Helper method to help the filterColor method to filter the image into blue color.
   *
   * @param photo Photo we selected
   * @param row the x-coordinate for the pixel in a image
   * @param col the y-coordinate for the pixel in a image
   */
  private void blueHelper(Photo photo, int row, int col) {
    // get the pixel from the bufferedImage
    int pixel = photo.getBufferedImage().getRGB(row, col);
    int alpha = (pixel >> 24) & 0xff;
    int blue = pixel & 0xff;
    // set to blue color for this image
    pixel = (alpha << 24) | blue;
    photo.getBufferedImage().setRGB(row, col, pixel);
  }

  /**
   * The method to save the filtered Photo to the same directory that the Photo has.
   *
   * @param photo Photo The Photo we have filtered
   * @param bufferedImage BufferedImage The filtered Photo we created
   */
  public void save(Photo photo, BufferedImage bufferedImage) {
    try {
      String[] imageFormat = photo.getFileName().split("\\.");
      File filteredPhoto =
          new File(photo.getLocation() + File.separator + "(filtered)" + photo.getFileName());
      ImageIO.write(bufferedImage, imageFormat[imageFormat.length - 1], filteredPhoto);
      ControlFile controlFile = new ControlFile(filteredPhoto.getName(), photo.getLocation(), "");
      loadPictures(controlFile);
    } catch (IOException ex) {
      System.out.println("File cannot be constructed");
    }
  }
}
