/*
 * DBBrowser is software for browsing the structure and contents of databases.
 * Copyright (C) 2001 Philip A. Chapman
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *                     Free Software Foundation, Inc.
 *                    51 Franklin Street, Fifth Floor
 *                      Boston, MA  02110-1301, USA.
 */
package us.pcsw.dbbrowser.cp;

/**
 * @author Philip A. Chapman
 *
 * us.pcsw.dbbrowser.ConnectionParameter
 * -
 * Class which holds connection information.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>10/09/2002 This class was created.</LI>
 * </P></UL>
 */
public abstract class ConnectionParameter
	implements java.io.Serializable
{
	/**
	 * The name of the paramter.
	 */
	protected String name = null;
	
	/**
	 * If true, indicates that a value is required (value is non-null).
	 */
	protected boolean required = false;
	
	/**
	 * The value of the parameter.
	 */
	protected Object value = null;

	/**
	 * Constructs a basic connection parameter.
	 */	
	protected ConnectionParameter
		(String name, boolean required, Object value)
	{
		this.name = name;
		this.required = required;
		this.value = value;
	}
	
	/**
	 * Constructs a basic connection parameter with a null initial value.  The
	 * parameter may still be required (required == true), which indicates that
	 * there is no default value, and the user must supply a non-null value.
	 */
	protected ConnectionParameter
		(String name, boolean required)
	{
		this(name, required, null);
	}
	
	/**
	 * Gets the name of the paramter.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the value of the parameter.
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 * If true, indicates that a value is required (value is non-null).
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Validate the value of the parameter.
	 * @return true if the value of the parameter is valid, false
	 *         otherwise.	
	 */
	public boolean isValid()
	{
		try {
			setValue(getValue());
		} catch (IllegalArgumentException iae) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sets the value of the parameter.  This method should be overridden by
	 * subclasses to validate the data type and possibly other characteristics
	 * of value.
	 * @param value The value to set this parameter to.
	 * @exception IllegalArgumentException Indicates that the value is not
	 *                                     valid for the parameter.
	 */
	public void setValue(Object value)
		throws IllegalArgumentException
	{
		this.value = value;
	}
	
	/**
	 * Returns a string representation of the object.
	 */
	public String toString()
	{
		return getName() + ": " + getValue().toString();
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof ConnectionParameter) {
			return
				name.equals(((ConnectionParameter)obj).getName()) &&
				value.equals(((ConnectionParameter)obj).getValue());
		} else {
			return false;
		}
	}

}
