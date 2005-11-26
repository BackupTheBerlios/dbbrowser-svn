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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;

import us.pcsw.dbbrowser.ColumnTreeNode;
import us.pcsw.dbbrowser.TableTreeNode;

import us.pcsw.dbbrowser.cp.ConnectionProvider;
import us.pcsw.dbbrowser.cp.DataType;

import us.pcsw.dbbrowser.dataimport.CSVImport;
import us.pcsw.dbbrowser.dataimport.DataImport;
import us.pcsw.dbbrowser.dataimport.ImportColumn;
import us.pcsw.dbbrowser.dataimport.ImportEvent;
import us.pcsw.dbbrowser.dataimport.ImportListener;

import us.pcsw.swing.BasicFileFilter;
import us.pcsw.swing.HorizontalGlue;
import us.pcsw.swing.HorizontalStrut;
import us.pcsw.swing.WrappedOptionPane;

import us.pcsw.util.Debug;
import us.pcsw.util.ExtVector;
import us.pcsw.util.Message;

/**
 * us.pcsw.dbbrowser.swing.ImportFrame
 * -
 * An interface that allows a user to import data.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 10, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class ImportFrame extends JFrame
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;
	
	private static final String[][] tabLabels = {
			{"File and Table", "<html>Select the file to import and the table to import into.  If the table name<br>does not match that of an existing table; a new table will be created.<html>"},
			{"Set Up Columns", "<html>Set up the columns into which the data will be imported.<html>"},
			{"Import Data",    "<html>Click the \"Import\" button to begin the import.  Results of the import will<br>be shown in the message area below.<html>"}
	};
	
	private static final char[] tabMnemonics = {'F', 'C', 'D'};
	
	// CONSTRUCTORS
	
	public ImportFrame(ConnectionProvider provider)
	{
		super("Import Data");
		this.provider = provider;
		initGUI();
		updateNavButtons();
	}

	// MEMBERS
	
	private ColumnSelectionListener columnSelectionListener;
	private JSpinner columnSpinner;
	private int currentTab = 0;
	private ImportColumn[] columns = null;
	private JFileChooser fileChooser;
	private JComboBox columnNameCombo;
	private DataImport dataImport;
	private JComboBox dataTypeCombo;
	private JButton executeButton;
	private JCheckBox firstRowHeaderBox;
	/**
	 * Used by inner classes to reference this instance.
	 */
	private JFrame me = this;
	private JButton nextButton;
	private JTextField precisionField;
	private JButton previousButton;
	private JProgressBar progressBar;
	private ConnectionProvider provider;
	private JTextArea resultsArea;
	private JTable sampleTable;
	private JTextField scaleField;
	private File selectedFile;
	private Object selectedTable;
	private JCheckBox skipColumnCheck;
	private JTabbedPane stepsTab;
	private JComboBox tableCombo;
	
	private int getCurrentTab()
	{
		return currentTab;
	}
	
	private void setCurrentTab(int tabIndex)
	{
		if (tabIndex > -1 && tabIndex < tabMnemonics.length) {
			stepsTab.setSelectedIndex(tabIndex);
			if (tabIndex == 1) {
				populateTab1();
			}
			currentTab = tabIndex;
		}
	}
	
	// METHODS
	
	private void columnNameSelected()
	{
		ColumnTreeNode ctn;
		Object o = columnNameCombo.getSelectedItem();
		if (o instanceof ColumnTreeNode) {
			// Find the data type that matches that of the
			// column and select it.
			
			ctn = (ColumnTreeNode)o;
			DefaultComboBoxModel dcbm = (DefaultComboBoxModel)dataTypeCombo.getModel();
			DataType dt;
			for (int i = 0; i < dcbm.getSize(); i++) {
				dt = (DataType)dcbm.getElementAt(i);
				if (
						dt.getJDBCType() == ctn.getType() &&
						dt.getTypeName().equals(ctn.getTypeName())
					)
				{
					dataTypeCombo.setSelectedIndex(i);
					Boolean b = dt.isPrecisionRequired();
					if (b != null && b.booleanValue()) {
						precisionField.setText(String.valueOf(ctn.getPrecision()));
					} else {
						precisionField.setText("");
					}
					precisionField.setEditable(false);
					b = dt.isScaleRequired();
					if (b != null && b.booleanValue()) {
						scaleField.setText(String.valueOf(ctn.getScale()));
					} else {
						scaleField.setText("");
					}
					scaleField.setEditable(false);
					break;
				}
			}
		}
	}
	
	private JPanel createButtonPanel()
	{
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		
		previousButton = new JButton("Previous");
		previousButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						setCurrentTab(getCurrentTab() - 1);
					}
				}
			);
		previousButton.setMnemonic('P');
		panel1.add(previousButton);
		
		panel1.add(new HorizontalStrut(5));
		
		nextButton = new JButton("Next");
		nextButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						if (validateTab(getCurrentTab())) {
							setCurrentTab(getCurrentTab() + 1);
						}
					}
				}
			);
		nextButton.setMnemonic('N');
		panel1.add(nextButton);

		return panel1;
	}

	private JPanel createTab0()
	{
		GridBagConstraints constraints;
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		panel1.add(new JLabel(tabLabels[0][1]), BorderLayout.NORTH);
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel1.add(panel2, BorderLayout.CENTER);
		
		constraints = new GridBagConstraints
		(
			0, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(20, 0, 5, 0),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Choose a file to import the data from:"),
				constraints
			);
		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new BasicFileFilter("csv", "Coma Seperated Values"));
		fileChooser.setControlButtonsAreShown(false);
		constraints = new GridBagConstraints
		(
			0, 1,							// gridx, gridy
			1, 4,							// gridwidth, gridheight
			.5, .5,							// weightx, weighty
			GridBagConstraints.CENTER,		// anchor
			GridBagConstraints.BOTH,		// fill
			new Insets(5, 0, 5, 0),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(fileChooser, constraints);
		
		constraints = new GridBagConstraints
		(
			0, 5,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(10, 0, 5, 0),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Choose an existing table, or provide a name for a new table:"),
				constraints
			);
		tableCombo = new JComboBox(createDBTablesComboModel());
		tableCombo.setEditable(true);
		constraints = new GridBagConstraints
		(
			0, 6,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			.5, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,	// fill
			new Insets(5, 10, 0, 10),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(tableCombo, constraints);
		
		return panel1;
	}
	
	private JPanel createTab1()
	{
		GridBagConstraints constraints;
		columnSelectionListener = new ColumnSelectionListener();
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		panel1.add(new JLabel(tabLabels[1][1]), BorderLayout.NORTH);
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel1.add(panel2, BorderLayout.CENTER);
		
		// ROW 1
		
		constraints = new GridBagConstraints
		(
			0, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(10, 0, 5, 5),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Column:"),
				constraints
			);
		columnSpinner = new JSpinner(new SpinnerNumberModel());
		columnSpinner.addChangeListener(columnSelectionListener);
		constraints = new GridBagConstraints
		(
			1, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(10, 5, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(columnSpinner, constraints);
		
		skipColumnCheck = new JCheckBox("Skip");
		constraints = new GridBagConstraints
		(
			2, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(10, 5, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(skipColumnCheck, constraints);
		
		constraints = new GridBagConstraints
		(
			3, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			.25, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,	// fill
			new Insets(10, 5, 5, 0),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(new HorizontalGlue(), constraints);
		
		constraints = new GridBagConstraints
		(
			4, 0,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(10, 5, 5, 5),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Name:"),
				constraints
			);
		columnNameCombo = new JComboBox();
		columnNameCombo.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						columnNameSelected();
					}
				}
			);
		constraints = new GridBagConstraints
		(
			5, 0,							// gridx, gridy
			4, 1,							// gridwidth, gridheight
			.5, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,  // fill
			new Insets(10, 5, 5, 0),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(columnNameCombo, constraints);
		
		// ROW 2
		
		constraints = new GridBagConstraints
		(
			0, 1,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(5, 0, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Data Type:"),
				constraints
			);
		dataTypeCombo = new JComboBox(createDataTypesComboModel());
		dataTypeCombo.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						dataTypeSelected();
					}
				}
			);
		dataTypeCombo.setEditable(false);
		constraints = new GridBagConstraints
		(
			1, 1,							// gridx, gridy
			3, 1,							// gridwidth, gridheight
			.5, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,  // fill
			new Insets(5, 5, 5, 5),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(dataTypeCombo, constraints);
		
		constraints = new GridBagConstraints
		(
			4, 1,							// gridx, gridy
			2, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(5, 5, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Precision / Max Size:"),
				constraints
			);
		precisionField = new JTextField();
		precisionField.setEditable(false);
		constraints = new GridBagConstraints
		(
			6, 1,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			.25, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,  // fill
			new Insets(5, 5, 5, 5),		// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(precisionField, constraints);
		
		constraints = new GridBagConstraints
		(
			7, 1,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(5, 5, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(
				new JLabel("Scale:"),
				constraints
			);
		scaleField = new JTextField();
		scaleField.setEditable(false);
		constraints = new GridBagConstraints
		(
			8, 1,							// gridx, gridy
			1, 1,							// gridwidth, gridheight
			.25, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.HORIZONTAL,  // fill
			new Insets(5, 5, 5, 0),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(scaleField, constraints);
		
		constraints = new GridBagConstraints
		(
			0, 2,							// gridx, gridy
			4, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(5, 0, 5, 5),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(new JLabel("Sample Data:"), constraints);
		
		constraints = new GridBagConstraints
		(
			4, 2,							// gridx, gridy
			5, 1,							// gridwidth, gridheight
			0, 0,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.NONE,		// fill
			new Insets(5, 5, 5, 0),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		firstRowHeaderBox = new JCheckBox("The first row is column headers, not data.");
		panel2.add(firstRowHeaderBox, constraints);
		
		sampleTable = new JTable(new SampleDataTableModel());
		sampleTable.setEnabled(false);
		sampleTable.getSelectionModel().addListSelectionListener(columnSelectionListener);
		sampleTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sampleTable.setColumnSelectionAllowed(true);
		sampleTable.setRowSelectionAllowed(false);
		constraints = new GridBagConstraints
		(
			0, 3,							// gridx, gridy
			9, 4,							// gridwidth, gridheight
			.5, .5,							// weightx, weighty
			GridBagConstraints.NORTHWEST,	// anchor
			GridBagConstraints.BOTH,		// fill
			new Insets(5, 0, 0, 0),			// insets (t,l,b,r)
			0, 0							// padx pady
		);
		panel2.add(new JScrollPane(sampleTable), constraints);
		
		return panel1;
	}
	
	private JPanel createTab2()
	{
		JPanel panel1 = new JPanel(new BorderLayout(10, 10));
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel1.add(new JLabel(tabLabels[2][1]), BorderLayout.NORTH);
		
		JPanel panel2 = new JPanel(new BorderLayout(10, 10));
		resultsArea = new JTextArea();
		resultsArea.setEditable(false);
		JScrollPane sp = new JScrollPane(resultsArea);
		panel2.add(sp, BorderLayout.CENTER);
		progressBar = new JProgressBar();
		panel2.add(progressBar, BorderLayout.SOUTH);
		panel1.add(panel2, BorderLayout.CENTER);
		
		panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel1.add(panel2, BorderLayout.SOUTH);
		executeButton = new JButton("Import Data");
		executeButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						executeImport();
					}
				}
			);
		executeButton.setMnemonic('I');
		panel2.add(executeButton);
		
		return panel1;
	}
	
	private ComboBoxModel createDataTypesComboModel()
	{
		try {
			DataType[] dt = provider.getDataTypes();
			ExtVector ev = new ExtVector(dt.length);
			// Filter out "other" types.
			for (int i = 0; i < dt.length; i++) {
				if (dt[i].getJDBCType() < Types.OTHER) {
					ev.add(dt[i]);
				}
			}
			return new DefaultComboBoxModel(ev.toArray());
		} catch (Exception e) {
			Debug.log(e);
			WrappedOptionPane.showWrappedMessageDialog(
					this,
					"There was an error obtaining a list of tables.",
					"Error Listing Tables",
					WrappedOptionPane.INFORMATION_MESSAGE
				);
			return new DefaultComboBoxModel();
		}
	}
	
	private ComboBoxModel createDBTablesComboModel()
	{
		try {
			// Get a list of tables.
			String cat;
			String schema;
			Connection con = provider.getConnection();
			DatabaseMetaData dmd = con.getMetaData();
			String tblTypes[] = {"TABLE"};
			ResultSet rs = dmd.getTables(null , null, "%", tblTypes);
			ExtVector v = new ExtVector();
			while (rs.next()) {
				cat = rs.getString("TABLE_CAT");
				if (rs.wasNull()) {
					cat = null;
				}
				schema = rs.getString("TABLE_SCHEM");
				if (rs.wasNull()) {
					schema = null;
				}
				v.add(new TableTreeNode(con, cat, schema, rs.getString("TABLE_NAME")));
			}
			rs.close();
			TableTreeNode[] items = new TableTreeNode[v.size()];
			v.toArray(items);
			return new DefaultComboBoxModel(items);
		} catch (Exception e) {
			Debug.log(e);
			WrappedOptionPane.showWrappedMessageDialog(
					this,
					"There was an error obtaining a list of tables.",
					"Error Listing Tables",
					WrappedOptionPane.INFORMATION_MESSAGE
				);
			return new DefaultComboBoxModel();
		}
	}
	
	private void dataTypeSelected()
	{
		DataType dt = (DataType)dataTypeCombo.getSelectedItem();
		Boolean b = dt.isPrecisionRequired();
		precisionField.setEditable(b != null && b.booleanValue());
		b = dt.isScaleRequired();
		scaleField.setEditable(b != null && b.booleanValue());
	}
	
	public void executeImport()
	{
		try {
			FileInputStream fis = new FileInputStream(selectedFile);
			boolean b = !(selectedTable instanceof TableTreeNode);
			dataImport.importData(
					provider, selectedTable.toString(), b,
					columns, fis, firstRowHeaderBox.isSelected()
				);
		} catch (IOException ioe) {
			WrappedOptionPane.showWrappedMessageDialog(
					this,
					"There was an error reading data from the file " +
					selectedFile.getAbsolutePath() + '.',
					"Cannot Read Data",
					WrappedOptionPane.INFORMATION_MESSAGE
				);
		}
	}
	
	public void initGUI()
	{
		JPanel panel;
		getContentPane().setLayout(new BorderLayout());
		stepsTab = new JTabbedPane();
		stepsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		getContentPane().add(stepsTab, BorderLayout.CENTER);
		
		panel = createTab0();
		stepsTab.add(panel, 0);
		stepsTab.setTitleAt(0, tabLabels[0][0]);
		stepsTab.setMnemonicAt(0, tabMnemonics[0]);
		
		panel = createTab1();
		stepsTab.add(panel, 1);
		stepsTab.setTitleAt(1, tabLabels[1][0]);
		stepsTab.setMnemonicAt(1, tabMnemonics[1]);
		
		panel = createTab2();
		stepsTab.add(panel, 2);
		stepsTab.setTitleAt(2, tabLabels[2][0]);
		stepsTab.setMnemonicAt(2, tabMnemonics[2]);
		
		panel = createButtonPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		stepsTab.addChangeListener(new ChangeListener()
				{
					private boolean validationCall = false;
					public void stateChanged(ChangeEvent event)
					{
						if (!validationCall) {
							// Loop through each tab from current to the
							// one prior to where we are going.  Validate
							// each one.
							for (
									int i = getCurrentTab();
									i < stepsTab.getSelectedIndex();
									i++
								)
							{
								if (! validateTab(i)) {
									// Select the invalid tab.  This event
									// will be fired when we do, so make sure
									// we don't do all this again.
									validationCall = true;
									stepsTab.setSelectedIndex(i);
									validationCall = false;
									return;
								}
							}
							// We found no validation errors, so update
							// our internal position index and the UI.
							currentTab = stepsTab.getSelectedIndex();
							updateNavButtons();
						}
					}
				}
			);

		// Size and center
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle myBounds = getBounds();
		myBounds.x = screenSize.width / 2 - myBounds.width / 2;
		myBounds.y = screenSize.height / 2 - myBounds.height / 2;
		setBounds(myBounds);
	}
	
	private void populateTab1()
	{
		// Samples
		File f = fileChooser.getSelectedFile();
		if (! f.equals(selectedFile)) {
			try {
				selectedFile = f;
				//TODO Right now this is hard-coded.  Later we need to try to
				//     determine which importer to use.
				dataImport = new CSVImport();
				dataImport.addImportListener(
						new ImportListener()
						{
							public void importStatusChanged(ImportEvent ie)
							{
								int i = ie.getEventType();
								switch (i) {
									case ImportEvent.EVENT_TYPE_INITIALIZING:
										break;
									case ImportEvent.EVENT_TYPE_MESSAGE:
										println(ie.getMessage());
										break;
									case ImportEvent.EVENT_TYPE_PROGRESSED:
										break;
									case ImportEvent.EVENT_TYPE_STARTED:
										progressBar.setIndeterminate(true);
										setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
										break;
									case ImportEvent.EVENT_TYPE_STOPPED:
										progressBar.setIndeterminate(false);
										setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										break;
								}
							}
							
							private void println(String msg)
							{
								if (resultsArea.getText().length() > 0) {
									resultsArea.append("\n");
								}
								resultsArea.append(msg);
							}
						}
					);
				FileInputStream fis = new FileInputStream(f);
				String[][] data = dataImport.sampleData(fis);
				columns = new ImportColumn[data[0].length];
				((SampleDataTableModel)sampleTable.getModel()).setData(data);
				sampleTable.setColumnSelectionInterval(0, 0);
				SpinnerNumberModel snm = new SpinnerNumberModel(1, 1, data[0].length, 1);
				columnSpinner.setModel(snm);
			} catch (IOException ioe) {
				WrappedOptionPane.showWrappedMessageDialog(
						this,
						"There was an error reading data from the file " + f.getAbsolutePath() + '.',
						"Cannot Read Data",
						WrappedOptionPane.INFORMATION_MESSAGE
					);
			}
		}
		
		// Database table
		Object obj = tableCombo.getSelectedItem();
		if (! obj.equals(selectedTable)) {
			selectedTable = obj;
			if (obj instanceof TableTreeNode) {
				TableTreeNode ttn = (TableTreeNode)obj;
				columnNameCombo.setEditable(false);
				dataTypeCombo.setEnabled(false);
				// Get a list of columns to insert data into.
				try {
					Connection con = provider.getConnection();
					DatabaseMetaData dmd = con.getMetaData();
					ResultSet rs = dmd.getColumns(
							ttn.getCatalog(),
							ttn.getSchema(),
							ttn.getName(), null
						);
					ExtVector v = new ExtVector();
					while (rs.next()) {
						v.add(new ColumnTreeNode(null,
						                         rs.getString("COLUMN_NAME"),
						                         rs.getInt("DATA_TYPE"),
						                         rs.getString("TYPE_NAME"),
						                         rs.getInt("COLUMN_SIZE"),
						                         rs.getInt("DECIMAL_DIGITS"),
						                         (rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable)));
					}
					rs.close();

					columnNameCombo.setModel(new DefaultComboBoxModel(v.toArray()));
				} catch (Exception e) {
					WrappedOptionPane.showWrappedMessageDialog(
							this,
							"There was an error loading columns for the table " + selectedTable.toString() + '.',
							"Cannot Load Column List",
							WrappedOptionPane.INFORMATION_MESSAGE
						);
				}
			} else {
				// No columns to list.
				columnNameCombo.setEditable(true);
				dataTypeCombo.setEnabled(true);
				columnNameCombo.setModel(new DefaultComboBoxModel());
			}
		}
	}
	
	private void updateNavButtons()
	{
		previousButton.setEnabled(getCurrentTab() > 0);
		nextButton.setEnabled(getCurrentTab() < tabMnemonics.length - 1);
	}
	
	private boolean validateTab(int tabIndex)
	{
		switch (tabIndex) {
			case 0:
				return validateTab0();
			case 1:
				return validateTab1();
			case 2:
				return validateTab2();
			default:
				return false;
		}
	}
	
	private boolean validateTab0()
	{
		Message errorMessage = new Message(' ');
		
		// We must have a file.
		File f = fileChooser.getSelectedFile();
		if (f == null || (! f.exists())) {
			errorMessage.appendnl(
					"Select a file from which data is to be imported."
				);
		} else if (! f.isFile()) {
			errorMessage.appendnl(
					"Please select a file. " + f.getAbsolutePath() +
					" is not a file."
				);
		} else if (! f.canRead()) {
			errorMessage.appendnl(
					"The file " + f.getAbsolutePath() +
					" cannot be read because of file permissions."
				);
		}
		
		// We must have a table.
		Object o = tableCombo.getSelectedItem();
		if (o == null || o.toString().trim().length() == 0) {
			errorMessage.appendnl(
					"Please select an existing table, or provide a new name for the table into which the data will be inserted."
				);
		}
		
		if (errorMessage.getLength() > 0) {
			WrappedOptionPane.showWrappedMessageDialog(
					this, errorMessage.toString(), "Cannot Proceed",
					WrappedOptionPane.INFORMATION_MESSAGE
				);
			return false;
		} else {
			return true;
		}		
	}
	
	private boolean validateTab1()
	{
		// Make sure the column on which the user sits is saved.
		columnSelectionListener.saveCurrentColumnInfo();
		
		// We must make sure that there is at least one column to import.
		boolean returnValue = false;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] != null) {
				returnValue = true; // We have at least one column
				break;
			}
		}
		if (! returnValue) {
			WrappedOptionPane.showWrappedMessageDialog(
					this,
					"Define at least one column into which the data will be imported.",
					"Cannot Proceed",
					WrappedOptionPane.INFORMATION_MESSAGE
				);
		}
		return returnValue;
	}
	
	private boolean validateTab2()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	class ColumnSelectionListener extends Object
		implements ChangeListener, ListSelectionListener
	{
		boolean adjusting;
		private int currentColumn = 0;
		private NumberFormat integerFormat = NumberFormat.getIntegerInstance(); 
		
		private boolean changeToColumn(int columnIndex)
		{
			// Save previous column info
			if (saveCurrentColumnInfo()) {
			
				// Set up next column info
				currentColumn = columnIndex;
				skipColumnCheck.setSelected(columns[currentColumn] == null);
				if (columns[currentColumn] == null) {
					if (firstRowHeaderBox.isSelected()) {
						ComboBoxModel model = columnNameCombo.getModel();
						ColumnTreeNode node;
						String fooName = ((SampleDataTableModel)sampleTable.getModel()).data[0][currentColumn];
						if (model.getSize() == 0) {
							// No existing columns
							columnNameCombo.setSelectedItem(fooName);
						} else {
							for (int i = 0; i < model.getSize(); i++) {
								node = (ColumnTreeNode)model.getElementAt(i);
								if (node.toString().compareToIgnoreCase(fooName) == 0) {
									columnNameCombo.setSelectedIndex(i);
									columnNameSelected();
									break;
								}
							}
						}
					}
				} else {
					String s = columns[currentColumn].getColumnName();
					ComboBoxModel cbo = columnNameCombo.getModel();
					if (cbo.getSize() > 0) {
						for (int i = 0; i < cbo.getSize(); i++) {
							if (cbo.getElementAt(i).toString().equals(s)) {
								columnNameCombo.setSelectedIndex(i);
								columnNameSelected();
								break;
							}
						}
					} else {
						// Custom table
						columnNameCombo.setSelectedItem(columns[currentColumn].getColumnName());
						// Locate the type
						DefaultComboBoxModel dcbm = (DefaultComboBoxModel)dataTypeCombo.getModel();
						DataType dt;
						for (int i = 0; i < dcbm.getSize(); i++) {
							dt = (DataType)dcbm.getElementAt(i);
							if (
									dt.getJDBCType() == columns[currentColumn].getDataType().getJDBCType() &&
									dt.getTypeName().equals(columns[currentColumn].getDataType().getTypeName())
								)
							{
								dataTypeCombo.setSelectedIndex(i);
								Boolean b = dt.isPrecisionRequired();
								if (b != null && b.booleanValue()) {
									precisionField.setText(String.valueOf(columns[currentColumn].getPrecision()));
								} else {
									precisionField.setText("");
								}
								precisionField.setEditable(false);
								b = dt.isScaleRequired();
								if (b != null && b.booleanValue()) {
									scaleField.setText(String.valueOf(columns[currentColumn].getScale()));
								} else {
									scaleField.setText("");
								}
								scaleField.setEditable(false);
								break;
							}
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}
		
		boolean saveCurrentColumnInfo()
		{
			if (columns != null) {
				if (skipColumnCheck.isSelected()) {
					columns[currentColumn] = null;
				} else {
					ImportColumn column = new ImportColumn();
					column.setColumnName(columnNameCombo.getSelectedItem().toString());
					DataType dt = (DataType)dataTypeCombo.getSelectedItem();
					column.setDataType(dt);
					Boolean b = dt.isPrecisionRequired();
					if (b != null && b.booleanValue()) {
						int i;
						try {
							i = integerFormat.parse(precisionField.getText()).intValue();
							if (i < 1) {
								throw new ParseException("", i);
							}
							column.setPrecision(i);
						} catch (ParseException pe) {
							WrappedOptionPane.showMessageDialog(
									me,
									"Please provide a precision/length for the column.", 
									"Invalid Precision/Length",
									WrappedOptionPane.INFORMATION_MESSAGE
								);
							return false;
						}
						b = dt.isScaleRequired();
						if (b != null && b.booleanValue()) {
							try {
								i = integerFormat.parse(scaleField.getText()).intValue();
								if (i < 1) {
									throw new ParseException("", i);
								}
								column.setScale(i);
							} catch (ParseException pe) {
								WrappedOptionPane.showMessageDialog(
										me,
										"Please provide a scale for the column.", 
										"Invalid Scale",
										WrappedOptionPane.INFORMATION_MESSAGE
									);
								return false;
							}						
						}
					}
					columns[currentColumn] = column;
				}
			}
			return true;
		}
		
		// For the JSpinner
		public void stateChanged(ChangeEvent event)
		{
			if (! adjusting) {
				adjusting = true;
				int i = ((Integer)columnSpinner.getModel().getValue()).intValue() - 1;
				if (changeToColumn(i))
					sampleTable.setColumnSelectionInterval(i, i);
				else
					columnSpinner.setValue(new Integer(currentColumn));
				adjusting = false;
			}
		}

		// For the table
		public void valueChanged(ListSelectionEvent event)
		{
			if (! adjusting) {
				adjusting = true;
				int i = sampleTable.getSelectedColumn();
				if (i > -1) {
					if (changeToColumn(i)) {
						columnSpinner.setValue(new Integer(i + 1));
					} else {
						sampleTable.setColumnSelectionInterval(
								currentColumn, currentColumn
							);
					}
				}
				adjusting = false;
			}
		}
	}

	class SampleDataTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;

		// CONSTRUCTORS
		
		SampleDataTableModel()
		{
			this(null);
		}
		
		SampleDataTableModel(String[][] data)
		{
			super();
			this.data = data;
		}
		
		// MEMBERS
		
		private String[][] data;
		
		public String getColumnName(int column)
		{
			return String.valueOf(column + 1);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}

		public int getColumnCount()
		{
			if (data == null || data.length == 0) {
				return 0;
			} else {
				return data[0].length;
			}
		}
		
		public String[][] getData()
		{
			return data;
		}
		
		public void setData(String[][] data)
		{
			int oldCount = getColumnCount();
			this.data = data;
			if (oldCount == getColumnCount()) {
				super.fireTableDataChanged();
			} else {
				super.fireTableStructureChanged();
			}
		}

		public int getRowCount()
		{
			if (data == null) {
				return 0;
			} else {
				return data.length;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return data[rowIndex][columnIndex];
		}
		
		// METHODS
	}
}
