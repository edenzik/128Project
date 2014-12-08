package com.wrangler.load;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Representing data types for a PostgreSQL database (i.e. what would go into a create table statement).
 * 
 * @author kahliloppenheimer
 *
 */
public class PostgresAttType {

	// Ugliest but only way to initialize final set with initial values
	private static final Set<String> POSSIBLE_TYPES = new HashSet<String>(Arrays.asList(new String[] {"text", "numeric"}));
	// The type, i.e. 'NUMERIC', 'VARCHAR(X)', 'DATE', etc.
	private final String type;
	
	// Only ever release one instance for all types where possible 
	private static final PostgresAttType NUMERIC = new PostgresAttType("NUMERIC");
	private static final PostgresAttType TEXT = new PostgresAttType("TEXT");
	
	private PostgresAttType(String type) {
		if(!isValidType(type)) {
			throw new IllegalArgumentException(type + " is not a recognized type!");
		}
		this.type = type;
	}
	
	/**
	 * Returns true iff the passed string is a valid type for a postgreSQL db
	 */
	private static boolean isValidType(String type) {
		type = type.toLowerCase().trim();
		return POSSIBLE_TYPES.contains(type);
	}

	/**
	 * Returns a numeric PostgressAttType
	 * @return
	 */
	public static PostgresAttType newNumeric() {
		return PostgresAttType.NUMERIC;
	}
	
	/**
	 * Returns a varchar PostgresAttType with the specified length
	 * 
	 * @param size
	 * @return
	 */
	public static PostgresAttType newText() {
		return PostgresAttType.TEXT;
	}

	/**
	 * Does basic pattern matching to infer the data type of the given value.
	 * 
	 * @param string
	 * @param maxLength 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static PostgresAttType valueOf(String string) {
		// First try to read it as BigDecimal
		try {
			BigDecimal asDecimal = new BigDecimal(string);
			return newNumeric();
		} catch(NumberFormatException e) {
			// If that didn't work, just return varchar
			return newText();
		}
	}

	/**
	 * Returns true iff the PostgresAttType is a character type
	 * like varchar
	 * 
	 * @return
	 */
	public boolean isCharType() {
		return type.toLowerCase().matches("text");
	}
	
	/**
	 * Returns true iff this type is a numeric data type
	 * 
	 * @return
	 */
	public boolean isNumeric() {
		return type.toLowerCase().matches("numeric");
	}
	
	/**
	 * Returns a String representation ready for SQL create table statements
	 * 
	 */
	@Override
	public String toString() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PostgresAttType))
			return false;
		PostgresAttType other = (PostgresAttType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/**
	 * USED ONLY FOR UNIT TESTING
	 * @param args
	 */
	public static void main(String[] args) {
		PostgresAttType numeric1 = newNumeric();
		PostgresAttType numeric2 = newNumeric();
		System.out.printf("numeric1 = %s\nnumeric2 = %s\n", numeric1, numeric2);
		
		System.out.println("numeric1.equals(numeric2):\t" + numeric1.equals(numeric2));
		System.out.println("numeric1 == numeric2:\t" + (numeric1 == numeric2));
		
		PostgresAttType text = newText();
		System.out.println("Text = " + text);
	}

}
