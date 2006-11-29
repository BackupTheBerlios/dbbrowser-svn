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
 * Class which holds connection information of the boolean type.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>10/09/2003 This class was created.</LI>
 * <LI>10/30/2003 Fixed the setValue(Object) method to accept objects which
 *                are not of type Boolean and attempt to interpret them.
 *                PAC </LI>
 * </P></UL>
 */
public class BooleanConnectionParameter
	extends ConnectionParameter
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for BooleanConnectionParameter.
	 * @param name The name of the parameter.
	 * @param required True if the parameter is required (must be non-null).
	 * @param value The default value of the parameter.
	 */
	public BooleanConnectionParameter
		(String name, boolean required, Boolean value)
	{
		super(name, required, (Object)value);
	}

	/**
	 * Constructor for BooleanConnectionParameter.
	 * @param name The name of the parameter.
	 * @param required True if the parameter is required (must be non-null).
	 * @param value The default value of the parameter.
	 */
	public BooleanConnectionParameter
		(String name, boolean required, boolean value)
	{
		super(name, required, new Boolean(value));
	}

	/**
	 * Shortcut method to set the value of the parameter.
	 * @param value The value to set this parameter to.
	 * @exception IllegalArgumentException Indicates that the value is not
	 *                                     a boolean value, or is null and the
	 *                                     parameter is required.
	 */
	public void setValue(boolean value)
	{
		// the native type boolean is guaranteed to be a valid argument since
		// it is non-null and of the correct type.  Simply wrap it in the
		// java.lang.Boolean object.
		try {
			setValue(new Boolean(value));
		} catch (IllegalArgumentException iae) {}
	}

	/**
	 * Sets the value of the parameter.
	 * @param value The value to set this parameter to.
	 * @exception IllegalArgumentException Indicates that the value is not
	 *                                     a boolean value, or is null and the
	 *                                     parameter is required.
	 */
	public void setValue(Object value)
		throws IllegalArgumentException
	{
		if (value == null) {
			if (isRequired()) {
				throw new IllegalArgumentException
					("The parameter " + getName() + " Is required.");
			} else {
				super.setValue(null);
			}
		}
		if (value instanceof java.lang.Boolean) {
			super.setValue(value);
		} else {
			super.setValue(Boolean.valueOf(value.toString()));
		}
	}
}
