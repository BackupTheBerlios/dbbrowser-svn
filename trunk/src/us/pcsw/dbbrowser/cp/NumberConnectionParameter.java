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
 * us.pcsw.dbbrowser.NumberConnectionParameter
 * -
 * A connection parameter which is numeric.  This is an abstract super for
 * More concrete classes, such as IntegerConnectionParameter.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Oct 16, 2002	This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public abstract class NumberConnectionParameter
	extends ConnectionParameter
{
	/**
	 * Constructs a basic number connection parameter.
	 */	
	protected NumberConnectionParameter
		(String name, boolean required, Number value)
	{
		super(name, required, (Object)value);
	}
}
