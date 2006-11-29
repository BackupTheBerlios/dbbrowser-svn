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
 * us.pcsw.dbbrowser.StatusEvent
 * -
 * Description of a status.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Feb 15, 2003 This class was created.</LI>
 * <LI>Feb 16, 2003 Added an optional description field and a throwable
 *                  field so that the StatusEvent could be a carrier of
 *                  trapped exceptions, not only a notifier.  PAC </LI>
 * <LI>Jul 25, 2003 Added an optional data field which will be used to pass
 *                  miscellaneous data to the status listeners.  PAC </LI>
 * </UL></P>
 */
public class StatusEvent
	extends java.util.EventObject
{
	private static final long serialVersionUID = 1L;

	/**
	 * Misc. data to be passed to the listener.
	 */
	private Object data = null;
	
	/**
	 * A description of the event.
	 */
	private String desc = "";
	
	/**
	 * The type of event.
	 */
	private StatusTypeEnum statusType;

	/**
	 * Throwable which caused the event status to be thrown.
	 */
	private Throwable throwable = null;
	
	/**
	 * Constructor for StatusEvent.
	 * @param statusType The type of status.
	 * @param source The source of the event.
	 * @exception IllegalArgumentException Indicates that either statusType or
	 *                                      source were null.
	 */
	public StatusEvent(Object source, StatusTypeEnum statusType)
		throws IllegalArgumentException
	{
		super(source);
		if (statusType == null) {
			throw new IllegalArgumentException("Status Type cannot be null");			
		} else if (source == null) {
			throw new IllegalArgumentException("Source cannot be null");
		} else {
			this.statusType = statusType;
		}
	}

	/**
	 * Constructor for StatusEvent.
	 * @param statusType The type of status.
	 * @param source The source of the event.
	 * @param data Miscellaneous data to pass to the listener.
	 * @exception IllegalArgumentException Indicates that either statusType or
	 *                                      source were null.
	 */
	public StatusEvent(Object source, StatusTypeEnum statusType, Object data)
		throws IllegalArgumentException
	{
		this(source, statusType);
		this.data = data;
	}
	
	/**
	 * Constructor for StatusEvent.
	 * @param statusType The type of status.
	 * @param source The source of the event.
	 * @param desc A description of the event.
	 * @exception IllegalArgumentException Indicates that either statusType or
	 *                                      source were null.
	 */
	public StatusEvent(Object source, StatusTypeEnum statusType, String desc)
		throws IllegalArgumentException
	{
		super(source);
		if (statusType == null) {
			throw new IllegalArgumentException("Status Type cannot be null");			
		} else if (source == null) {
			throw new IllegalArgumentException("Source cannot be null");
		} else {
			this.statusType = statusType;
			if (desc != null) {
				this.desc = desc;
			}
		}
	}

	/**
	 * Constructor for StatusEvent.
	 * @param statusType The type of status.
	 * @param source The source of the event.
	 * @param desc A description of the event.
	 * @param data Miscellaneous data to pass to the listener.
	 * @exception IllegalArgumentException Indicates that either statusType or
	 *                                      source were null.
	 */
	public StatusEvent(Object source, StatusTypeEnum statusType, String desc, Object data)
		throws IllegalArgumentException
	{
		this(source, statusType, desc);
		this.data = data;
	}
	/**
	 * Constructor for StatusEvent which creates an Error status event and uses
	 * the Throwable's message as a description.
	 * @param source The source of the event.
	 * @param throwable The exception which cause the status event to be fired.
	 * @exception IllegalArgumentException Indicates that throwable was null.
	 * @see us.pcsw.dbbrowser.StatusTypeEnum#ERROR
	 */
	public StatusEvent(Object source, Throwable throwable)
		throws IllegalArgumentException
	{
		super(source);
		if (throwable == null) {
			throw new IllegalArgumentException("The throwable cannot be null.");
		} else {
			this.desc = throwable.getMessage();
			this.throwable = throwable;
		}
	}

	/**
	 * Get misc data passed to the listener.
	 * @return the data.
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Returns a description of the event.
	 */
	public String getDescription()
	{
		return desc;
	}
	
	/**
	 * Gets the throwable responsable for the error status.  Null if the status
	 * is not of the Error type.
	 * @see #StatusEvent(Object, Throwable)
	 */
	public Throwable getThrowable()
	{
		return throwable;
	}

	/**
	 * Returns the type of this event.
	 */
	public StatusTypeEnum getType()
	{
		return statusType;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(statusType.toString());
		sb.append(" status event");
		if (desc.length() > 0) {
			sb.append(":  ");
			sb.append(desc);
		}
		return sb.toString();
	}
}
