package us.pcsw.util;

/**
 * us.pcsw.util.Message
 * -
 * A class that can be used to build a message string.  It is much like
 * StringBuffer except that when content is appended and there is already
 * text in the message, a delimeter will be appended.  Usually, it's a series
 * of spaces or a newline character.  If delimeter is null, this class will
 * act exactly like StringBuffer.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 8, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class Message
{
	private StringBuffer buffer;
	private String delimeter;

	// CONSTRUCTORS
	
	/**
	 * Creates a new instance with a delimeter of '\n'.
	 */
	public Message()
	{
		this("\n");
	}
	
	/**
	 * Creates a new instance with the given delimeter.
	 */
	public Message(char delimeter)
	{
		this(new String(""+delimeter)); // I cheat!
	}
	
	/**
	 * Creates a new instance with the given delimeter.
	 */
	public Message(String delimeter)
	{
		this.buffer = new StringBuffer();
		this.delimeter = delimeter;
	}
	
	// MEMBERS
	
	/**
	 * Returns the delimeter that will be appended by appendln calls.
	 */
	public String getDelimeter()
	{
		return delimeter;
	}
	
	/**
	 * Returns the length of the message.
	 */
	public int getLength()
	{
		return buffer.length();
	}
	
	// METHODS

	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(boolean value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(char value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(char[] value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(char[] value, int offset, int len)
	{
		buffer.append(value, offset, len);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(double value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(float value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(int value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(long value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(Object value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(String value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends a string representation of the value to the end of the message.
	 */
	public void append(StringBuffer value)
	{
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(boolean value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(char value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(char[] value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(char[] value, int offset, int len)
	{
		preappend();
		buffer.append(value, offset, len);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(double value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(float value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(int value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(long value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(Object value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(String value)
	{
		preappend();
		buffer.append(value);
	}
	
	/**
	 * Appends the delimeter to the end of the message, then a string
	 * representation of the value to the end of the message.  the delimeter
	 * is not appended if there is no text in the message before this call.
	 */
	public void appendnl(StringBuffer value)
	{
		preappend();
		buffer.append(value);
	}
	
	public void clear()
	{
		buffer.setLength(0);
	}
	
	/**
	 * Determines whether to append the delimeter.  If the delimeter is
	 * required, it is appended.  This method is called by all the
	 * appenddnl(x) methods before they append their values.
	 */
	private void preappend()
	{
		if (delimeter != null && buffer.length() > 0) {
			buffer.append(delimeter);
		}
	}
	
	/**
	 * Returns the message text.
	 */
	public String toString()
	{
		return buffer.toString();
	}
}
