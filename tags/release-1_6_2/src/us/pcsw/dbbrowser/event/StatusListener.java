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

/**
 * us.pcsw.dbbrowser.StatusListener
 * -
 * Abstract used to define listeners of the StatusEvent.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 15, 2003 This class was created.</LI>
 * </UL></P>
 */
public interface StatusListener
{
	/**
	 * Method called when the status is changed.
	 * @param StatusEvent The status event describing the change in status.
	 */
	public abstract void statusChanged(StatusEvent se);
}
