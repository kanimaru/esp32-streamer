import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;

/**
 * Converts image to RGB565 format.
 *
 * See also: https://stackoverflow.com/questions/8319770/java-image-conversion-to-rgb565
 */
@RequiredArgsConstructor
class ImageRGB565Converter implements ImageConverter {

	final int width;
	final int height;

	@Override
	public byte[] convert(BufferedImage bufferedImage) {
		int numByte = 0;
		byte[] convertedImage = new byte[width * height * 2];

		int x;
		int y;

		for(y = 0; y < height; y++) {
			for(x = 0; x < width; x++) {
				int pixel = bufferedImage.getRGB(x, y);

				//RGB888
				int red = (pixel >> 16) & 0x0FF;
				int green = (pixel >> 8) & 0x0FF;
				int blue = (pixel) & 0x0FF;

				//RGB565
				red = red >> 3;
				green = green >> 2;
				blue = blue >> 3;

				short pixelToSend;
				int pixelToSendInt;
				pixelToSendInt = (red << 11) | (green << 5) | (blue);
				pixelToSend = (short) pixelToSendInt;


				//dividing into bytes
				byte byteH = (byte) ((pixelToSend >> 8) & 0x0FF);
				byte byteL = (byte) (pixelToSend & 0x0FF);

				//Writing it to array - High-byte is second
				convertedImage[numByte] = byteL;
				convertedImage[numByte + 1] = byteH;

				numByte += 2;
			}
		}

		return convertedImage;
	}

	@Override
	public BufferedImage convert(byte[] imageData) {
		var x = 0;
		var y = 0;
		var numByte = 0;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
		for(y = 0; y < height; y++) {
			for(x = 0; x < width; x++) {
				int curPixel;
				int alpha = 0x0FF;
				int red;
				int green;
				int blue;

				byte byteL;
				byte byteH;

				byteH = imageData[numByte];
				byteL = imageData[numByte + 1];

				curPixel = byteH & 0xFF; // Convert byte to int to be within [0 , 255]
				curPixel = (byteL << 8) | curPixel; // Apply OR bit-operator

				//RGB565
				red = (curPixel >> (6 + 5)) & 0x01F;
				green = (curPixel >> 5) & 0x03F;
				blue = (curPixel) & 0x01F;

				//RGB888
				red = red << 3;
				green = green << 2;
				blue = blue << 3;

				//aRGB
				curPixel = (alpha << 24) | (red << 16) | (green << 8) | (blue);

				img.setRGB(x, y, curPixel);
				numByte += 2;

			}
		}
		return img;
	}
}
