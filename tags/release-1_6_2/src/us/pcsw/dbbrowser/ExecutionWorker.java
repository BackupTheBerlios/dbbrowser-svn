package us.pcsw.dbbrowser;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.SwingUtilities;

import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusListener;
import us.pcsw.swing.SwingWorker;

public abstract class ExecutionWorker extends SwingWorker
{
	protected ExecutionWorker()
	{
		super();
	}

	/**
	 * List of listeners to be notified of status events.
	 */
	private Vector statusListeners;
	
	/**
	 * Adds a listener to the list of those to be notified of status events
	 * thrown by this class.
	 * @param listener The listener.
	 */
	public void addStatusListener(StatusListener listener)
	{
		if (statusListeners == null) {
			statusListeners = new Vector(1, 1);
		}
		if (! statusListeners.contains(listener)) {
			statusListeners.add(listener);
		}
	}
	
	/**
	 * Notifies all listeners of a status event.
	 * @param ae The event to notify the listeners of.
	 */
	protected void notifyStatusListeners(StatusEvent se)
	{
		if (SwingUtilities.isEventDispatchThread()) {
			if (statusListeners != null) {
				// Clone the vector because we do not want to run into trouble if a
				// listener adds or removes itself while we are iterating through the
				// enumeration. 
				Vector clone = (Vector)statusListeners.clone();
				StatusListener listener;
				Enumeration e = clone.elements();
				while (e.hasMoreElements()) {
					listener = (StatusListener)e.nextElement();
					listener.statusChanged(se);
				}
			}
		} else {
			SwingUtilities.invokeLater(new NotifyWorker(this, se));
		}
	}
	
	/**
	 * Removes the status listener from those that should be notified of
	 * status events.
	 * @param listener The listener.
	 */
	public void removeStatusListener(StatusListener listener)
	{
		if (statusListeners != null) {
			statusListeners.remove(listener);
		}
	}


	/**
	 * Helper class for SQLExceptionWorker.
	 */
	private class NotifyWorker extends Object
		implements Runnable
	{
		private StatusEvent se;
		private ExecutionWorker worker;
		
		NotifyWorker(ExecutionWorker worker, StatusEvent se)
		{
			this.se = se;
			this.worker = worker;
		}
		
		public void run()
		{
			worker.notifyStatusListeners(se);
		}
	}
}
