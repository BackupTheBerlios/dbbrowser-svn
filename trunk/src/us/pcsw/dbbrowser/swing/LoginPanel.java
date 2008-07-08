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

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.pcsw.dbbrowser.ConnectionProviderModel;
import us.pcsw.dbbrowser.Preferences;
import us.pcsw.dbbrowser.cp.BooleanConnectionParameter;
import us.pcsw.dbbrowser.cp.ConnectionParameter;
import us.pcsw.dbbrowser.cp.ConnectionProvider;
import us.pcsw.dbbrowser.cp.IntegerConnectionParameter;
import us.pcsw.dbbrowser.cp.PicklistConnectionParameter;
import us.pcsw.dbbrowser.cp.StringConnectionParameter;
import us.pcsw.swing.BasicFileFilter;
import us.pcsw.swing.RegExpFormatter;

/**
 *  us.pcsw.dbbrowser.LoginPanel
 *
 * This class provids a modal form for obtaining the user's ID and password as
 * well as the hostname of the database server and the database to connect to.
 * Upon construction, this class must be passed java.util.Properties instance
 * in which to strore the data.  If the properties already have value in the
 * java.util.Properties instance, they are used as defaults.  If the user
 * provides information for these fields, it is stored in the
 * java.util.Properties instance.  If the user selects cancel or closes the
 * frame using the window controls, the data in the java.util.Properties will
 * be unchanged.
 * <P>
 * The indexes used for these values are:<BR>
 * <B>cancelled</B> If the cancel key/value pair exists, the user canceled the
 * login operation.
 * <B>dbname</B> The name of the database to use.<BR>
 * <B>password</B> The user's password.<BR>
 * <B>server</B> The hostname of the database server to connect to.<BR>
 * <B>user</B> The user's login id.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/10/2001 This class was created.</LI>
 * <LI>07/25/2001 Support for selecting the database type (connection provider)
 *                was added.  The GUI was redesigned.</LI>
 * <LI>10/18/2002 The GUI was redesigned again.  The dialog was changed to use
 *                the new ConnectionParameter object so that the parameters
 *                portion of the dialog can be built dynamically based on the
 *                needs of the connection provider. PAC </LI>
 * <LI>10/22/2003 Open button and related code was added so that it is possible
 *                to load connection info from a file. <BR/> Work began on
 *                making this an embedable UI object rather than a dialog.
 *                This class's super was changed from JDialog to JPanel and
 *                renamed to LoginPanel.  PAC </LI>
 * <LI>04/05/2003 Added the method setDefaultButtonForRootPanel(JRootPane) so
 *                that the parent object can choose to have this panel's
 *                connect button as the default button.  PAC <LI>
 * <LI>01/02/2004 Changed the caption of the parameter load button from "Open"
 *                to "Load".  Also, I added a tooltip to the button.  PAC</LI>
 * <LI>04/27/2005 Added support for the PicklistConnectionParameter.  PAC</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
final class LoginPanel
	extends JPanel
	implements ActionListener, ListSelectionListener
{
	private static final Logger logger = LoggerFactory.getLogger(LoginPanel.class);
	private static final long serialVersionUID = 1L;

	public static int ACTION_EVENT_CANCEL = 0;
	public static int ACTION_EVENT_SELECTION = 1;
	
	private JButton cancelButton = null;
    private boolean cancelled = false;
    private JFileChooser fileChooser = null;
    private JButton okButton = null;
    private JButton openButton = null;
    private Vector listeners = null;    
    private JList providerList = null;
    private JPanel parmsPanel = null;
    private ConnectionProvider provider = null;
    private ConnectionProviderModel providerModel = null;

    /**
     * Initialize the JDialog.
     * @param provider The provider to display initially, or null.
     * @param enableCancel If true, the cancel button is enabled.
     */
    LoginPanel
    	(ConnectionProvider provider, boolean enableCancel)
	{
		super(new BorderLayout());
	
		this.providerModel = Preferences.getConnectionProviderModel();

		// Create the Interface.  On the left is a list of providers.  On the
		// right are parameters for the selected parameter.  Buttons reside on
		// the bottom of the Dialog.
		JLabel label;
		JPanel panel;
		JPanel panel2;
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		// Provider list on the left.
		panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 1));
		label = new JLabel("Providers");
		panel2.add(label, BorderLayout.NORTH);
		providerList = new JList(providerModel);
		providerList.getSelectionModel().setSelectionMode
		    (ListSelectionModel.SINGLE_SELECTION);
		JScrollPane jsp = new JScrollPane(providerList);
		providerList.addListSelectionListener(this);
		panel2.add(jsp, BorderLayout.CENTER);
		panel.add(panel2);
		
		// Parameters panel on the right.
		panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 1, 4));
		label = new JLabel("Database Connection Parameters");
		panel2.add(label, BorderLayout.NORTH);
		parmsPanel = new JPanel();
		panel2.add(parmsPanel, BorderLayout.CENTER);
		panel.add(panel2);
		
		add(panel, BorderLayout.CENTER);
		
		// Buttons at the bottom.
		JPanel buttonPanel = new JPanel(new BorderLayout());
		panel = new JPanel();
		openButton = new JButton("Load");
		openButton.setMnemonic('o');
		openButton.setToolTipText("Load saved connection parameters");
		openButton.addActionListener(this);
		panel.add(openButton);
		buttonPanel.add(panel, BorderLayout.WEST);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("Connect");
		okButton.setMnemonic('n');
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		panel.add(okButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic('a');
		cancelButton.addActionListener(this);
		cancelButton.setEnabled(enableCancel);
		panel.add(cancelButton);
		
		buttonPanel.add(panel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		// Load pre-existing provider.
		if (provider != null) {
			setConnectionProvider(provider);
		}
    }

	/**
	 * The event handler for all menu items (except look and feel) and buttons.
	 */
	public void actionPerformed(ActionEvent e)
	{
		try {
			Object source = e.getSource();
			// Process action events from buttons.
			if (source.equals(okButton)) {
				processOK();
			} else if (source.equals(openButton)) {
				processOpen();
			} else if (source.equals(cancelButton)) {
				processCancel();
			}
		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Adds a listener to the list of those to be notified of events thrown
	 * by this class.
	 * @param listener The listener.
	 */
	public void addActionListener(ActionListener listener)
	{
		if (listeners == null) {
			listeners = new Vector(1, 1);
		}
		if (! listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

    boolean cancelled()
    {
		return cancelled;
    }

	/**
	 * Gets an instance of ConnectionProvider which contains information to
	 * use when connecting to the DB.
	 */
	public ConnectionProvider getConnectionProvider()
	{
		return provider;
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
		logger.error("unexpected error", exception);
	}

	/**
	 * Notifies all listeners of an event.
	 * @param ae The event to notify the listeners of.
	 */
	private void notifyActionListeners(ActionEvent ae)
	{
		if (listeners != null) {
			// Clone the vector because we do not want to run into trouble if a
			// listener adds or removes itself while we are iterating through the
			// enumeration. 
			Vector clone = (Vector)listeners.clone();
			ActionListener listener;
			Enumeration e = clone.elements();
			while (e.hasMoreElements()) {
				listener = (ActionListener)e.nextElement();
				listener.actionPerformed(ae);
			}
		}
	}

	private void populateParmsPanel()
	{
		if (providerList.getSelectedValue() != null) {
			Enumeration e = null;
			JComponent component = null;
			JLabel label = null;
			ConnectionParameter cParm = null;
			String s = null;
			
			s = providerList.getSelectedValue().toString();
			s =	providerModel.getConnectionProviderClass(s);
			if (provider == null ||
			    (! s.equals(provider.getClass().getName())))
			{
				// This parameter class is different than the previous.
				// Rebuild the parameters panel.
				parmsPanel.removeAll();

				// Create the parameter fields for user input.
				try {
					provider =
						(ConnectionProvider)(Class.forName(s).newInstance());
				} catch (Exception exc) {
					s = "Unable to load the Connection Provider class " + s;
					JOptionPane.showMessageDialog(null, s, "Class Not Found",
					                              JOptionPane.ERROR_MESSAGE);
					logger.error("Error loading connection provider", exc);
					return;
				}

				GridBagLayout gridBag = new GridBagLayout();
				parmsPanel.setLayout(gridBag);
				GridBagConstraints compConstraints = null;
				GridBagConstraints labelConstraints = null;
				Insets insets = new Insets(2,2,2,2);
				int row = 0;
				e = provider.getConnectionParameters();
				while (e.hasMoreElements()) {
					compConstraints = null;
					label = null;
					labelConstraints = null;
					cParm = (ConnectionParameter)e.nextElement();
					// Create the field component based on the ConnectionParameter type.
					if (cParm instanceof BooleanConnectionParameter) {
						BooleanConnectionParameter bcp = (BooleanConnectionParameter)cParm;
						JCheckBox jcb = new JCheckBox();
						if (bcp.getValue() != null) {
							jcb.setSelected(((Boolean)bcp.getValue()).booleanValue());
						}
						component = jcb;
					} else if (cParm instanceof StringConnectionParameter) {
						StringConnectionParameter scp = (StringConnectionParameter)cParm;
						JTextField jtf = null;
						if (scp.getMaxLength() > 0) {
							if (scp.isSecret()) {
								jtf = new JPasswordField(scp.getMaxLength());
							} else {
								jtf = new JTextField(scp.getMaxLength());
							}
						} else {
							if (scp.isSecret()) {
								jtf = new JPasswordField();
							} else {
								jtf = new JTextField();
							}
						}
						if (scp.getValue() != null) {
							jtf.setText(scp.getValue().toString());
						}
						component = jtf;
					} else if (cParm instanceof IntegerConnectionParameter) {
						IntegerConnectionParameter icp = (IntegerConnectionParameter)cParm;
						RegExpFormatter ref = new RegExpFormatter();
						ref.setPattern(Pattern.compile("\\d{1,10}?"));
						JFormattedTextField jftf = new JFormattedTextField(ref);
						if (icp.getValue() != null) {
							jftf.setText(icp.getValue().toString());
						}
						component = jftf;
					} else if (cParm instanceof PicklistConnectionParameter) {
						PicklistConnectionParameter parm = (PicklistConnectionParameter)cParm;
						DefaultComboBoxModel model = new DefaultComboBoxModel(parm.getPickList());
						if (parm.isMultiSelectAllowed()) {
							JList list = new JList(model);
							list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
							component = new JScrollPane(list);
							compConstraints = 
							    new GridBagConstraints(1, row,        // gridx, gridy 
										   3, 3,        // gridWidth, gridHeight
										   2, 2,        // weightx, weghty
										   GridBagConstraints.WEST, // anchor
										   GridBagConstraints.BOTH, // fill
										   insets,      // insets
										   0, 0);       // ipadx, ipady
							row += 3;
							String[] values = (String[])parm.getValue();
							if (values != null && values.length > 0) {
								int[] selectedIndices = new int[values.length];
								for (int i = 0; i < values.length; i++) {
									selectedIndices[i] = model.getIndexOf(values[i]);
								}
								list.setSelectedIndices(selectedIndices);
							}
						} else {
							component = new JComboBox(model);
							((JComboBox)component).setEditable(false);
							Object[] values = (Object[])parm.getValue();
							if (values != null && values.length == 1) {
								((JComboBox)component).setSelectedIndex(model.getIndexOf(values[0]));
							}							
						}
					} else {
						continue;
					}
					// Add the widgets to the panel
					
					// Label, column 1
					if (label == null) {
						label = new JLabel(((ConnectionParameter)cParm).getName());
					}
					if (labelConstraints == null) {
						labelConstraints = 
						    new GridBagConstraints(0, row,        // gridx, gridy 
									   1, 1,        // gridWidth, gridHeight
									   1, 1,        // weightx, weghty
									   GridBagConstraints.WEST, // anchor
									   GridBagConstraints.NONE, // fill
									   insets,      // insets
									   0, 0);       // ipadx, ipady
					}
					parmsPanel.add(label, labelConstraints);
					// Field component, columns 2,3,4.
					component.setName(cParm.getName());
					if (compConstraints == null) {
						compConstraints = 
						    new GridBagConstraints(1, row++,        // gridx, gridy 
									   3, 1,        // gridWidth, gridHeight
									   2, 1,        // weightx, weghty
									   GridBagConstraints.WEST, // anchor
									   GridBagConstraints.HORIZONTAL, // fill
									   insets,      // insets
									   0, 0);       // ipadx, ipady
					}
					parmsPanel.add(component, compConstraints);
				} // end while
			} // end provider == null
			if (! okButton.isEnabled()) {
				okButton.setEnabled(true);
			}
		} // end providerList.getSelectedValue() != null

		// Mark this component as "dirty".
		repaint();
		
		// Find the parent component and make it repaint.
		java.awt.Component com = this, last = this;
		while (com != null) {
			last = com;
			com = last.getParent();
		}
		last.repaint();
	}

	private void processCancel()
	{
		cancelled = true;
		notifyActionListeners(new ActionEvent(this, ACTION_EVENT_CANCEL, "Cancel"));
	}

    private void processOK()
    {
		try {
			// Populate the provider from parameters	
			Object o = null;
			Component[] components = parmsPanel.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof JCheckBox) {
					o = new Boolean(((JCheckBox)components[i]).isSelected());
				} else if (components[i] instanceof JTextField) {
					o = ((JTextField)components[i]).getText();
				} else if (components[i] instanceof JComboBox) {
					o = ((JComboBox)components[i]).getSelectedObjects();
				} else if (components[i] instanceof JList) {
					o = ((JList)components[i]).getSelectedValues();
				} else {
					continue;  // Continue through the for loop, this is
					            // probably a label.
				}
				provider.getConnectionParameter(components[i].getName()).setValue(o);
			}
			
			cancelled = false;
			notifyActionListeners(new ActionEvent(this, ACTION_EVENT_SELECTION, "Selection Made"));
		} catch (IllegalArgumentException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage(),
			                              "Invalid Login Info",
			                              JOptionPane.WARNING_MESSAGE);
		} catch (Throwable t) {
			handleException(t);
		}
    }

	/**
	 * Response to the Load button.  Prompt the user for the name of a
	 * connection file and loads the saved connection parameters from it.
	 */
	private void processOpen()
	{
		if (fileChooser == null) {
			fileChooser = new JFileChooser("Open DBBrowser Connection File");
			fileChooser.setFileFilter(new BasicFileFilter("dbc", "DBBrowser Connection Parameters"));			
		}
		// Get the path/name of the file to save the file to.
		File file = null;
		int response = JFileChooser.APPROVE_OPTION;
		while (response != JFileChooser.CANCEL_OPTION && file == null) {
			response = fileChooser.showOpenDialog(this);
			if (response == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				loadConnectionParameters(file);
			}
		}
	}
	
	/**
	 * Loads the saved connection parameters from a connection file.
	 * @param file The connection file.
	 * @return True if the load was successfull, else False.
	 */
	public boolean loadConnectionParameters(File file)
	{
		// Assume the wost
		boolean retVal = false;
		
		if (file.exists()) {
			try {
				// Load the connection provider and params.
				ConnectionProvider cp = ConnectionProvider.load(file);
				setConnectionProvider(cp);
				retVal = true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog
					(this,
				     "An unexpected error occurred while loading saved " +
				     "connection parameters from file " + file.toString() +
                     ".  " + e.getMessage(),
					 "Unexpected Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog
				(this,
                 "The file " + file.toString() + " does not exist.  " +
                 "Unable to load saved connection parameters.",
				 "File Does Not Exist", JOptionPane.ERROR_MESSAGE);
		}
		return retVal;
	}
	
	/**
	 * Removes the action listener from those that should be notified of
	 * events.
	 * @param listener The listener.
	 */
	public void removeActionListener(ActionListener listener)
	{
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
	public void setConnectionProvider(ConnectionProvider cp)
	{
		Object selected = null;
		int index;
		int size = providerModel.getRowCount();
		
		// Find the item in the connection provider list to select (showing
		// parameters).
		String className = cp.getClass().getName();
		for (index = 0; index < size; index++) {
			if (providerModel.getValueAt(index, 1).equals(className)) {
				selected = providerModel.getElementAt(index);
				break; 
			}
		}

		if (selected == null) {
			// The item did not exist in the list!  Add one.
			index = providerModel.addConnectionProvider(className, className);
		}

		// Select the item in the list and populate the parameters fields.
		providerList.setSelectedIndex(index);
		
		// Build the parameter panel.
		this.provider = null;
		populateParmsPanel();

		// Populate parameters.
		ConnectionParameter cParm = null;
		int i;
		Object value;
		
		this.provider = cp;
		Component[] components = parmsPanel.getComponents();
		Enumeration e = cp.getConnectionParameters();
		while (e.hasMoreElements()) {
			cParm = (ConnectionParameter)e.nextElement();
			value = cParm.getValue();
			for (i = 0; i < components.length; i++) {
				if (cParm.getName().equals(components[i].getName())) {
					if (cParm instanceof PicklistConnectionParameter) {
						PicklistConnectionParameter plParm = (PicklistConnectionParameter)cParm;
						String[] values = (String[])plParm.getValue();
						if (values != null && values.length > 0) {
							if (components[i] instanceof JList) {
								DefaultComboBoxModel model = (DefaultComboBoxModel)((JList)components[i]).getModel();
								int[] selectedIndices = new int[values.length];
								for (int j = 0; j < values.length; j++) {
									selectedIndices[j] = model.getIndexOf(values[j]);
								}
								((JList)components[i]).setSelectedIndices(selectedIndices);
							} else if (components[i] instanceof JComboBox) {
								DefaultComboBoxModel model = (DefaultComboBoxModel)((JComboBox)components[i]).getModel();
								((JComboBox)components[i]).setSelectedIndex(model.getIndexOf(values[0]));
							}							
						}
					} else if (components[i] instanceof JCheckBox) {
						if (value == null) {
							((JCheckBox)components[i]).setSelected(false);
						} else {
							((JCheckBox)components[i]).setSelected(((Boolean)value).booleanValue());
						}
						break;
					} else {
						if (value == null) {
							((JTextComponent)components[i]).setText("");
						} else {
							((JTextComponent)components[i]).setText(value.toString());
						}
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Sets the default button for the provided root pane to the connect
	 * button of this panel.
	 * @param rootPane
	 */
	public void setDefaultButtonForRootPanel(JRootPane rootPane)
	{
		rootPane.setDefaultButton(okButton);
	}
	
	/**
	 * Called when the connection provider list's selection has changed.
	 * @param lse The event provided by the list.
	 */
	public void valueChanged(ListSelectionEvent lse) {
		Object source = lse.getSource();
		if (source.equals(providerList)) {
			populateParmsPanel();
		}
	}
}
