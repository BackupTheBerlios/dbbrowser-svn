package us.pcsw.util;

/**
 * us.pcsw.util.BaseEnum
 * -
 * Base class for enumerations.  This class is heavily invfluenced by the
 * java-gnome project's enumerations.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>11/09/2002 The class was created.</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public class BaseEnum
{
    /**
     * holder for the raw enumeration value
     */
    protected int value;

    /**
     * This class is only instantiable via subclasses.
     */
    protected BaseEnum(int value)
    {
		this.value = value;
    }

    /**
     * Get the raw value of the object.
     *
     * @return the raw value.
     */
    public final int getValue()
    {
		return value;
    }

    /**
     * Get the hash code for this instance. It is the same as its value.
     *
     * @return the hash code
     */
    public final int hashCode()
    {
		return value;
    }

    /**
     * Compare this to another object. The comparison is only
     * <code>true</code> when the other object is also a <code>BaseEnum</code>
     * and when the values match.
     *
     * @param other the object to compare to
     * @return the result of comparison
     */
    public final boolean equals(Object other)
    {
    	if (other == null) {
    		return false;
    	} else if (other instanceof BaseEnum) {
	    	return (value == ((BaseEnum)other).value);
		} else {
			return false;
		}
    }
}
