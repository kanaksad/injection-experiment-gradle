package api;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

/**
 * Class containing information about the state of a specific tile.
 * @author Rigès De Witte, Simon Peeters,Barny Pieters,Laurens Van Damme
 *
 */
public class TileInfo {
	private final @RUntainted String field;
	private final @RUntainted String subtype;
	private final @RUntainted String state;
	private final long expiryTime;

	public TileInfo(@RUntainted String field, @RUntainted String subtype, @RUntainted String state) {
		this(field,subtype,state,0);
	}
	public TileInfo(@RUntainted String field, @RUntainted String subtype, @RUntainted String state, long expirytime) {
		this.field = field;
		this.subtype = subtype;
		this.state = state;
		this.expiryTime = expirytime;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	public long getExpiryTime() {
		return expiryTime;
	}
	public String toString() {
		StringJoiner str = new StringJoiner("_",this.field);
		if(this.subtype!=null && this.subtype.length()>0)			
			str.add(this.subtype);
		if(this.state!=null && this.state.length()>0)			
			str.add(this.state);
		return str.toString();
	}
}
