import java.io.OutputStream;

/**
 * Interface for unifying the beam functionality.
 */
public interface Beam {

	/**
	 * Beams the image from the source to the output stream.
	 *
	 * @param outputStream to beam
	 */
	void beam(OutputStream outputStream);
}
