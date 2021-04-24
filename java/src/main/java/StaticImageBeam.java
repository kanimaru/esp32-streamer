import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Beams a static image.
 */
class StaticImageBeam implements Beam {

	final BufferedImage bufferedImage;
	final byte[] convertedImage;

	public StaticImageBeam(String filename, int targetWidth, int targetHeight, ImageConverter imageConverter) throws IOException {
		var resource = StaticImageBeam.class.getClassLoader().getResourceAsStream(filename);
		assert resource != null;
		bufferedImage = ImageIO.read(resource);
		var thumbnailImage = Thumbnails.of(bufferedImage)
				.size(targetWidth, targetHeight)
				.asBufferedImage();
		convertedImage = imageConverter.convert(thumbnailImage);
	}

	@Override
	public void beam(OutputStream outputStream) {
		try {
			outputStream.write(convertedImage);
		} catch(IOException e) {
			System.err.println("Can't write the static image.");
			e.printStackTrace();
		}
	}
}
