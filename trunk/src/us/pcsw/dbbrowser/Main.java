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

import java.io.File;
import java.io.IOException;
import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;

import us.pcsw.dbbrowser.swing.MainFrame;
import us.pcsw.util.Debug;

/**
 * us.pcsw.dbbrowser.Main
 * -
 * The entry point for the dbbrowser application.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/10/2001 Development began on this class.</LI>
 * <LI>10/18/2002 main was modified.  Most of the code to load and maintain
 *                parameters  was moved to the new Preferences class.</LI>
 * <LI>03/20/2003 Check for successfull initialization of Preferences class
 *                was added.  Removed old unused options from output in the
 *                usageErr method.
 * <LI>03/20/2003 Removed code in main(String[]) which retrieves a reference
 *                to a ConnectionProviderModel.  The requirement for the model
 *                was take out of MainFrame's constructor, and so it is not
 *                needed in main.  PAC </LI>
 * <LI>03/11/2005 Fixed a bug in which the saved look and feel was never
 *                honored.  PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class Main
	extends Object
{
    public static void main(String args[])
    {
		try {
			File dbConFile = null;
			File logFile = null;
			MainFrame mainFrame = null;
			
			// Make sure a GUI is available
			try {
			    GraphicsEnvironment.getLocalGraphicsEnvironment();
			} catch (Throwable t) {
				System.err.println
					("This application requires a Graphical User Interface.\n");
				System.exit(1);
			}
			    
		    // Parse any command line arguments
			for(int i = 0; i < args.length; i++) {
			    if (args[i].startsWith("-")) {
			    	if (dbConFile != null) {
			    		// An option was given after a connection file name
			    		// was provided.
			    		usageErr();
			    	} else {
						if (args[i].equals("-d")) {
							try {
							    logFile = new File(args[++i]);
							    Debug.setLogFile(logFile);
							} catch (IOException exc) {
							    System.err.println("Unable to open " + args[i] + "for output.  Debugging will be written to standard error output.");
							    Debug.logToStdErr();
							}
//						} else if (args[i].equals("-g")) {
							//javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			    		} else {
						    usageErr();
						}
			    	}
			    } else {
			    	if (dbConFile == null) {
						dbConFile = new File(args[i]);			    		
			    	} else {
						usageErr();
			    	}
			    } // end if args[i].startsWith("-")
			} // end for loop of args

			// Initialize preferences
			if (! Preferences.initialize()) {
				Debug.log("There was an error parsing the preferences file.  Defaults will be used.", 10);
			}
	    	
	    	try {
	    		String lafcn = Preferences.getLookAndFeelName();
	    		if (lafcn.length() > 0)
	    			UIManager.setLookAndFeel(lafcn);
	    		else
	    			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    	} catch (Throwable t) {
	    		Debug.log(t);
	    		try {
	    			// Use the system default
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    		} catch (Throwable t2) {
	    			Debug.log(t2);
	    		}
	    	}

			if (dbConFile == null) {
				mainFrame = new us.pcsw.dbbrowser.swing.MainFrame();
			} else {
				mainFrame = new us.pcsw.dbbrowser.swing.MainFrame(dbConFile);
			}
			mainFrame.setVisible(true);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			// The user did not provide a required argument for one of the
			// command-line switches.
			usageErr();
		} catch (Exception e) {
		    System.err.println("There was an unexpected error starting the DB Browser application.\n");
		    System.err.println(e.getMessage());
	    	Debug.log(e);
		}
    }

    /** Prints a usage listing to stderr. */
    private static void usageErr()
    {
        System.err.println("\n\nUsage: java us.pcsw.dbbrowser.Main [-d logfile] [filename]\n");
		System.err.println("-d logfile      Turns on debugging. Debug messages are logged to the file indicated by logfile.");
		System.err.println("filename        The name of a file containing saved connection parameters to load on startup.\n");
		System.exit(1);
    }
}
