package us.pcsw.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * us.pcsw.util.IntegerUtil
 * -
 * Utility class for dealing with integer native type values and Integer Objects.
 *
 * Revision History:
 * 02/11/2000 This class was created.
 *
 * @author Philip A. Chapman
 */
public class IntegerUtil
{
	/**
	 * This method retrieves a Integer value from the resultset at the given
	 * column.  If the column's value is null, null is returned, else a new
	 * Integer object containing the integer value is returned.
	 * @param result The result from which the value is to be retrieved.
	 * @param columnIndex The index of the column from which the value is to
	 *                    be retrieved.
	 * @return If the column's value is null, null; else a new Integer object
	 *         containing the integer value in the column.
	 * @throws SQLException Indicates an error retrieving the data from the
	 *         ResultSet.
	 */
	public static Integer getNullableInteger(ResultSet result, int columnIndex)
		throws SQLException
	{
		int i = result.getInt(columnIndex);
		if (result.wasNull()) {
			return null;
		} else {
			return new Integer(i);
		}
	}	

	/**
	 * This method retrieves a Integer value from the callable statement at
	 * the given parameter.  If the parameter's value is null, null is
	 * returned, else a new Integer object containing the integer value is
	 * returned.
	 * @param stmt The callable statement from which the value is to be
	 *             retrieved.
	 * @param paramIndex The index of the parameter from which the value is to
	 *                   be retrieved.
	 * @return If the parameter's value is null, null; else a new Integer
	 *         object containing the integer value in the parameter.
	 * @throws SQLException Indicates an error retrieving the data from the
	 *         CallableStatement.
	 */
	public static Integer getNullableInteger(CallableStatement stmt, int paramIndex)
		throws SQLException
	{
		int i = stmt.getInt(paramIndex);
		if (stmt.wasNull()) {
			return null;
		} else {
			return new Integer(i);
		}
	}
	
	/**
	 * This method uses java.lang.Integer.parseInteger(String) to parse the
	 * string value into a integer.  The difference is that if a
	 * NumberFormatException is caught, 0 is returned. 
	 * @param str The string to return.
	 * @return The integer value, or 0 if the string cannot be parsed into a
	 *         integer.
	 */
    public static int parseIntegerSafe(String str) {
	// Initialize
	int i = (int)0;

	// Try to parse the string
	try {
	    i = Integer.valueOf(str).intValue();
	} catch (NumberFormatException e) {}

	// Return the value.  Notice that if a Number Format Exception was 
	// caught, (integer)0 is returned.
	return i;
    }

    /**
     * Sets the parameter in the statement with an integer value, or null if
     * the integer is null.
     * @param stmt The statement in which the parameter value is to be set.
     * @param paramIndex The index of the parameter to set.
     * @param i The value to set (or null).
     * @throws SQLException Indicates an error setting the parameter.
     */
    public static void setNullableInteger(PreparedStatement stmt, int paramIndex, Integer i)
    	throws SQLException
    {
    	if (i == null) {
    		stmt.setNull(paramIndex, Types.INTEGER);
    	} else {
    		stmt.setInt(paramIndex, i.intValue());
    	}
    }
}
