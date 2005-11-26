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

import us.pcsw.dbbrowser.HistoryListModel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * us.pcsw.dbbrowser.swing.HistoryListDialog
 * -
 * Displays the SQL statement history in a list, allowing the user to choose a
 * SQL statement.  In the constructor, it registers the MainFrame object as an
 * ActionEvent listener.  When an SQL statement is selected, MainFrame's
 * actionPerformed method will be called with an action event.  The action
 * event will have the SQL statement as it's command String.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/30/2001 Development began on this class.</LI>
 * <LI>10/18/2002 Added code to center the dialog on the screen.</LI>
 * <LI>11/11/2005 Philip A. Chapman fixed a bug where the close button did not
 *                close the dialog.</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
final class HistoryListDialog
    extends javax.swing.JDialog
    implements java.awt.event.ActionListener,
	       java.awt.event.MouseListener
{
    private JList statementList = null;
    private JScrollPane statementScrollPane = null;
    private JPanel buttonPane = null;
    private JButton selectButton = null;
    private JButton clearButton = null;
    private JButton closeButton = null;

    /**
     * Holds registered data change listeners.
     */
    private Vector actionListeners = null;

    /**
     * Contains the list of SQL statements which have been executed.
     */
    private HistoryListModel hlm = null;

    /**
     * Initializes the JDialog, but does not show it.
     * @exception IllegalArgumentException will be thrown if a null value is
     *                                     provided for hlm.
     * @param owner The owner Frame.
     * @param hlm The history list model which contains the list of SQL
     *            statements which have been executed.
     * @param lsm The ListSelectionModel to be used by the history list.  If
     *            null, the list's default model is used.
     */
    HistoryListDialog
    	(java.awt.Frame owner, HistoryListModel hlm, ListSelectionModel lsm)
	{
		super(owner, "SQL Statement History");
		if (hlm == null) {
		    throw new IllegalArgumentException("A null value was provided for the SQL statement list provider.");
		}
		this.hlm = hlm;
	
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
	
		statementList = new JList(hlm);
		if (lsm == null) {
		    statementList.getSelectionModel().setSelectionMode
				(ListSelectionModel.SINGLE_SELECTION);
		} else {
		    statementList.setSelectionModel(lsm);
		}
		statementScrollPane = new JScrollPane(statementList);
		statementList.addMouseListener(this);
		contentPane.add(statementScrollPane, BorderLayout.CENTER);
	
		buttonPane = new JPanel();
	
		selectButton = new JButton("Select");
		selectButton.setMnemonic('S');
		selectButton.addActionListener(this);
		buttonPane.add(selectButton);
	
		clearButton = new JButton("Clear");
		clearButton.setMnemonic('l');
		clearButton.addActionListener(this);
		buttonPane.add(clearButton);
	
		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener(this);
		buttonPane.add(closeButton);
	
		contentPane.add(buttonPane, BorderLayout.SOUTH);
	
		pack();

		// Center the dialog
		Dimension mySize = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension halfScreenSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
     	if (mySize.height > halfScreenSize.height) {
     		mySize.height = halfScreenSize.height;
     	}
     	if (mySize.width > halfScreenSize.width) {
     		mySize.width = halfScreenSize.width;
     	}
        setBounds(halfScreenSize.width - (mySize.width / 2), 
                  halfScreenSize.height - (mySize.height / 2),
                  mySize.width, mySize.height);
    }

    /**
     * Add an ActionListener to the list of listeners to be notified when a
     * SQL statement is selected by the user.
     */
    void addActionListener(ActionListener l)
    {
		if (actionListeners == null) {
		    // Not many listeners are expected, so grow by one element only.
		    // It will be slightly slower, but will not waste memory.
		    actionListeners = new Vector(1,1);
		}
		if (! actionListeners.contains(l)) {
		    actionListeners.addElement(l);
		}
    }

    /**
     * Takes notification of ActionEvents originating with the select button,
     * the clear button, or the close button.
     */
    public void actionPerformed(ActionEvent event)
    {
		Object o = event.getSource();
		if (clearButton.equals(o)) {
		    if (hlm != null) {
				hlm.clear();
		    }
		} else if (closeButton.equals(o)) {
		    setVisible(false);
		} else if (selectButton.equals(o)) {
		    fireActionPerformed();
		}
    }

    /**
     * Notifies all registered ActionListeners that a new SQL statement has
     * been selected.
     */
    void fireActionPerformed()
    {
		if (actionListeners != null) {
		    ActionListener al;
		    int index = statementList.getSelectedIndex();
		    ActionEvent event = new ActionEvent
				(this, index, (String)hlm.getElementAt(index));
		    if (index > -1) {
				// Clone vector of listeners in case a listener's
				// actionPerformed method removes the listener from the list.
				Vector copyOfListeners = (Vector)(actionListeners.clone());
		
				// Notify each listener.
				Enumeration enum = copyOfListeners.elements();
				while (enum.hasMoreElements()) {
				    ((ActionListener)enum.nextElement()).actionPerformed(event);
				}
		    }
		}
    }

    /**
     * If this is a double-click, fireActionPerformed is called.
     */
    public void mouseClicked(MouseEvent event)
    {
		if (event.getClickCount() == 2) {
		    int index = statementList.locationToIndex(event.getPoint());
		    statementList.setSelectedIndex(index);
		    fireActionPerformed();
		}
    }

    /** Not implemented */
    public void mouseEntered(MouseEvent event) {}

    /** Not implemented */
    public void mouseExited(MouseEvent event) {}

    /** Not implemented */
    public void mousePressed(MouseEvent event) {}

    /** Not implemented */
    public void mouseReleased(MouseEvent event) {}
}
