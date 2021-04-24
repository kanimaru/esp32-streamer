import java.awt.image.BufferedImage;

/**
 * Converts image to byte[] and vice versa.
 */
public interface ImageConverter {

	byte[] convert(BufferedImage bufferedImage);

	BufferedImage convert(byte[] imageData);
}
