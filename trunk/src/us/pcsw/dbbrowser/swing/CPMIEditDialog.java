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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import us.pcsw.dbbrowser.ConnectionProviderModelItem;
import us.pcsw.util.Debug;

/**
 * CPMIEditDialog
 * -
 * Allows the user to edit a ConnectionProviderModelItem.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Apr 22, 2003 Class Created. PAC </LI>
 * </UL></P>
 */
final class CPMIEditDialog extends JDialog
	implements ActionListener
{	
	private JButton
		cancelButton = null,
		saveButton = null;
	private JTextField
		classTextField = null,
		nameTextField = null;

	/**
	 * If true, indicates that the user cancelled.
	 */
	private boolean cancelled = true;

	/**
	 * The ConnectionProviderModelItem being edited.
	 */
	private ConnectionProviderModelItem cpmi = null;
	
    /**
     * Allows the user to create a new ConnectionProviderModelItem.
     * @param owner the Frame from which the dialog is displayed
     * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
     *                                     returns true.
     */
    public CPMIEditDialog(Frame owner)
    	throws HeadlessException
    {
        super(owner);
		this.setTitle("Add Connection Provider");
        initGUI();
    }

    /**
     * Allows the user to create a new ConnectionProviderModelItem.
     * @param owner the Dialog from which the dialog is displayed
     * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
     *                                     returns true.
     */
    public CPMIEditDialog(Dialog owner)
    	throws HeadlessException
    {
        super(owner);
		this.setTitle("Add Connection Provider");
        initGUI();
    }

	/**
	 * Allows the user to edit a ConnectionProviderModelItem.
	 * @param owner the Frame from which the dialog is displayed
	 * @param cpmi the ConnectionProviderModelItem to edit.
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                     returns true.
	 */
	public CPMIEditDialog(Frame owner, ConnectionProviderModelItem cpmi)
		throws HeadlessException
	{
		super(owner);
		this.setTitle("Add Connection Provider");
		this.cpmi = cpmi;
		initGUI();
	}

	/**
	 * Allows the user to edit ConnectionProviderModelItem.
	 * @param owner the Dialog from which the dialog is displayed
	 * @param cpmi the ConnectionProviderModelItem to edit.
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                     returns true.
	 */
	public CPMIEditDialog(Dialog owner, ConnectionProviderModelItem cpmi)
		throws HeadlessException
	{
		super(owner);
		this.setTitle("Add Connection Provider");
		this.cpmi = cpmi;
		initGUI();
	}

    /**
     * Handles user clicking the save or cancel button.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    	Object o = e.getSource();
		if (o == cancelButton) {
			cancelled = true;
			setVisible(false);
		} else if (o == saveButton) {
			try {
				if (cpmi == null) {
					cpmi = new ConnectionProviderModelItem
									(nameTextField.getText(),
									 classTextField.getText());			
				} else {
					cpmi.setConnectionProviderName(nameTextField.getText());
					cpmi.setClassName(classTextField.getText());
					cancelled = false;
					setVisible(false);
				}
			} catch (IllegalArgumentException iae) {
				Debug.log(iae);
				JOptionPane.showMessageDialog
					(this, iae.getMessage(),
					 "Cannot Load Connection Provider",
					 JOptionPane.ERROR_MESSAGE);
			}
		}
    }

	private void initGUI()
	{
		GridBagConstraints constraints = null;
		GridBagLayout gbLayout = new GridBagLayout();
		Insets insets = new Insets(12,12,6,12);
		JLabel label = null;
		
		setModal(true);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(gbLayout);
		
		constraints = new GridBagConstraints(0, 0,        // gridx, gridy 
								1, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTH, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		label = new JLabel("Name:");
		gbLayout.setConstraints(label, constraints);
		contentPane.add(label);
		
		constraints = new GridBagConstraints(1, 0,        // gridx, gridy 
								2, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		nameTextField = new JTextField(25);
		if (cpmi != null) {
			nameTextField.setText(cpmi.getConnectionProviderName());
		}
		gbLayout.setConstraints(nameTextField, constraints);
		contentPane.add(nameTextField);
		
		constraints = new GridBagConstraints(0, 1,        // gridx, gridy 
								1, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		label = new JLabel("Class:");
		gbLayout.setConstraints(label, constraints);
		contentPane.add(label);
		
		constraints = new GridBagConstraints(1, 1,        // gridx, gridy 
								2, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		classTextField = new JTextField(25);
		if (cpmi != null) {
			classTextField.setText(cpmi.getClassName());
		}
		gbLayout.setConstraints(classTextField, constraints);
		contentPane.add(classTextField);
		
		constraints = new GridBagConstraints(0, 2,        // gridx, gridy 
								1, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		label = new JLabel(" ");
		gbLayout.setConstraints(label, constraints);
		contentPane.add(label);
		
		constraints = new GridBagConstraints(1, 2,        // gridx, gridy 
								2, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		label = new JLabel(" ");
		gbLayout.setConstraints(label, constraints);
		contentPane.add(label);

		constraints = new GridBagConstraints(1, 3,        // gridx, gridy 
								1, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		saveButton = new JButton("Save");
		saveButton.setMnemonic('S');
		saveButton.addActionListener(this);
		getRootPane().setDefaultButton(saveButton);
		gbLayout.setConstraints(saveButton, constraints);
		contentPane.add(saveButton);
		
		constraints = new GridBagConstraints(2, 3,        // gridx, gridy 
								1, 1,        // gridWidth, gridHeight
								1, 1,        // weightx, weghty
								GridBagConstraints.NORTHWEST, // anchor
								GridBagConstraints.BOTH, // fill
								insets,      // insets
								0, 0);       // ipadx, ipady
		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		gbLayout.setConstraints(cancelButton, constraints);
		contentPane.add(cancelButton);
		
		pack();

		// Center the dialog
		Dimension mySize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width / 2) - (mySize.width / 2), 
				  (screenSize.height / 2) - (mySize.height / 2),
				  mySize.width, mySize.height);
	}

	/**
	 * Returns the edited or new ConnectionProviderModelItem, or null if this
	 * was a add and the user cancelled.
	 * @return ConnectionProviderModelItem the item.
	 */
	public ConnectionProviderModelItem getConnectionProviderModelItem()
	{
		return cpmi;
	}

	/**
	 * If true, indicates that the user cancelled.
	 */
	public boolean userCancelled()
	{
		return cancelled;
	}
}
