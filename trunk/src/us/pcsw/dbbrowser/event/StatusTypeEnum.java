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
package us.pcsw.dbbrowser.event;

import us.pcsw.util.BaseEnum;

/**
 * us.pcsw.dbbrowser.enclosing_type
 * -
 * Description Text
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 15, 2003 This class was created.</LI>
 * <LI>Feb 16, 2003 Added the ERROR status type.</LI>
 * <LI>Mar 25, 2003 Added the DATA_AVAIL, DATA_NOT_AVAIL, QUERY_ENDED, and
 *                  QUERY_STARTED status types. PAC </LI>
 * </UL></P>
 */
public class StatusTypeEnum
	extends BaseEnum
{
	/**
	 * Indicates that some long-running work is in process.
	 */
	public static StatusTypeEnum BUSY = new StatusTypeEnum(0);
	
	/**
	 * Indicates that a connection to a database has been made.
	 */
	public static StatusTypeEnum CONNECTED = new StatusTypeEnum(1);

	/**
	 * Indicats that data is available.  What data depends on the Status
	 * Event's source.
	 */
	public static StatusTypeEnum DATA_AVAIL = new StatusTypeEnum(2);
	
	/**
	 * Indicats that data is not available.  What data depends on the Status
	 * Event's source.
	 */
	public static StatusTypeEnum DATA_NOT_AVAIL = new StatusTypeEnum(3);
	
	/**
	 * Indicates that a connection to a database has been disconnected.
	 */
	public static StatusTypeEnum DISCONNECTED = new StatusTypeEnum(4);
	
	/**
	 * Indicates that an error occurred during some long-running process.
	 */
	public static StatusTypeEnum ERROR = new StatusTypeEnum(5);

	/**
	 * Indicates that some long-running work has been completed.
	 */
	public static StatusTypeEnum NOT_BUSY = new StatusTypeEnum(6);
	
	/**
	 * Indicates that a response has been recieved from the server for a
	 * submitted query.  The response may be an error condition, not
	 * necessarily results. 
	 */
	public static StatusTypeEnum QUERY_ENDED = new StatusTypeEnum(7);
	
	/**
	 * Indicates that a query has been submitted to the server.
	 */
	public static StatusTypeEnum QUERY_STARTED = new StatusTypeEnum(8);
	
	/**
	 * Indicates that some amount of time has elapsed.  The data item will
	 * be a Long object indicating the number of miliseconds elapsed.
	 */
	public static StatusTypeEnum TIME_ELAPSED = new StatusTypeEnum(9);
	
	/**
	 * Descriptions for the enumeration values.
	 */
	private static String[] desc = {"Busy", "Connected", "Data Available",
	                                  "Data Not Available", "Disconnected",
	                                  "Error", "Not Busy", "Query Ended",
	                                  "Query Started", "Time Elapsed"};

	/**
	 * Constructor for StatusTypeEnum.
	 * @param value The value represented by this enumeration.
	 */
	private StatusTypeEnum(int value)
	{
		super(value);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		// Because of the private constructor, I can be sure there will not be
		// an array out of bounds error here.
		return desc[getValue()];
	}	
}
