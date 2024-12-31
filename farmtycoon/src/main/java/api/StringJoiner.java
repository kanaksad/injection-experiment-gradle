package api;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

/**
 * Wrapper around StringBuilder to join string using the given glue.
 * @author Rigès De Witte, Simon Peeters,Barny Pieters,Laurens Van Damme
 */
public class StringJoiner {
	/**
	 * the glue to place in between the stings.
	 */
	private String glue;
	/**
	 * StringBuilder in which the string is constructed
	 */
	@RUntainted StringBuilder builder;

	/**
	 * Create a new StringJoiner using the given glue and initialize it.
	 * @param glue the glue to use
	 * @param coll the elements to initially add.
	 */
	public StringJoiner(String glue, @RUntainted Object...coll){
		this(glue);
		for(Object o : coll)
			add(o);
	}
	
	/**
	 * Create a empty StringJoiner using the given glue.
	 * @param glue
	 */
	public StringJoiner(String glue){
		super();
		this.glue = glue;
	}
	
	/**
	 * Add an element to the string.
	 * The elements toString() method will be used.
	 * @param o element to add.
	 */
	public void add(@RUntainted Object o) {
		if (builder == null)
			builder = new StringBuilder(o.toString());
		else
			builder.append(glue).append(o.toString());
	}
	
	public @RUntainted String toString(){
		return builder.toString();
	}
}
