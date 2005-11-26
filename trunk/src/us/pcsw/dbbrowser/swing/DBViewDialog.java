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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.WeakHashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import us.pcsw.dbbrowser.DBObjectsTreeNode;
import us.pcsw.dbbrowser.DBObjectsTreeModel;
import us.pcsw.dbbrowser.Preferences;
import us.pcsw.dbbrowser.ProcedureTreeNode;
import us.pcsw.dbbrowser.TableTreeNode;
import us.pcsw.dbbrowser.TablesTreeNodeType;
import us.pcsw.dbbrowser.cp.ConnectionProvider;
import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusListener;
import us.pcsw.dbbrowser.event.StatusTypeEnum;
import us.pcsw.util.Debug;

/**
 * us.pcsw.dbbrowser.swing.DBViewDialog
 * -
 * Allows the user to view a hirarchical view of the DB objects.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 8, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Jan 2, 2003  This class's super was changed to JFrame from JDialog
 *                  so that it would not float and be always on top.</LI>
 * <LI>Feb 15, 2003 Implementation of StatusListener was added.</LI>
 * <LI>May 28, 2003 Added code to restore the dialog's size and location to
 *                  what it was when it was last closed.  PAC </LI>
 * <LI>Jul 25, 2003 Added a popup menu for tree nodes.  Added a popup menu
 *                  item that will retrieve a procedure's text and pass it
 *                  to status listeners through the status event's data
 *                  memeber.  PAC </LI>
 * <LI>Aug 1, 2003  Added a popup menu item that will retrieve a view's text,
 *                  similar to the one for procedures.  Improved the procedure
 *                  text view code.  PAC </LI>
 * </UL></P>
 */
