package us.pcsw.swing;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * ToolbarButton
 * -
 * A subclass of JButton with added methods that make it handy for use in a
 * toolbar.  Instead of having to set the text to null or a caption string, the
 * method setLabelDisplayed(boolean) can be called to disable or enable the
 * display text on the button.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 28, 2003 Class Created By PAC.</LI>
 * </UL></P>
 */
public class ToolbarButton
	extends JButton
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	protected String labelText = null;

	/**
	 * @see javax.swing.JButton#JButton()
	 */
	public ToolbarButton() {
		super();
	}

    /**
     * @see javax.swing.JButton#JButton(java.lang.String)
     */
    public ToolbarButton(String text)
    {
        super(text);
        labelText = text;
    }

    /**
     * @see javax.swing.JButton#JButton(javax.swing.Icon)
     */
    public ToolbarButton(Icon icon) {
        super(icon);
    }

    /**
     * @see javax.swing.JButton#JButton(javax.swing.Action)
     */
    public ToolbarButton(Action a) {
        super(a);
    }

    /**
     * @see javax.swing.JButton#JButton(java.lang.string, javax.swing.Icon)
     */
    public ToolbarButton(String text, Icon icon)
    {
        super(text, icon);
        labelText = text;
    }
    
    /**
     * Creates a ToolbarButton and adds it to the given parent.
     * @param caption The caption for the button.
     * @param icon The icon for the button.
     * @param tooltipText The tooltip text for the button.
     * @param displayLabel Whether the label should be displayed.
     * @param parent The item to which the button should be added.
     *               May be null.
     * @param listener A listener which will listen for action events.
     *                 May be null.
     */
    public ToolbarButton
    	(String caption, Icon icon, String tooltipText, boolean displayLabel,
    	 Container parent, ActionListener listener
    	)
    {
		super(icon);
		setBorderPainted(false);
		setLabel(caption);
		setToolTipText(tooltipText);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setLabelDisplayed(displayLabel);
		if (listener != null) {
			addActionListener(listener);
		}
		if (parent != null) {
			parent.add(this);
		}
    }

	/**
	 * Creates a ToolbarButton and adds it to the given parent.
	 * @param caption The caption for the button.
	 * @param iconURL The location from which the icon can be loaded.
	 * @param tooltipText The tooltip text for the button.
	 * @param displayLabel Whether the label should be displayed.
	 * @param parent The item to which the button should be added.
	 *               May be null.
	 * @param listener A listener which will listen for action events.
	 *                 May be null.
	 */
	public ToolbarButton
		(String caption, URL iconURL, String tooltipText, boolean displayLabel,
		 Container parent, ActionListener listener
		)
	{
		super(new ImageIcon(iconURL));
		setBorderPainted(false);
		setLabel(caption);
		setToolTipText(tooltipText);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setLabelDisplayed(displayLabel);
		addActionListener(listener);
		parent.add(this);
	}

	/**
	 * Indicates whether the label is being displayed.
	 * @return True if the label is being displayed, false otherwise.
	 */
	public boolean isLabelDisplayed()
	{
		return (getText() == null);		
	}

	/**
	 * Disables the display of the label.  If the label is turned off, the
	 * text is still retained and displayed when the label is turned back on. 
	 * @param display True if the label should be displayed, false otherwise.
	 */
	public void setLabelDisplayed(boolean display)
	{
		if (display) {
			setText(labelText);
		} else {
			setText(null);
		}
	}
	
    /**
     * @deprecated
     * @see javax.swing.AbstractButton#setLabel(java.lang.String)
     */
    public void setLabel(String label) {
        super.setLabel(label);
        labelText = label;
    }

    /**
     * @see javax.swing.AbstractButton#setText(java.lang.String)
     */
    public void setText(String text) {
        super.setText(text);
        labelText = text;
    }
}
