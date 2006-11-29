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
package us.pcsw.dbbrowser.dataimport;

/**
 * com.alliancemanaged.simis.importer.ImportListener
 * -
 * An interface that can be extended by a class in order to monitor the status
 * of an import.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 7, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public interface ImportListener
{
	public void importStatusChanged(ImportEvent ie);
}
