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
package us.pcsw.dbbrowser.bsh;

import java.util.LinkedList;
import java.util.List;

/**
 * A token object that can be used by the DBBrowser beanshell command objects
 * differentiate between different sessions.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 17, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public final class BeanShellSession
{
	// CONSTRUCTORS
	
	public BeanShellSession()
	{
		super();
		id = IDFactory.getNewID();
		resultList = new LinkedList();
	}

	// MEMBERS
	
	private long id;
	public long getId()
	{
		return id;
	}
	
	private List resultList;
	public List getResultList()
	{
		return resultList;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof BeanShellSession) {
			return ((BeanShellSession)obj).getId() == id;
		} else {
			return false;
		}
	}
	
	/*&
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return Long.valueOf(id).hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "BeanShellSession: " + String.valueOf(id);
	}
	
	// METHODS
		
}
