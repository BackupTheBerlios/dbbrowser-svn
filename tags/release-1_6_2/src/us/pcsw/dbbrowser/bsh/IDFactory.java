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

/**
 * A factory class that will provide unique IDs.  The IDs are only unique for
 * this instance of the VM.
 * 
 * @author pchapman
 */
public class IDFactory
{
	/**
	 * No instances allowed.
	 */
	private IDFactory()
	{
		super();
	}

	private static long lastID = 0;
	private static Object lock = new Object();
	
	public static long getLastID()
	{
		synchronized (lock) {
			return lastID;
		}
	}
	
	public static long getNewID()
	{
		synchronized(lock) {
			return ++lastID;
		}
	}
}
