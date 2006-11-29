package us.pcsw.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * us.pcsw.swing.FontChooser
 * -
 * A dialog which allows the user to choose a font.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>11/07/2002 This class was created.</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public class FontChooser
	extends JDialog
	implements ActionListener, ListSelectionListener
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

    /**
     * Font sizes for the selection.
     * */
    protected static final String[] FONT_SIZES =
    	{
    		"9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28",
	    	"36", "48", "72"
    	};

	/**
	 * Checkbox used to indicate that the font is to be bold.
	 */
	protected JCheckBox boldCheckBox = null;

	/**
	 * Used by the user to cancel the dialog.
	 */
	protected JButton cancelButton = null;

	/**
	 * The font chosen by the user.
	 */
	protected Font chosenFont = null;

	/**
	 * List holds displays available fonts.
	 */
	protected JList fontList = null;
	
	/**
	 * List which displays available font sizes.
	 */
	protected JList fontSizeList = null;

	/**
	 * Checkbox used to indicate that the font is to be italics.
	 */
	protected JCheckBox italicsCheckBox = null;

	/**
	 * Used by the user to accept the selected font and close the dialog.
	 */
	protected JButton okButton = null;

	/**
	 * Text area used to sample the selected font.
	 */
	protected JTextArea sampleTextArea = null;
	
    /**
     * Constructs a FontChooser with a dialog owner.
     *
     * @param owner the non-null Dialog from which the dialog is displayed.
     * @param title the String to display in the dialog's title bar.
     * @param modal a boolean that indicates whether or not the dialog is modal.
     * @param font true for a modal dialog, false for one that allows other
     *              windows to be active at the same time.  May be null.
     */
    public FontChooser(Dialog owner, String title, boolean modal, Font font)
    {
        super(owner, title, modal);
		initGUI();
		setSelectedFont(font);
    }

    /**
     * Constructs a FontChooser with a dialog owner.
     *
     * @param owner the Dialog from which the dialog is displayed.
     * @param title the String to display in the dialog's title bar.
     * @param modal a boolean that indicates whether or not the dialog is modal.
     * @param font true for a modal dialog, false for one that allows other
     *              windows to be active at the same time.  May be null.
     */
    public FontChooser(Frame owner, String title, boolean modal, Font font)
    {
        super(owner, title, modal);
  		initGUI();
		setSelectedFont(font);
    }

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();
		if (cancelButton.equals(o)) {
			chosenFont = null;
			setVisible(false);
		} else if (okButton.equals(o)) {
			setVisible(false);
		} else {
			setSelectedFont(buildSelectedFont());
		}
	}

    /**
     * Uses selected values to build a font and return it.
     *
     * @return the font.
     */
    protected Font buildSelectedFont() {
        return new Font(getSelectedFontName(), getSelectedFontStyle(), getSelectedFontSize());
    }
    
    /**
     * Returns the selected font.  If the dialog was cancelled, null is
     * returned.
     *
     * @return the font.
     */
    public Font getSelectedFont()
    {
        return chosenFont;
    }

    /**
     * Returns the selected font name.
     *
     * @return the name.
     */
    public String getSelectedFontName() {
        return (String) fontList.getSelectedValue();
    }

    /**
     * Returns the selected font style.
     *
     * @return the style.
     */
    public int getSelectedFontStyle() {
    	int ret = Font.PLAIN;
    	if (boldCheckBox.isSelected()) {
            ret = ret + Font.BOLD;
        }
        if (italicsCheckBox.isSelected()) {
            ret = ret + Font.ITALIC;
        }
        return ret;
    }

    /**
     * Returns the selected font size.
     *
     * @return the size.
     */
    public int getSelectedFontSize() {
        String selected = (String) fontSizeList.getSelectedValue();
        if (selected == null) {
        	return 10;
        } else {
            return Integer.parseInt(selected);
        }
    }

    /**
     * Initializes the GUI.
     */
    protected void initGUI()
    {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = g.getAvailableFontFamilyNames();
        getContentPane().setLayout(new BorderLayout());
		JPanel primaryPanel = new JPanel();
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.Y_AXIS));
		JPanel choicesPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), "Font:"));
        fontList = new JList(fonts);
        fontList.addListSelectionListener(this);
        JScrollPane fontPane = new JScrollPane(fontList);
        fontPane.setBorder(BorderFactory.createEtchedBorder());
        fontPanel.add(fontPane);
        choicesPanel.add(fontPanel, BorderLayout.CENTER);

        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), "Size:"));
        fontSizeList = new JList(FONT_SIZES);
        fontSizeList.addListSelectionListener(this);
        JScrollPane sizePane = new JScrollPane(fontSizeList);
        sizePane.setBorder(BorderFactory.createEtchedBorder());
        sizePanel.add(sizePane);
        rightPanel.add(sizePanel, BorderLayout.CENTER);

        JPanel attributesPanel = new JPanel(new GridLayout(1, 2));
        boldCheckBox = new JCheckBox("Bold");
        boldCheckBox.addActionListener(this);
        italicsCheckBox = new JCheckBox("Italic");
        italicsCheckBox.addActionListener(this);
        attributesPanel.add(boldCheckBox);
        attributesPanel.add(italicsCheckBox);
        attributesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                             	 "Attributes:"));
        rightPanel.add(attributesPanel, BorderLayout.SOUTH);

        choicesPanel.add(rightPanel, BorderLayout.EAST);
        primaryPanel.add(choicesPanel);
        
        sampleTextArea = new JTextArea("ABCDefgh 1234");
        JScrollPane sampleScrollPane = new JScrollPane(sampleTextArea);
        sampleScrollPane.setPreferredSize(new Dimension(350, 120));
        primaryPanel.add(sampleScrollPane);
        getContentPane().add(primaryPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        
       	okButton = new JButton("OK");
       	okButton.setMnemonic('O');
       	okButton.addActionListener(this);
       	buttonPanel.add(okButton);
       	
       	cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
       	
       	getContentPane().add(buttonPanel, BorderLayout.SOUTH);
       	pack();
    	// Center the dialog
		Dimension mySize = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width / 2) - (mySize.width / 2), 
                  (screenSize.height / 2) - (mySize.height / 2),
                  mySize.width, mySize.height);
    }

	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		setSelectedFont(buildSelectedFont());
	}

	/**
	 * Sets the selected font.
	 */
	public void setSelectedFont(Font font)
	{
		if (font != null && ! font.equals(chosenFont)) {
			fontList.setSelectedValue(font.getName(), true);
			fontSizeList.setSelectedValue(String.valueOf(font.getSize()), true);
			boldCheckBox.setSelected(font.isBold());
			italicsCheckBox.setSelected(font.isItalic());
			sampleTextArea.setFont(font);
			chosenFont = font;
		}
	}
}
