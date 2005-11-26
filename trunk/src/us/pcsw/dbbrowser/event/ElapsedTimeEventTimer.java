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

import java.util.Date;
import java.util.Enumeration;
import java.util.TimerTask;
import java.util.Vector;

/**
 *  us.pcsw.dbbrowser.event.ElapsedTimeEventTimer
 * -
 * This class will report a StatusTypeEnum.TIME_ELAPSED StatusEvent to its
 * listeners after a preset time has elapsed.
 *
 * <P><B>Revision History:</B><UL>
 * <LI> Jan 4, 2004 This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public class ElapsedTimeEventTimer extends TimerTask
{
	/**
	 * The time at which the task began.
	 */
	private Date startTime;
	
	/**
	 * List of listeners to be notified of status events.
	 */
	private Vector statusListeners;
	
	/**
	 * Constructs a new instance of ElapsedTimeEventTimer.
	 */
	public ElapsedTimeEventTimer()
	{
		super();
		startTime = new Date();
	}

	/**
	 * Constructs a new instance of ElapsedTimeEventTimer.
	 * @param listener The listener to notify of TIME_ELAPSED status events.
	 */
	public ElapsedTimeEventTimer(StatusListener listener)
	{
		super();
		addStatusListener(listener);
		startTime = new Date();
	}

	/**
	 * Adds a listener to the list of those to be notified of status events
	 * thrown by this class.
	 * @param listener The listener.
	 */
	synchronized public void addStatusListener(StatusListener listener)
	{
		if (statusListeners == null) {
			statusListeners = new Vector(1, 1);
		}
		if (! statusListeners.contains(listener)) {
			statusListeners.add(listener);
		}
	}

	synchronized private Enumeration getNotifyListeners()
	{
		if (statusListeners != null) {
			// Clone the vector because we do not want to run into trouble if a
			// listener adds or removes itself while we are iterating through the
			// enumeration. 
			Vector clone = (Vector)statusListeners.clone();
			return clone.elements();
		} else {
			return null;
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		StatusListener listener = null;
		Enumeration e = getNotifyListeners();
		if (e != null) {
			Long elapsedTime = new Long(new Date().getTime() - startTime.getTime());
			StatusEvent se = new StatusEvent(this, StatusTypeEnum.TIME_ELAPSED, elapsedTime);
			while (e.hasMoreElements()) {
				listener = (StatusListener)e.nextElement();
				listener.statusChanged(se);
			}
		}
	}

	/**
	 * Removes the status listener from those that should be notified of
	 * status events.
	 * @param listener The listener.
	 */
	synchronized public void removeStatusListener(StatusListener listener)
	{
		if (statusListeners != null) {
			statusListeners.remove(listener);
		}
	}
}
