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
package us.pcsw.dbbrowser.swing;

import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

/**
 * us.pcsw.dbbrowser.swing.DBBrowserAppListener
 * -
 * A listener that listens for Apple application events and reacts to them.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 15, 2003 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class DBBrowserAppListener implements ApplicationListener
{
	/**
	 * The primary frame of the application.
	 */
	private MainFrame mainFrame = null;
	
	DBBrowserAppListener(MainFrame mainFrame)
	{
		super();
		this.mainFrame = mainFrame;
		// The following is needed to Enable the "Properties" menu 
		// item on the application menu of Apple computers. :-)
		(new com.apple.eawt.Application()).setEnabledPreferencesMenu(true);
		(new com.apple.eawt.Application()).addApplicationListener(this);
	}
	
	/**
	 * @see com.apple.eawt.ApplicationListener#handleAbout(com.apple.eawt.ApplicationEvent)
	 */
	public void handleAbout(ApplicationEvent ae) {
		if (mainFrame.handleAbout()) {
			ae.setHandled(true);
		}
	}

	/**
	 * @see com.apple.eawt.ApplicationListener#handleOpenApplication(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenApplication(ApplicationEvent ae) {
		if (mainFrame.handleOpenApplication()) {
			ae.setHandled(true);
		}
	}

	/**
	 * @see com.apple.eawt.ApplicationListener#handleOpenFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenFile(ApplicationEvent ae) {
		if (mainFrame.handleOpenFile()) {
			ae.setHandled(true);
		}
	}

	/**
	 * @see com.apple.eawt.ApplicationListener#handlePreferences(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePreferences(ApplicationEvent ae)
	{
		if (mainFrame.handlePreferences()) {
			ae.setHandled(true);
		}
	}

	/**
	 * @see com.apple.eawt.ApplicationListener#handlePrintFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePrintFile(ApplicationEvent ae)
	{
		if (mainFrame.handlePrintFile()) {
			ae.setHandled(true);
		}
	}

	/**
	 * @see com.apple.eawt.ApplicationListener#handleQuit(com.apple.eawt.ApplicationEvent)
	 */
	public void handleQuit(ApplicationEvent ae)
	{
		if (mainFrame.handleQuit()) {
			ae.setHandled(true);
		}
	}
}
