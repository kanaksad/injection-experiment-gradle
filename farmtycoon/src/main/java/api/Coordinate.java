package api;

import java.util.ArrayList;
import java.util.Collection;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

/**
 * Type for defining Coordinates.
 *
 * @author Rigès De Witte
 * @author Simon Peeters
 * @author Barny Pieters
 * @author Laurens Van Damme
 * 
 */

public class Coordinate {
	private final @RUntainted short x;
	private final @RUntainted short y;

	/**
	 * Create a new coordinate object with coordinates x and y
	 */
	public Coordinate(@RUntainted int x, @RUntainted int y) {
		this.x=(short) x;
		this.y=(short) y; }

	/**
	 * Get the X value for this coordinate object.
	 */
	public @RUntainted short getX() { return x; }
	/**
	 * Get the y value for this coordinate object.
	 */
	public @RUntainted short getY() { return y; }

	@Override
	public @RUntainted int hashCode() {
		return ( getY() << 16 ) | ( getX() & 0xFFFF );
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof Coordinate))
			return false; 
		return obj.hashCode() == hashCode();
	}

	/**
	 * Reconstruct a coordinate from a given hash
	 */
	public static Coordinate forHash(@RUntainted int hash) {
		short x = (short) (hash >> 16);
		short y = (short) (hash & 0xFFFF);
		return new Coordinate(x,y);
	}
	/**
	 * Get a collection containing Coordinates from 'from' to 'to' in a rectangle
	 */
	public static Collection<Coordinate> getCoordSet(Coordinate from, Coordinate to) {
		ArrayList<Coordinate> ret = new ArrayList<Coordinate>();
		for (short i = from.getX(); i < to.getX(); i++)
			for (short j = from.getY(); j < to.getY(); j++)
				ret.add(new Coordinate(i, j));
		return ret;
	}
	public String toString() {
		return String.format("X:%d;Y:%d", x,y);
	}
}
