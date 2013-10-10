package tilt.tools;

/**
 * Some useful math functions
 * 
 * @author Sam Luc
 * @group Joseph Guinto, Jan Martin
 */
public class Math2 {

	private final static char base64Array[] = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };

	/**
	 * Distance between two points
	 * 
	 * @param x1
	 *            xCoordinate of first point
	 * @param y1
	 *            yCoordinate of first point
	 * @param x2
	 *            xCoordinate of second point
	 * @param y2
	 *            yCoordinate of second point
	 * @return distance between the two points
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Angle between two points
	 * 
	 * @param x1
	 *            xCoordinate of first point
	 * @param y1
	 *            yCoordinate of first point
	 * @param x2
	 *            xCoordinate of second point
	 * @param y2
	 *            yCoordinate of second point
	 * @return angle between the two points in radians
	 */
	public static double angle(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		// Why does atan2 takes the inverse of deltaX/Y and deltaY first?
		return Math.atan2(-dy, -dx);
	}

	/**
	 * Limits a given value between two endpoints
	 * 
	 * @param value
	 *            the value to limit
	 * @param minimum
	 *            the minimum the value should take
	 * @param maximum
	 *            the maximum the value should take
	 * @return minimum if value is less than minimum, maximum if value is
	 *         greater than maximum, value in any other case
	 */
	public static float limit(float value, float minimum, float maximum) {
		if (value > maximum) {
			return maximum;
		} else if (value < minimum) {
			return minimum;
		} else {
			return value;
		}
	}

	/**
	 * Returns a string formatted as M+:SS:ss
	 * 
	 * @param seconds
	 * @return formatted time
	 */
	public static String formatMilliseconds(long milliseconds) {
		StringBuilder time = new StringBuilder();

		time.append(milliseconds / 600000);
		time.append((milliseconds %= 600000) / 60000);
		time.append(':');
		time.append((milliseconds %= 60000) / 10000);
		time.append((milliseconds %= 10000) / 1000);
		time.append(':');
		time.append((milliseconds %= 1000) / 100);
		time.append((milliseconds %= 100) / 10);

		return time.toString();
	}

	/**
	 * Base64 encodes a given string
	 * 
	 * @param string
	 * @return base64 encoded string
	 */
	public static String base64Encode(String string) {
		String encodedString = "";
		byte bytes[] = string.getBytes();
		int i = 0;
		int pad = 0;
		while (i < bytes.length) {
			byte b1 = bytes[i++];
			byte b2;
			byte b3;
			if (i >= bytes.length) {
				b2 = 0;
				b3 = 0;
				pad = 2;
			} else {
				b2 = bytes[i++];
				if (i >= bytes.length) {
					b3 = 0;
					pad = 1;
				} else
					b3 = bytes[i++];
			}
			byte c1 = (byte) (b1 >> 2);
			byte c2 = (byte) (((b1 & 0x3) << 4) | (b2 >> 4));
			byte c3 = (byte) (((b2 & 0xf) << 2) | (b3 >> 6));
			byte c4 = (byte) (b3 & 0x3f);
			encodedString += base64Array[c1];
			encodedString += base64Array[c2];
			switch (pad) {
			case 0:
				encodedString += base64Array[c3];
				encodedString += base64Array[c4];
				break;
			case 1:
				encodedString += base64Array[c3];
				encodedString += "=";
				break;
			case 2:
				encodedString += "==";
				break;
			}
		}
		return encodedString;
	}

}
