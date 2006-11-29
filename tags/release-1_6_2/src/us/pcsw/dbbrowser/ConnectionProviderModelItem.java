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
package us.pcsw.dbbrowser;

import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.ConnectionProviderModelItem
 * -
 * A list item / table row item which contains data on a ConnectionProvider
 * implimentation.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>07/23/2001 This class was created.</LI>
 * <LI>04/22/2003 Made this class public.  Changed name of method from
 *                getDBName and setDBName to getConnectionProviderName and
 *                setConnectionProviderName. Added class name checking into
 *                setClassName method. PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class ConnectionProviderModelItem
	extends Object
{
    /**
     * The name of the database to which the ConnectionProvider connects.
     */
    private String dbName;

    /**
     * The class name (including package) of the ConnectionProvider
     * implementation.
     */
    private String className;

    /**
     * Initializes the ConnectionProviderModelItem with the give data.
     * @param cpName The name of the ConnectionProvider.  May not be null.
     * @param className The class name (including package) of the
     *                   ConnectionProvider implementation.  May not be null.
     * @throws IllegalArgumentException The value of cpName is null, the value
     *                                   of className is null, or the indicated
     *                                   ConnectionProvider class could not be
     *                                   loaded. 
     */
    public ConnectionProviderModelItem(String cpName, String className)
    	throws IllegalArgumentException
    {
    	if (cpName == null) {
    		throw new IllegalArgumentException("Invalid Connection Provider name.");
    	} else if (className == null) {
			throw new IllegalArgumentException("Invalid Connection Provider classname.");
    	} else {
    		setConnectionProviderName(cpName);
    		setClassName(className);
	    }	
    }

    /**
     * Compares another ConnectionProviderModelItem to this instance.
     * @param o the ConnectionProviderModelItem to compare this instance to.
     * @return the value 0 if the argument's toString() method returns the same
     *         value as this instance's dbName; a value less than 0 if this
     *         instance's dbName is lexicographically less than the return
     *         value of the argument's toString() method; and a value greater
     *         than 0 if this instance's dbName is lexicographically greater
     *         than the toString() method of the argument.
     */
    public int compareTo(Object o)
	throws ClassCastException
    {
		return (getConnectionProviderName().compareTo(o.toString()));
    }

    /**
     * Gets the descriptive name of the connection provider. 
     */
    public String getConnectionProviderName()
    {
		return dbName;
    }

    /**
     * Gets the name (including package) of the ConnectionProvider
     * implementation.
     */
    public String getClassName()
    {
		return className;
    }

    /**
     * Compares another object to this instance.
     * @return true if compareTo(o) returns 0, false otherwise.
     * @see #compareTo
     */
    public boolean equals(Object o)
    {
		return (compareTo(o) == 0);
    }

    /**
     * Sets the name of the ConnectionProvider implementation.  Name should
     * include the package name.
     * @throws IllegalArgumentException The value of className is null, or the
     *                                   indicated ConnectionProvider class
     *                                   could not be loaded. 
     */
    public void setClassName(String className)
    {
		try {
			Object provider = Class.forName(className).newInstance();
			// If we went this far, the class was loaded.
			if (provider instanceof ConnectionProvider) {
				this.className = className;
			} else {
				throw new IllegalArgumentException
					(className + " is not a Connection Provider.");
			}
		} catch (Throwable t) {
			if (t instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)t;
			} else {
				throw new IllegalArgumentException
					("Unable to load the Connection Provider class " +
				 	 className);
			}
		}
    }
    
    /**
     * Sets the name of the Connection Provider.
     * @param dbName The name.
     */
    public void setConnectionProviderName(String cpName)
    {
		this.dbName = cpName;
    }

    /**
     * Returns a string representation of this object (the name of the
     * database to which the implementation connects).
     */
    public String toString()
    {
		return dbName;
    }
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone()
    	throws CloneNotSupportedException
    {
        return new ConnectionProviderModelItem(getConnectionProviderName(),
                                                getClassName());
    }

}
