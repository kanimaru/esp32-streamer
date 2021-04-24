import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * Takes screenshot and beam it.
 *
 * See: https://stackoverflow.com/questions/4490454/how-to-take-a-screenshot-in-java
 */
class ScreenBeam implements Beam {

	final int targetWidth;
	final int targetHeight;
	final ImageConverter imageConverter;
	final Rectangle rectangle;

	public ScreenBeam(Rectangle recordScreen, int targetWidth, int targetHeight, ImageConverter imageConverter) {
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.imageConverter = imageConverter;
		this.rectangle = recordScreen;
	}

	@Override
	public void beam(OutputStream outputStream) {
		try {
			BufferedImage image = new Robot().createScreenCapture(rectangle);

			var thumbnailImage = Thumbnails.of(image)
					.size(targetWidth, targetHeight)
					.asBufferedImage();
			var convert = imageConverter.convert(thumbnailImage);

			outputStream.write(convert);
			outputStream.flush();
		} catch(Exception e) {
			System.err.println("Can't take screenshot.");
			e.printStackTrace();
		}
	}
}
