package us.pcsw.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * us.pcsw.swing.SearchAndReplaceDialog
 * -
 * A standard dialog which will allow search and replace on text in a swing
 * text component, such as TextArea or TextField.  This JDialog expects to be
 * modal.  Its access to the JTextComponent is not syncronized.  Nor does it
 * check to see if the JTextComponent's text has been altered by another
 * thread.
 *
 * <P>Revision History:
 * <BR>06/05/2001 Development began on this class.
 *
 * @author Philip A. Chapman
 */
public class SearchAndReplaceDialog
    extends javax.swing.JDialog
    implements java.awt.event.ActionListener
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	private JButton closeButton = null;
	private JButton findNextButton = null;
	private JButton replaceAllButton = null;
	private JButton replaceButton = null;
	private JTextField replaceTextField = null;
	private JTextField searchTextField = null;

    /**
     * Holds the text string to do the search and replace on.
     */
    private JTextComponent targetTextComponent = null;

    /**
     * The current position in the string
     */
    private int currentPosition = -1;

    /**
     * Initializes the JDialog without an owner, but does not show it.
     * @exception IllegalArgumentException will be thrown if a null value is
     *                                     provided for targetTextComponent.
     * @param targetTextComponent The JTextComponent which contains the text
     *                            string to do the search and replace on.
     * @exception IllegalArgumentException Indicates that targetTextComponent was null.
     */
    public SearchAndReplaceDialog(JTextComponent targetTextComponent)
    	throws IllegalArgumentException
    {
		super();
		setModal(true);
	
		if (targetTextComponent == null) {
		    throw new IllegalArgumentException
			("A null value was provided for the text control to search.");
		}
		this.targetTextComponent = targetTextComponent;
	
		initGUI();
    }

    /**
     * Initializes the JDialog, but does not show it.
     * @exception IllegalArgumentException will be thrown if a null value is
     *                                     provided for targetTextComponent.
     * @param owner The owner Dailog.
     * @param targetTextComponent The JTextComponent which contains the text
     *                            string to do the search and replace on.
     * @exception IllegalArgumentException Indicates that targetTextComponent was null.
     */
    public SearchAndReplaceDialog(Dialog owner,
				  JTextComponent targetTextComponent)
		throws IllegalArgumentException
	{
		super(owner);
		setModal(true);
	
		if (targetTextComponent == null) {
		    throw new IllegalArgumentException
			("A null value was provided for the text control to search.");
		}
		this.targetTextComponent = targetTextComponent;
	
		initGUI();
    }

    /**
     * Initializes the JDialog, but does not show it.
     * @exception IllegalArgumentException will be thrown if a null value is
     *                                     provided for targetTextComponent.
     * @param owner The owner Frame.
     * @param targetTextComponent The JTextComponent which contains the text
     *                            string to do the search and replace on.
     * @exception IllegalArgumentException Indicates that targetTextComponent was null.
     */
    public SearchAndReplaceDialog
    	(Frame owner, JTextComponent targetTextComponent)
		throws IllegalArgumentException
	{
		super(owner);
		setModal(true);
	
		if (targetTextComponent == null) {
		    throw new IllegalArgumentException
			("A null value was provided for the text control to search.");
		}
		this.targetTextComponent = targetTextComponent;
	
		initGUI();
    }

    /**
     * Takes notification of ActionEvents originating with the select button,
     * the clear button, or the close button.
     */
    public void actionPerformed(ActionEvent event)
    {
		Object o = event.getSource();
		if (closeButton.equals(o)) {
		    setVisible(false);
		} else if (findNextButton.equals(o)) {
		    findNextButton_ActionEvent(event);
		} else if (replaceAllButton.equals(o)) {
			replaceAllButton_ActionEvent(event);
		} else if (replaceButton.equals(o)) {
		    replaceButton_ActionEvent(event);
		}
    }

    private void findNextButton_ActionEvent(ActionEvent event)
    {
		if (currentPosition == -1) {
			currentPosition = targetTextComponent.getSelectionStart();
		}
		int endPosition = 0;
		int startPosition = 0;
		String s1 = targetTextComponent.getText();
		String s2 = searchTextField.getText();
    	if (s2.length() == 0) {
    		// Nothing to search for.
    		return;
    	}
		startPosition = s1.indexOf(s2, currentPosition);
		if (startPosition > -1) {
		    endPosition = startPosition + s2.length();
			replaceAllButton.setEnabled(true);
		    replaceButton.setEnabled(true);
		} else {
		    startPosition = s1.length();
		    endPosition = currentPosition;
			replaceAllButton.setEnabled(false);
		    replaceButton.setEnabled(false);
		}
		targetTextComponent.setSelectionStart(startPosition);
		targetTextComponent.setSelectionEnd(endPosition);
		currentPosition = endPosition;
    }

    private void initGUI()
    {
		setTitle("Search And Replace");
	
		GridBagConstraints constraints = null;
		Insets insets = new Insets(2,2,2,2);
		JLabel label = null;
		JPanel panel = null;
	
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
	
	
		panel = new JPanel();
		GridBagLayout gridBag = new GridBagLayout();
		panel.setLayout(gridBag);
	
		label = new JLabel("Search For:");
		constraints = 
		    new GridBagConstraints(0, 0,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);
	
		searchTextField = new JTextField();
		searchTextField.setColumns(25);
		constraints = 
		    new GridBagConstraints(1, 0,        // gridx, gridy 
					   2, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(searchTextField, constraints);
		panel.add(searchTextField);
	
		label = new JLabel("Replace With:");
		constraints = 
		    new GridBagConstraints(0, 1,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(label, constraints);
		panel.add(label);
	
		replaceTextField = new JTextField();
		replaceTextField.setColumns(25);
		constraints = 
		    new GridBagConstraints(1, 1,        // gridx, gridy 
					   2, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.WEST, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   0, 0);       // ipadx, ipady
		gridBag.setConstraints(replaceTextField, constraints);
		panel.add(replaceTextField);
	
		contentPane.add(panel, BorderLayout.CENTER);
	
	
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		insets = new Insets(10,10,10,10);
	
		findNextButton = new JButton("Find Next");
		findNextButton.setMnemonic('F');
		findNextButton.addActionListener(this);
	       	constraints = 
	       	    new GridBagConstraints(0, 0,        // gridx, gridy 
	       				   1, 1,        // gridWidth, gridHeight
	       				   1, 1,        // weightx, weghty
	       				   GridBagConstraints.CENTER, // anchor
	       				   GridBagConstraints.NONE, // fill
	       				   insets,      // insets
	       				   5, 0);       // ipadx, ipady
	       	gridBag.setConstraints(findNextButton, constraints);
		panel.add(findNextButton);
	
		replaceButton = new JButton("Replace");
		replaceButton.setMnemonic('R');
		replaceButton.setEnabled(false);
		replaceButton.addActionListener(this);
		constraints = 
		    new GridBagConstraints(1, 0,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.CENTER, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   5, 0);       // ipadx, ipady
		gridBag.setConstraints(replaceButton, constraints);
		panel.add(replaceButton);
	
		replaceAllButton = new JButton("Replace All");
		replaceAllButton.setMnemonic('A');
		replaceAllButton.setEnabled(false);
		replaceAllButton.addActionListener(this);
		constraints = 
		    new GridBagConstraints(2, 0,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.CENTER, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   5, 0);       // ipadx, ipady
		gridBag.setConstraints(replaceAllButton, constraints);
		panel.add(replaceAllButton);

		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener(this);
		constraints = 
		    new GridBagConstraints(3, 0,        // gridx, gridy 
					   1, 1,        // gridWidth, gridHeight
					   1, 1,        // weightx, weghty
					   GridBagConstraints.CENTER, // anchor
					   GridBagConstraints.NONE, // fill
					   insets,      // insets
					   5, 0);       // ipadx, ipady
		gridBag.setConstraints(closeButton, constraints);
		panel.add(closeButton);
	
		contentPane.add(panel, BorderLayout.SOUTH);
	
		pack();
		
		// Center the dialog
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        setBounds(screenSize.width / 2 - mySize.width / 2,
                  screenSize.height / 2 - mySize.height / 2, 
                  mySize.width, 
                  mySize.height);
    }

	private void replaceAllButton_ActionEvent(ActionEvent event)
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		closeButton.setEnabled(false);
		findNextButton.setEnabled(false);
		replaceAllButton.setEnabled(false);
		replaceButton.setEnabled(false);

		// Set up the SearchAndReplaceWorker object
		SearchAndReplaceWorker sarw = new SearchAndReplaceWorker (
			this, targetTextComponent.getText(), searchTextField.getText(),
			replaceTextField.getText(),
			targetTextComponent.getSelectionStart()
		);
		sarw.start();
		return;
	}
	
	/**
	 * Called when the SearchAndReplaceWorker has completed the search and
	 * replace opperation.  This method is called by SearchAndReplaceWorker's
	 * finish method in the event handling thread.  It's OK to do GUI work
	 * here.
	 * @param replacedText The text after all the found strings have been
	 *                      replaced.
	 */
	void replaceAllFinished(String replacedText)
	{
		targetTextComponent.setText(replacedText);
		closeButton.setEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

    private void replaceButton_ActionEvent(ActionEvent event)
    {
		targetTextComponent.replaceSelection(replaceTextField.getText());
		findNextButton_ActionEvent(null);
    }
}
