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
 * us.pcsw.dbbrowser.IntegerConnectionParameter
 * -
 * Represents an integer connection parameter.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Oct 16, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Oct 29, 2002 Fixed problem with setValue() when sending in an Integer
 *                  object.</LI>
 * </UL></P>
 */
public class IntegerConnectionParameter
	extends NumberConnectionParameter
{
	private static final long serialVersionUID = 1L;

	/**
	 * Maximum length for the string.  0 indicates that there is no maximum
	 * and is the default.
	 */
	protected int max = Integer.MAX_VALUE;
	
	/**
	 * Mimimum length for the string.  0 is the default.
	 */
	protected int min = Integer.MIN_VALUE;

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
	public IntegerConnectionParameter
		(String name, boolean required, Integer value)
	{
		super(name, required, (Number)value);
	}

	/**
	 * Constructor for BooleanConnectionParameter.
	 * @param name The name of the parameter.
	 * @param required True if the parameter is required (must be non-null).
	 * @param value The default value of the parameter.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 */
	public IntegerConnectionParameter
		(String name, boolean required,Integer value, int min, int max)
	{
		super(name, required, (Number)value);
		this.max = max;
		this.min = min;
		this.secret = false;
	}

	public int getMax()
	{
		return max;
	}
	
	public int getMin()
	{
		return min;
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
			try {
				int min = getMin();
				int max = getMax();
				int i = 0;
				if (value instanceof Integer) {
					i = ((Integer)value).intValue();
				} else {
					i = Integer.parseInt(value.toString());
				}
				if (i < min) {
					throw new IllegalArgumentException
						("The parameter " + getName() +
						 " has a minimum value requirement of " + min +
						 ".");
				} else if (i > max) {
					throw new IllegalArgumentException
						("The parameter " + getName() +
						 " has a maximum value requirement of " + max +
						 ".");
				} else {
					super.setValue(new Integer(i));
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException
					("The parameter " + getName() + " must be an Integer.");
			}
		}
	}
}
