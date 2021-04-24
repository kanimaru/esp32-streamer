import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Beams a GIF file.
 *
 * See: https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
 */
class GifBeam implements Beam {

	final String[] imageAttributes = new String[] {
			"imageLeftPosition",
			"imageTopPosition",
			"imageWidth",
			"imageHeight"
	};

	final ImageInputStream imageInputStream;
	final ImageReader reader;
	final int numberOfImage;
	final int targetWidth;
	final int targetHeight;
	final ImageConverter imageConverter;

	public GifBeam(String filename, int targetWidth, int targetHeight, ImageConverter imageConverter) throws IOException {
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.imageConverter = imageConverter;
		reader = ImageIO.getImageReadersByFormatName("gif").next();
		var resourceAsStream = Main.class.getClassLoader().getResourceAsStream(filename);
		assert resourceAsStream != null;
		imageInputStream = ImageIO.createImageInputStream(resourceAsStream);
		reader.setInput(imageInputStream, false);
		numberOfImage = reader.getNumImages(true);
	}

	@Override
	public void beam(OutputStream outputStream) {
		try {
			BufferedImage master = null;

			for (int imageIndex = 0; imageIndex < numberOfImage; imageIndex++) {
				var image = reader.read(imageIndex);
				var metadata = reader.getImageMetadata(imageIndex);
				var tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
				var children = tree.getChildNodes();

				for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
					var nodeItem = children.item(childIndex);

					if(nodeItem.getNodeName().equals("ImageDescriptor")){
						var imageAttributes = new HashMap<String, Integer>();

						for(String imageAttribute : this.imageAttributes) {
							var attributeMap = nodeItem.getAttributes();
							var attributeNode = attributeMap.getNamedItem(imageAttribute);
							imageAttributes.put(imageAttribute, Integer.valueOf(attributeNode.getNodeValue()));
						}

						if(imageIndex == 0){
							master = new BufferedImage(
									imageAttributes.get("imageWidth"),
									imageAttributes.get("imageHeight"),
									BufferedImage.TYPE_INT_ARGB
							);
						}

						if(master != null) {
							var imageLeftPosition = imageAttributes.get("imageLeftPosition");
							var imageTopPosition = imageAttributes.get("imageTopPosition");
							var graphics = master.getGraphics();
							graphics.drawImage(image, imageLeftPosition, imageTopPosition, null);
						}
					}
				}

				var thumbnailImage = Thumbnails.of(master)
						.size(targetWidth, targetHeight)
						.keepAspectRatio(false)
						.asBufferedImage();
				var convert = imageConverter.convert(thumbnailImage);
				outputStream.write(convert);
				outputStream.flush();
			}
		} catch (IOException e) {
			System.err.println("Can't write the GIF file.");
			e.printStackTrace();
		}
	}
}
