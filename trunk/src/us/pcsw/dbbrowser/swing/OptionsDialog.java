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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import org.slf4j.LoggerFactory;

import us.pcsw.dbbrowser.ConnectionProviderModel;
import us.pcsw.dbbrowser.ConnectionProviderModelItem;
import us.pcsw.dbbrowser.Preferences;
import us.pcsw.swing.FontChooser;
import us.pcsw.swing.RegExpFormatter;

/**
 * us.pcsw.dbbrowser.swing.OptionsDialog
 * -
 * The dialog is used to change options for the application.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>06/05/2001 This class was created.</LI>
 * <LI>10/16/2002 Removed connection info fields from dialog.  It was
 *                redundant having them here as well as the login dialog.</LI>
 * <LI>10/18/2002 Added code to center the dialog on screen.</LI>
 * <LI>11/08/2002 Added font selection for SQL text.</LI>
 * <LI>03/14/2003 (GB) Changed constructor set dialog title based on platform.</LI>
 * <LI>03/13/2003 Added option to disable lazy load of cache.</LI>
 * <LI>03/19/2003 Removed the config field for connection provider list file.
 *                The list of available connection providers are no longer
 *                kept in a seperate file.  PAC </LI>
 * <LI>03/21/2003 (GB) Rearranged UI, set "Apply" button as default, Removed
 *                "Ignore SQL..." checkbox label.
 * <LI>03/21/2003 (GB) Modified field labels, tweaked UI Insets, and changed font
 *                JTextArea to a JTextField.</LI>
 * <LI>03/21/2003 (GB) Aligned field labels to the right.</LI> 
 * <LI>04/22/2003 Added the Connection Providers tab.  PAC </LI>
 * <LI>07/14/2003 Added option to save login secrets with connection info.
 *                During this process, I cleaned up the UI initialization code
 *                a little and Removed the unused Cancelled() property.  PAC
 *                </LI>
 * <LI>09/24/2003 Added results font option.  PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class OptionsDialog
 	extends javax.swing.JDialog
	implements java.awt.event.ActionListener,
				java.awt.event.FocusListener,
				java.awt.event.MouseListener,
				javax.swing.event.ChangeListener
{
	private static final long serialVersionUID = 1L;
	
	private JButton closeButton = null;
	private Font resFont = null;
	private Font sqlFont = null;
	private JTable cpList = null;	// Used to display connection providers
	private JButton cpListAddButton = null;  // Used to add a row to cpList
	private JButton cpListDelButton = null;  // Used to delete a row from cpList
	private JButton cpListEditButton = null; // Used to edit a row from cpList
	private JButton resFontButton = null;
	private JTextField resFontTextArea = null;
	private JButton sqlFontButton = null;
	private JTextField sqlFontTextArea = null;
	private JCheckBox ignoreSelCheckBox = null;
	private JCheckBox saveSecretCheckBox = null;
	private JTextField repForNullTextField = null;
	private JFormattedTextField tabSizeTextField = null;
	private JTabbedPane optTab = null;
	private JLabel cacheSizeLabel = null;
	private JScrollPane cpListScrollPane = null;
	private JSlider slider = null;

	/**
	 * Indicates changes were cancelled.
	 */
	private static String DialogTitle = "";
	private static int SLIDER_MAX_VALUE = 2499;
	
	/**
	 * Initializes the JDialog, but does not show it.
	 * @param parentFrame The calling JFrame.
	 * @param options The Properties object which holds option settings.  If
	 *                null is provided, a new Properties option is created with
	 *                empty values.
	 * @param cpm Model which provides a list of connection providers.
	 * @param cParm Object which holds connection parameters.
	 */
	public OptionsDialog
		(JFrame parentFrame)
	{
		// Call to super, setup title
		super(parentFrame);
		if (java.lang.System.getProperty("os.name").indexOf("Mac") == -1) {
			DialogTitle = "Application Options";
		} else {
			DialogTitle = "DBBrowser Preferences";
		}                                                                                
		this.setTitle(DialogTitle);

		// Set up overall layout
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		JPanel panel = null;

		// Set up tabbed form
		optTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT );
		panel = initTabGeneral();
		optTab.addTab("General Preferences", null, panel, "General Application Settings");
		optTab.setMnemonicAt(0, 'G');
		panel = initTabConnection();
		optTab.addTab("Connection Options", null, panel, "Configure Connection Options");
		optTab.setMnemonicAt(1, 'C');
		contentPane.add(optTab, BorderLayout.CENTER);

		// Cancel button
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener(this);
		panel.add(closeButton);
		contentPane.add(panel, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(closeButton);

		// Center the dialog
		getContentPane().add(contentPane);
		pack();
		Dimension mySize = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width / 2) - (mySize.width / 2), 
                  (screenSize.height / 2) - (mySize.height / 2),
                  mySize.width, mySize.height);
    }

    /**
     * Takes notification of ActionEvents originating with the select button,
     * the clear button, or the close button.
     */
    public void actionPerformed(ActionEvent event)
    {
    	try {
			ConnectionProviderModel cpm = Preferences.getConnectionProviderModel();
	    	CPMIEditDialog editDialog = null;
	    	
			Object o = event.getSource();
			if (closeButton == o) {
			    //cancelled = true;
		    	setVisible(false);
			} else if (cpListAddButton == o) {
				// Add a new Connection Provider list item.
				editDialog = new CPMIEditDialog(this);
				editDialog.setVisible(true);
				if (! editDialog.userCancelled()) {
					cpm.addConnectionProvider
						(editDialog.getConnectionProviderModelItem());
				}
			} else if (cpListDelButton == o) {
				// Delete the selected Connection Provider list item
				cpm.removeConnectionProviderItem(cpList.getSelectedRow());
			} else if (cpListEditButton == o) {
				editConnectionProviderListItem();
			} else if (resFontButton == o) {
				resFontButton_ActionEvent(event);
			} else if (sqlFontButton == o) {
				sqlFontButton_ActionEvent(event);
			}
    	} catch (Throwable t) {
    		handleException(t);
    	}
    }

	private void editConnectionProviderListItem()
		throws CloneNotSupportedException
	{
		// Edit the selected Connection Provider list item
		int i = cpList.getSelectedRow();
		ConnectionProviderModel cpm = Preferences.getConnectionProviderModel();
		ConnectionProviderModelItem cpmi =
			(ConnectionProviderModelItem)cpm.getElementAt(i);
		
		cpmi = (ConnectionProviderModelItem)cpmi.clone();
		CPMIEditDialog editDialog = new CPMIEditDialog(this, cpmi);
		editDialog.setVisible(true);
		if (! editDialog.userCancelled()) {
			cpm.removeConnectionProviderItem(i);
			cpm.addConnectionProvider(cpmi);
		}
	}

	private void resFontButton_ActionEvent(ActionEvent event)
	{
		FontChooser fcd = new FontChooser(this, "Choose Result Text Font", true, resFont);
		fcd.setVisible(true);
		setResultsFont(fcd.getSelectedFont());
	}
	
    private void sqlFontButton_ActionEvent(ActionEvent event)
    {
		FontChooser fcd = new FontChooser(this, "Choose SQL Text Font", true, sqlFont);
		fcd.setVisible(true);
		setSQLFont(fcd.getSelectedFont());
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
		LoggerFactory.getLogger(getClass()).error("Error occurred", exception);
	}
    
    private JPanel initTabConnection()
    {
		GridBagConstraints constraints = null;
		GridBagLayout gridBag = new GridBagLayout();
		JLabel label = null;
		JPanel panel = new JPanel(gridBag);

		// SEPARATOR PANEL
		JPanel sepPanel = new JPanel();
		sepPanel.setLayout(new GridBagLayout());
		
		// SEPARATOR TEXT
		label = new JLabel("Connection Providers");
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					1, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,4),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(label, constraints);
		sepPanel.add(label);
		
		// SEPARATOR
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		constraints = 
			new GridBagConstraints(1, 0,        // gridx, gridy 
					2, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.SOUTHWEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,0),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(sep, constraints);
		sepPanel.add(sep);
		
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(12,12,0,12),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(sepPanel, constraints);
		panel.add(sepPanel);
	
		// CONNECTION PROVIDER LIST
		ConnectionProviderModel cpm = Preferences.getConnectionProviderModel();
		cpList = new JTable(cpm);
		cpList.addMouseListener(this);
		cpList.setCellSelectionEnabled(false);
		cpList.setColumnSelectionAllowed(false);
		cpList.setRowSelectionAllowed(true);
		cpList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cpListScrollPane = new JScrollPane(cpList);
		cpListScrollPane.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
		constraints = 
			new GridBagConstraints(0, 1,        // gridx, gridy 
					3, 5,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.EAST, // anchor
					GridBagConstraints.BOTH, // fill
					new Insets(0,0,0,0),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)panel.getLayout()).setConstraints(cpListScrollPane, constraints);
		panel.add(cpListScrollPane);

		// Connection Provider List Mod Buttons
		sepPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		cpListAddButton = new JButton("Add");
		cpListAddButton.setMnemonic('A');
		cpListAddButton.addActionListener(this);
		sepPanel.add(cpListAddButton);

		
		cpListEditButton = new JButton("Edit");
		cpListEditButton.setMnemonic('E');
		cpListEditButton.addActionListener(this);
		sepPanel.add(cpListEditButton);
		
		cpListDelButton = new JButton("Delete");
		cpListDelButton.setMnemonic('D');
		cpListDelButton.addActionListener(this);
		sepPanel.add(cpListDelButton);

		constraints = 
			new GridBagConstraints(0, 6,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					1, 0,        // weightx, weghty
					GridBagConstraints.EAST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(5,5,5,5),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)panel.getLayout()).setConstraints(sepPanel, constraints);
		panel.add(sepPanel);

		// CONNECTION SAVE OPTIONS

		// SEPARATOR PANEL
		sepPanel = new JPanel();
		sepPanel.setLayout(new GridBagLayout());
		
		// SEPARATOR TEXT
		label = new JLabel("Connection Save Options");
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					1, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,4),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(label, constraints);
		sepPanel.add(label);
		
		// SEPARATOR
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		constraints = 
			new GridBagConstraints(1, 0,        // gridx, gridy 
					2, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.SOUTHWEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,0),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(sep, constraints);
		sepPanel.add(sep);
		
		constraints = 
			new GridBagConstraints(0, 7,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(12,12,0,12),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(sepPanel, constraints);
		panel.add(sepPanel);

		// SAVE CONNECTION PASSWORDS
		label = new JLabel("Save passwords in connection parameter file:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 8,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(6,12,12,6),      // insets
					   0, 0);       // ipadx, ipady
		((GridBagLayout)panel.getLayout()).setConstraints(label, constraints);
		panel.add(label);
		
		saveSecretCheckBox = new JCheckBox("", Preferences.storeConnectionSecrets());
		saveSecretCheckBox.addChangeListener(this);
		constraints = 
			new GridBagConstraints(1, 8,        // gridx, gridy 
					   2, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(6,0,12,12),      // insets
					   0, 0);       // ipadx, ipady
		((GridBagLayout)panel.getLayout()).setConstraints(saveSecretCheckBox, constraints);
		panel.add(saveSecretCheckBox);

		return panel;
	}
    
	private JPanel initTabGeneral()
	{
		GridBagConstraints constraints = null;
    	JLabel label = null;
    	JPanel panel = null;
		RegExpFormatter ref = null;
    	
		panel = new JPanel();
		GridBagLayout gridBag = new GridBagLayout();
		panel.setLayout(gridBag);

		// SEPARATOR PANEL
		JPanel sepPanel = new JPanel();
		sepPanel.setLayout(new GridBagLayout());
		
		// SEPARATOR TEXT
		label = new JLabel("Resultset");
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					1, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,4),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(label, constraints);
		sepPanel.add(label);
		
		// SEPARATOR
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		constraints = 
			new GridBagConstraints(1, 0,        // gridx, gridy 
					2, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.SOUTHWEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,0),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(sep, constraints);
		sepPanel.add(sep);
		
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(12,12,0,12),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(sepPanel, constraints);
		panel.add(sepPanel);



		// ROW CACHE PAGE SIZE
		label = new JLabel("Number of Rows to Fetch at One Time:");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		constraints = 
			new GridBagConstraints(0, 1,        // gridx, gridy 
					2, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					 new Insets(12,24,0,0),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);
	 	
		cacheSizeLabel = new JLabel();
		cacheSizeLabel.setFont(new Font("Default", Font.PLAIN, 10));
		if (Preferences.getCachePageSize() <= SLIDER_MAX_VALUE) {
			cacheSizeLabel.setText(String.valueOf(Preferences.getCachePageSize()).concat(" Rows"));
		} else {
			cacheSizeLabel.setText("\u221e Rows");
		}
		constraints = 
			new GridBagConstraints(2, 1,        // gridx, gridy 
					1, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.EAST, // anchor
					GridBagConstraints.NONE, // fill
					 new Insets(12,0,0,24),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(cacheSizeLabel, constraints);
		panel.add(cacheSizeLabel);
		
		JLabel endlbl = new JLabel("\u221e");
		endlbl.setFont(new Font("Default", Font.PLAIN, 18));
				
		java.util.Hashtable labelTable = new java.util.Hashtable();
		labelTable.put(new Integer(0), new JLabel("0") );
		labelTable.put(new Integer(500), new JLabel("500"));
		labelTable.put(new Integer(1000), new JLabel("1000")); 
		labelTable.put(new Integer(1500), new JLabel("1500"));
		labelTable.put(new Integer(2000), new JLabel("2000"));
		labelTable.put(new Integer(2500), endlbl); 
		
		slider = new JSlider(JSlider.HORIZONTAL,
										  0, 2500, 0);
		slider.setValue(Preferences.getCachePageSize());
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(500);
		slider.setMinorTickSpacing(250);
		slider.setLabelTable(labelTable);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		constraints = 
			 new GridBagConstraints(0, 2,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.CENTER, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					 new Insets(12,36,0,36),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(slider, constraints);
		panel.add(slider);
	 	
		// REPRESENTATION FOR NULL
		label = new JLabel("Representation for Null Values:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 3,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
						new Insets(6,12,0,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);

		repForNullTextField = new JTextField();
		repForNullTextField.setColumns(8);
		repForNullTextField.addFocusListener(this);
		repForNullTextField.setText(Preferences.getRepresentationForNull());
		constraints = 
			new GridBagConstraints(1, 3,        // gridx, gridy 
					   2, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   new Insets(6,0,0,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(repForNullTextField, constraints);
		panel.add(repForNullTextField);
		
		// FONT FOR RESULTS
		label = new JLabel("Results Text Font:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 4,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(6,12,36,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		resFontTextArea = new JTextField();
		resFontTextArea.setEditable(false);
		resFontTextArea.setColumns(35);
		resFontTextArea.setBackground(Color.WHITE);
		resFontTextArea.setHorizontalAlignment(JTextField.CENTER);
		try {
			setResultsFont(Preferences.getResultsFont());
		} catch (Throwable t) {
			setResultsFont(new Font("Monospaced", Font.PLAIN, 10));
		}
		panel1.add(resFontTextArea);
		panel1.add(Box.createHorizontalStrut(5));
		resFontButton = new JButton("Choose...");
		resFontButton.setMnemonic('C');
		resFontButton.addActionListener(this);
		panel1.add(resFontButton);
		constraints = 
			new GridBagConstraints(1, 4,        // gridx, gridy 
					   3, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					  new Insets(6,0,36,12),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(panel1, constraints);
		panel.add(panel1);

		// SEPARATOR PANEL
		sepPanel = new JPanel();
		sepPanel.setLayout(new GridBagLayout());
				
		// SEPARATOR TEXT
		label = new JLabel("SQL Editor Pane");
		constraints = 
			new GridBagConstraints(0, 0,        // gridx, gridy 
					1, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(12,0,0,4),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(label, constraints);
		sepPanel.add(label);
				
		// SEPARATOR
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		constraints = 
			new GridBagConstraints(1, 0,        // gridx, gridy 
					2, 1,        // gridWidth, gridHeight
					1, 1,        // weightx, weghty
					GridBagConstraints.SOUTHWEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(0,0,0,0),      // insets
					0, 0);       // ipadx, ipady
		((GridBagLayout)sepPanel.getLayout()).setConstraints(sep, constraints);
		sepPanel.add(sep);
				
		constraints = 
			new GridBagConstraints(0, 4,        // gridx, gridy 
					3, 1,        // gridWidth, gridHeight
					0, 1,        // weightx, weghty
					GridBagConstraints.WEST, // anchor
					GridBagConstraints.HORIZONTAL, // fill
					new Insets(12,12,0,12),      // insets
					0, 0);       // ipadx, ipady
		gridBag.setConstraints(sepPanel, constraints);
		panel.add(sepPanel);

		// TAB SIZE FOR SQL TEXT
		label = new JLabel("Tab Size for SQL Text:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 5,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(12,12,0,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);

		ref = new RegExpFormatter();
		ref.setPattern(Pattern.compile("\\d{1,2}?"));
		tabSizeTextField = new JFormattedTextField(ref);
		tabSizeTextField.setColumns(2);
		tabSizeTextField.addFocusListener(this);
		tabSizeTextField.setText(String.valueOf(Preferences.getSQLTabSize()));
		constraints = 
			new GridBagConstraints(1, 5,        // gridx, gridy 
					   3, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   new Insets(12,0,0,12),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(tabSizeTextField, constraints);
		panel.add(tabSizeTextField);

		// FONT FOR SQL TEXT
		label = new JLabel("SQL Text Font:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 6,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(6,12,0,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);

		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		sqlFontTextArea = new JTextField();
		sqlFontTextArea.setEditable(false);
		sqlFontTextArea.setColumns(35);
		sqlFontTextArea.setBackground(Color.WHITE);
		sqlFontTextArea.setHorizontalAlignment(JTextField.CENTER);
		try {
			setSQLFont(Preferences.getSQLFont());
		} catch (Throwable t) {
			setSQLFont(new Font("Monospaced", Font.PLAIN, 10));
		}
		panel1.add(sqlFontTextArea);
		panel1.add(Box.createHorizontalStrut(5));
		sqlFontButton = new JButton("Choose...");
		sqlFontButton.setMnemonic('h');
		sqlFontButton.addActionListener(this);
		panel1.add(sqlFontButton);
		constraints = 
			new GridBagConstraints(1, 6,        // gridx, gridy 
					   3, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					  new Insets(6,0,0,12),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(panel1, constraints);
		panel.add(panel1);

		// IGNORE SQL TEXT SLECTION
		label = new JLabel("Ignore Selected SQL Text on Excution:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints = 
			new GridBagConstraints(0, 7,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.EAST, // anchor
					   GridBagConstraints.HORIZONTAL, // fill
					   new Insets(6,12,12,6),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);
		
		ignoreSelCheckBox = new JCheckBox("", Preferences.ignoreSelectedText());
		ignoreSelCheckBox.addChangeListener(this);
		constraints = 
			new GridBagConstraints(1, 7,        // gridx, gridy 
					   3, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   new Insets(6,0,12,12),      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(ignoreSelCheckBox, constraints);
		panel.add(ignoreSelCheckBox);  
		
		return panel; 	
    }

	public void setResultsFont(Font font)
	{
		if (font != null && ! font.equals(resFont)) {
			resFont = font;
			Preferences.setResultsFont(font);
			resFontTextArea.setFont(font);
			resFontTextArea.setText(resFont.getName() + " - " + resFont.getSize() + " points");
		}
	}
	    
    public void setSQLFont(Font font)
    {
		if (font != null && ! font.equals(sqlFont)) {
			sqlFont = font;
			Preferences.setSQLFont(font);
			sqlFontTextArea.setFont(font);
			sqlFontTextArea.setText(sqlFont.getName() + " - " + sqlFont.getSize() + " points");
		}
    }
    
	public void stateChanged(ChangeEvent e) 
	{
		Object o = e.getSource();
		if (o.equals(slider)) {
			if (slider.getValue() > SLIDER_MAX_VALUE) {
				cacheSizeLabel.setText("\u221e Rows");
				Preferences.setCachePageSize(slider.getValue());
			} else {
				cacheSizeLabel.setText(String.valueOf(slider.getValue()).concat(" Rows"));
				Preferences.setCachePageSize(slider.getValue());
			}
		} else if (o.equals(ignoreSelCheckBox)) {
			Preferences.ignoreSelectedText(ignoreSelCheckBox.isSelected());
		} else if (o.equals(saveSecretCheckBox)) {
			Preferences.storeConnectionSecrets(saveSecretCheckBox.isSelected());
		}
	}
	
	public void focusLost(FocusEvent e) {
		if (e.getSource().equals(repForNullTextField)) {
			Preferences.setRepresentationForNull(repForNullTextField.getText());
			System.out.println(repForNullTextField.getText());
		} else if (e.getSource().equals(tabSizeTextField)) {
			Preferences.setSQLTabSize(Integer.parseInt(tabSizeTextField.getText()));
			System.out.println(tabSizeTextField.getText());
		}
	}
	
	public void focusGained(FocusEvent e) {}
	
    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {    	
		if (e.getSource() == cpList && e.getClickCount() == 2) {
			try {
				editConnectionProviderListItem();
			} catch (Throwable t) {
				handleException(t);
			}
		}
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {}
}