public final class DBViewDialog
	extends JFrame
	implements ActionListener, MouseListener, StatusListener
{
	private int busyStatusCount = 0;
	private JButton closeButton = null;
	private Hashtable dbViewModels = null;
	private JTree dbViewTree = null;
	private JPopupMenu popup = null;
	private DBObjectsTreeNode popupNode = null;
	private JMenuItem popupProcText = null;
	private ConnectionProvider popupProvider = null;
	private JMenuItem popupViewText = null;
	private JButton refreshButton = null;
	private JComboBox viewsComboBox = null;
	
	/**
	 * Listeners for the status event.
	 */
	protected WeakHashMap statusListeners = new WeakHashMap();
	
	/**
	 * Constructor for DBObjectsDialog.
     * @param conProv The connection provider to the database server.
     * @exception ClassNotFoundException indicates there was an error
     *                       connecting to the database.
     * @exception IllegalAccessException indicates there was an error
     *                       connecting to the database.
     * @exception InstintiationException indicates there was an error
     *                       connecting to the database.
     * @exception SQLException indicates that an error occurred while querying
     *                         the database server.
     */
	public DBViewDialog(ConnectionProvider conProv)
		throws ClassNotFoundException, IllegalAccessException,
		              InstantiationException, SQLException
	{
		super("Database Browser - DB View");

		// Set the icon of the dialog.
		ImageIcon icon = null; //new ImageIcon(getClass().getResource
//			("/us/pcsw/dbbrowser/resources/images/DBView.png"));
//		setIconImage(icon.getImage());

		getContentPane().setLayout(new BorderLayout());
		
		DBObjectsTreeModel dbViewModel = new DBObjectsTreeModel(conProv);
		dbViewModel.addStatusListener(this);
		String title = dbViewModel.getTitle();
		dbViewModels = new Hashtable();
		dbViewModels.put(title, dbViewModel);
		
		viewsComboBox = new JComboBox();
		((DefaultComboBoxModel)viewsComboBox.getModel()).addElement(title);
		viewsComboBox.addActionListener(this);
		getContentPane().add(viewsComboBox, BorderLayout.NORTH);
		
		dbViewTree = new JTree(dbViewModel);
		dbViewTree.setDragEnabled(true);
		dbViewTree.addMouseListener(this);
		JScrollPane sPane = new JScrollPane(dbViewTree);
		getContentPane().add(sPane, BorderLayout.CENTER);

		// Popup menues for dbViewTree
		popup = new JPopupMenu();
		popupProcText = new JMenuItem("Load Procedure Text");
		popupProcText.setMnemonic('P');
		popupProcText.addActionListener(this);
		popup.add(popupProcText);
		popupViewText = new JMenuItem("Load View Text");
		popupViewText.setMnemonic('V');
		popupViewText.addActionListener(this);
		popup.add(popupViewText);
		
		JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		refreshButton = new JButton("Refresh");
		refreshButton.setMnemonic('R');
		refreshButton.addActionListener(this);
		panel.add(refreshButton);
		bottomPanel.add(panel);
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener(this);
		panel.add(closeButton);
		bottomPanel.add(panel);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		pack();
		// Default to upper left
		Rectangle saneDefaults = new Rectangle(0, 0, getWidth(), getHeight());
		Rectangle myBounds = Preferences.getDbViewBounds();
		if (myBounds == null) {
			myBounds = saneDefaults;
		} else {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			// Make sure the stored bounds will fit in the current screen
			if (myBounds.height > screenSize.height) {
				// Too tall for the screen, resize to default.
				myBounds.height = saneDefaults.height;
			}
			if (myBounds.width > screenSize.width) {
				// Too wide for the screen, resize to default
				myBounds.width = saneDefaults.width;
			}
			if ((myBounds.height + myBounds.y) > screenSize.height ||
				myBounds.y < 0) {
				// It will hang off the screen, far top instead. 
				myBounds.y = 0;
			}
			if ((myBounds.width + myBounds.x) > screenSize.width ||
				myBounds.x < 0) {
				// It will hang off the screen, far left instead.
				myBounds.x = 0;
			}
		}
		setBounds(myBounds);
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		try {
			if (closeButton.equals(e.getSource())) {
				Preferences.setDbViewBounds(getBounds());
				dispose();
			} else if (popupProcText.equals(e.getSource())) {
				popupProcText_Action(e);
			} else if (popupViewText.equals(e.getSource())) {
				popupViewText_Action(e);
			} else if (viewsComboBox.equals(e.getSource())) {
				dbViewTree.setModel((TreeModel)dbViewModels.get(viewsComboBox.getSelectedItem()));
			} else if (refreshButton.equals(e.getSource())) {
				((DBObjectsTreeModel)dbViewTree.getModel()).refresh();
			}
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * If the provided connection provider does not already exist, a view for
	 * it is added.
	 */
	public void add(ConnectionProvider conProv)
	{
		try {
			DBObjectsTreeModel dbViewModel = new DBObjectsTreeModel(conProv);
			String title = dbViewModel.getTitle();
			if (! dbViewModels.containsKey(title)) {
				dbViewModel.addStatusListener(this);
				dbViewModels.put(title, dbViewModel);
				((DefaultComboBoxModel)viewsComboBox.getModel()).addElement(title);
			}
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Adds a StatusListener to the list to be notified of StatusEvents.
	 * @param l The StatusListener to be notified.
	 */
	public void addStatusListener(StatusListener l)
	{
		// don't add the listener if it's already in the list
		if (! statusListeners.containsKey(l)) {
			statusListeners.put(l, new WeakReference(l));
		}	
	}

    /**
     * Called whenever an exception is thrown.
     * @param exception Throwable
     */
    private void handleException(Throwable exception)
    {
		JOptionPane.showMessageDialog(this, exception.getMessage(),
		                              "Unexpected Error",
		                              JOptionPane.ERROR_MESSAGE);
		Debug.log(exception);
    }

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		// Do Nothing
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// Do Nothing
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// Do Nothing
	}

	/**
	 * Processes a mouse event to determine if the popup Menu should be shown.
	 * @param e The mouse event.
	 */
	private void mousePopup(MouseEvent e)
	{
		if (e.getSource() == dbViewTree && e.isPopupTrigger()) {
			// Assume we will not have to show the popup menu.
			boolean show = false;
			popupProcText.setEnabled(false);
			popupViewText.setEnabled(false);

			Object node = dbViewTree.getPathForLocation
				(e.getX(), e.getY()).getLastPathComponent();

			if (node instanceof DBObjectsTreeNode) {
				popupNode = (DBObjectsTreeNode)node;
				DBObjectsTreeModel treeModel = (DBObjectsTreeModel)dbViewModels.get(viewsComboBox.getSelectedItem());
				popupProvider = treeModel.getConnectionProvider();

				if (node instanceof ProcedureTreeNode && popupProvider.canGetProcedureText()) {
					popupProcText.setEnabled(true);
					show = true;
				} else if (node instanceof TableTreeNode) {
					if (((TableTreeNode)node).getType() == TablesTreeNodeType.VIEWS &&
						popupProvider.canGetViewText())
					{ 
						popupViewText.setEnabled(true);
						show = true;
					}
				}
			}
			// If it was found that the popup menu should be shown, show it.
			if (show) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}		
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		mousePopup(e);
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		mousePopup(e);
	}

	/**
	 * The StatusEvent is sent to all registered listeners.  This is done in
	 * the current thread.
	 * @param event The StatusEvent to notify the listeners with.
	 */
	protected void notifyStatusListeners(StatusEvent e)
	{
		StatusListener sListener;
		Iterator iterator;
	
		// Notify each listener.  Since this class "echos" events recieved,
		// a check is made to be sure that the originating object is not
		// notified.  Such a thing may cause an endless loop if the source
		// object doesn't check the event's origin.
		iterator = statusListeners.values().iterator();
		while (iterator.hasNext()) {
			sListener = ((StatusListener)((WeakReference)iterator.next()).get());
			if (sListener != null) {
				sListener.statusChanged(e);
			}
		}
	}
	
	/**
	 * Respond to the View Text popup menu item.
	 */
	private void popupProcText_Action(ActionEvent e)
	{
		try {
			ProcedureTreeNode node = (ProcedureTreeNode)popupNode; 
			
			String procText =
				popupProvider.getProcedureText(popupProvider.getConnection(),
			                                   node.getCatalog(),
			                                   node.getSchema(),
			                                   node.getName());
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.DATA_AVAIL, (Object)procText));
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Respond to the View Text popup menu item.
	 */
	private void popupViewText_Action(ActionEvent e)
	{
		try {
			TableTreeNode node = (TableTreeNode)popupNode; 
			
			String viewText =
				popupProvider.getViewText(popupProvider.getConnection(),
										  node.getCatalog(),
										  node.getSchema(),
										  node.getName());
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.DATA_AVAIL, (Object)viewText));
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Removes the provided connection provider from the view.
	 */
	public void remove(ConnectionProvider conProv)
	{
		try {
			DBObjectsTreeModel dbViewModel = new DBObjectsTreeModel(conProv);
			String title = dbViewModel.getTitle();
			if (dbViewModels.containsKey(title)) {
				dbViewModels.remove(title);
				((DefaultComboBoxModel)viewsComboBox.getModel()).removeElement(title);
			}
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Removes a StatusListener from the list to be notified of StatusEvents.
	 * @param l The StatusListener to be removed.
	 */
	public void removeStatusListener(StatusListener l)
	{
		statusListeners.remove(l);
	}

	/**
	 * Method called when the status of one of the table Models have changed.
	 */
	public void statusChanged(StatusEvent se)
	{
		// Right now I keep a count of all the busy events and decrement that
		// count for each not-busy event received.  The cursor goes to wait
		// if any nodes report busy and default if none are busy.
		if (se.getType() == StatusTypeEnum.BUSY) {
			busyStatusCount++;
		} else if (se.getType() == StatusTypeEnum.NOT_BUSY) {
			busyStatusCount--;
			if (busyStatusCount < 0) {
				// This should never happen.
				Debug.log("ASSERT:  DBViewDialog:  busyStatusCount >= 0 IS FALSE", 10);
				busyStatusCount = 0;
			}
		} else if (se.getType() == StatusTypeEnum.ERROR) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(se.getThrowable());
		}
		if (busyStatusCount == 0) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}
}
