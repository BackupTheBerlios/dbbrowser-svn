package us.pcsw.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * SystemInfoDialog
 * -
 * Displays system information.  This information is often useful for
 * debugging application problems.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Jan 5, 2004 Class Created By pchapman.</LI>
 * </UL></P>
 */
public class SystemInfoDialog extends JDialog
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	private ActionListener closeActionListener = null;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public SystemInfoDialog()
		throws HeadlessException
	{
		super((Dialog)null, true);
		initGUI();
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public SystemInfoDialog(Dialog owner)
		throws HeadlessException
	{
		super(owner, true);
		initGUI();
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public SystemInfoDialog(Frame owner)
		throws HeadlessException
	{
		super(owner, true);
		initGUI();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public SystemInfoDialog(Dialog owner, String title)
		throws HeadlessException
	{
		super(owner, title, true);
		initGUI();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public SystemInfoDialog(Frame owner, String title)
		throws HeadlessException
	{
		super(owner, title, true);
		initGUI();
	}

	private ActionListener getCloseActionListener()
	{
		if (closeActionListener == null) {
			// A listener that will close this dialog any time an action event is
			// reported to it.
			closeActionListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent actionEvent) {
					dispose();
				}
			};
		}
		return closeActionListener;
	}

	/**
	 * Returns the system properties in a two dimensional Object array.
	 */
	private Object[][] getSystemProperties()
	{
		Properties properties = System.getProperties();
		Object[][] values = new Object[properties.size()][2];
		Enumeration keys = properties.keys();
		int i = 0;
		while (keys.hasMoreElements()) {
			values[i][0] = keys.nextElement();
			values[i][1] = properties.get(values[i][0]);
			i++;
		}
		return values;
	}

	/**
	 * Builds the user interface.
	 */
	private void initGUI()
	{
		// Initilize
		JComponent container = null;
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		
		// Set up the content pane.
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
		Container contentPane = new JPanel(new BorderLayout());
		getContentPane().add(contentPane);

		
		// A Label to explain the dialog's contents.
		JLabel label = new JLabel("The Java Runtime Environment's system properties are as follows:");
		contentPane.add(label, BorderLayout.NORTH);
		
		// The bulk of the interface is a table which shows system properties.
		String columns[] = {"Key", "Value"}; 
		TableModel tm =
					new DefaultTableModel (getSystemProperties(), columns)
					{
						// CONSTANTS
						
						private static final long serialVersionUID = 1L;

						public boolean isCellEditable(int row, int column)
							{return false;}
					};
		JTable propertiesTable = new JTable(tm);
		container = new JScrollPane(propertiesTable);
		container.registerKeyboardAction(getCloseActionListener(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		contentPane.add(container, BorderLayout.CENTER);

		// On the bottom, left of the frame, put a close button.
		FlowLayout layout = new FlowLayout(FlowLayout.RIGHT, 0, 5);
		container = new JPanel(layout);
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener(getCloseActionListener());
		getRootPane().setDefaultButton(closeButton);
		container.add(closeButton);
		contentPane.add(container, BorderLayout.SOUTH);
		
		// Make sure we always respond to the Escape key being pressed.
		getRootPane().registerKeyboardAction(getCloseActionListener(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// Realize and center the dialog.
		pack();
		Dimension mySize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width / 2) - (mySize.width / 2), 
				  (screenSize.height / 2) - (mySize.height / 2),
				  mySize.width, mySize.height);
	}
}
