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
 * @author pchapman
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StringConnectionParameter
	extends ConnectionParameter
{
	/**
	 * Maximum length for the string.  0 indicates that there is no maximum
	 * and is the default.
	 */
	protected int maxLength = 0;
	
	/**
	 * Mimimum length for the string.  0 is the default.
	 */
	protected int minLength = 0;

	/**
	 * Indicates that the value should be protected (such as a password) if
	 * true.  False is the default.
	 */
	protected boolean secret = false;

	/**
	 * Constructs a new StringConnectionparameter with default requirements.
	 * @param name The name of the parameter.
	 * @param required True if the parameter is required (must be non-null).
	 * @param value The default value of the parameter.
	 */
	public StringConnectionParameter
		(String name, boolean required, String value)
	{
		super(name, required, (Object)value);
	}

	/**
	 * Constructor for BooleanConnectionParameter.
	 * @param name The name of the parameter.
	 * @param required True if the parameter is required (must be non-null).
	 * @param value The default value of the parameter.
	 * @param minLength The minimum length for the string.  Negative values
	 *                   are rounded to 0.
	 * @param maxLength The maximum length for the string, 0 indicates no
	 *                  maximum length requirement.  Negative values are
	 *                  rounded to 0.
	 * @param secret Indicates that the value should be protected (such as
	 *                a password).
	 */
	public StringConnectionParameter
		(String name, boolean required, String value, int minLength,
		 int maxLength, boolean secret)
	{
		super(name, required, (Object)value);
		this.maxLength = maxLength > 0 ? maxLength : 0;
		this.minLength = minLength > 0 ? minLength : 0;
		this.secret = secret;
	}

	public int getMaxLength()
	{
		return maxLength;
	}
	
	public int getMinLength()
	{
		return minLength;
	}

	public boolean isSecret()
	{
		return secret;
	}

	/**
	 * Sets the value of the parameter.
	 * @param value The value to set this parameter to.  If value is not of
	 *               type String, values's toString method is called.
	 * @exception IllegalArgumentException Indicates that the value is too
	 *                                     long, is too short, or is null and
	 *                                     the parameter is required.
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
		} else {
			int minLength = getMinLength();
			int maxLength = getMaxLength();
			String s = value.toString();
			if (s.length() < minLength) {
				throw new IllegalArgumentException
					("The parameter " + getName() +
					 " has a minimum length requirement of " + minLength +
					 ".");
			} else if (maxLength > 0 && s.length() > maxLength) {
				throw new IllegalArgumentException
					("The parameter " + getName() +
					 " has a maximum length requirement of " + maxLength +
					 ".");
			} else {
				super.setValue(s);
			}
		}
	}
}
