package us.pcsw.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * us.pcsw.util.LookAndFeelMenu
 *
 * This is a JMenu which allows for the selection of look and feel.  It can be
 * added to any JMenuBar or other JMenu.  When a new Look And Feel is selected,
 * this class makes a getTopLevelAncestor() call on itself.  That Component's
 * UI is then updated using SwingUtilities.updateComponentTreeUI(Component).
 *
 * <UL>Revision History:
 * <LI>06/07/2000 This class was created.</LI>
 * <LI>04/11/2001 A bug was discovered in that the super's getTopLevelAncestor
 *                method returns null if this menu has been added to another
 *                JMenu rather than a JMenuBar (so that you can use it as a
 *                submenu).  It shouldn't do this as JComponent's documentation
 *                states that the method returns null "if this component has
 *                not been added to any container".  Which it has, as JMenu
 *                inherits from java.awt.Container.  This caused a null-pointer
 *                exception on the call to the top level ancestor component to
 *                update it's UI.  To fix this, the constructor was altered to
 *                require that a component be provided to be used rather than
 *                relying on the object returned by getTopLevelAncestor.
 *                <P>Also, changes were made so that the ItemStateChanged
 *                method ignores ItemEvents thrown by any objects other than
 *                the JRadioButtonMenuItems created in this class's
 *                constructor.  This insures that there are no conflicts should
 *                this class be registered as a listener for any other Object
 *                which may create ItemEvents.</LI>
 * <LI>07/29/2003 Added The LookAndFeelMenu(Component, String) constructor.
 *                Also added code so that a java.awt.event.ActionEvent is
 *                emitted when one of the menu items selection changes.
 *                PAC </LI>
 * </UL>
 *
 * @author Philip A. Chapman
 */
public class LookAndFeelMenu
    extends javax.swing.JMenu
    implements java.awt.event.ItemListener
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

    /**
     * When a look and feel change occurs, changes are propogated through this
     * UI component.
     */
    private Component root;

    /**
     * The currently selected look and feel menu item.
     */
    private JRadioButtonMenuItem currentLookAndFeel;

    /**
     * The ButtonGroup which provides the mutual exclusive nature of the
     * JRadioButtonMenuItems.
     */
    private LookAndFeelButtonGroup buttonGroup;

    /**
     * Constructor which creates a new instance of LookAndFeelMenu.  Look And
     * Feel options are taken from a call to the UIManager.LookAndFeelInfo()
     * method.
     * @param root The UI component from which look and feel changes are to be
     *             propogated.  This should normally be a java.awt.Frame,
     *             javax.swing.JFrame, or java.applet.Applet.
     */
    public LookAndFeelMenu(Component root)
    {
		this(root, null);
    }
    
    /**
     * Constructor which creates a new instance of LookAndFeelMenu.  Look And
     * Feel options are taken from a call to the UIManager.LookAndFeelInfo()
     * method.  If there is a look and feel which has the given name, it is
     * selected.
     * @param root The UI component from which look and feel changes are to be
     *             propogated.  This should normally be a java.awt.Frame,
     *             javax.swing.JFrame, or java.applet.Applet.
     * @param lafName The name of the look and feel to select at start.
     */
    public LookAndFeelMenu(Component root, String lafName)
    {
		// Set up class
		super("Look And Feel");
		this.root = root;
		setMnemonic('L');
		
		// Set up menu items
		buttonGroup = new LookAndFeelButtonGroup();
		String laf = UIManager.getLookAndFeel().getName();
		UIManager.LookAndFeelInfo lfi[] = 
		    UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lfi.length; i++) {
		    JMenuItem mi = new JRadioButtonMenuItem(lfi[i].getName());
		    mi.setActionCommand(lfi[i].getClassName());
		    if (laf.equals(lfi[i].getName())) {
				mi.setSelected(true);
				currentLookAndFeel = (JRadioButtonMenuItem)mi;
		    }
		    mi.addItemListener(this);
		    buttonGroup.add(mi);
		    add(mi);
		}
		
		// Select the indicated look and feel (if there is one)
		if (lafName != null) {
			AbstractButton ab;
			Enumeration enumer = buttonGroup.getElements();
			while (enumer.hasMoreElements()) {
				ab = (AbstractButton)enumer.nextElement();
				if (lafName.equals(ab.getText())) {
					ab.setSelected(true);
					break;
				}
			}
		}
    }

    /**
     * Called when the look and feel menu items are selected/deselected.  Look
     * and Feel is Changed here.  Any errors setting look and feel to the
     * selected value will be handled internally and dialog boxes will notify
     * the user that the selected look and feel is not available.  Also, that
     * look and feel's JRadioButtonMenuItem  will be disabled.
     *
     * @param e The ItemEvent fired by the JRadioButtonMenuItem which was
     *          changed.
     */
    public void itemStateChanged(ItemEvent e)
    {
		try {
		    if (buttonGroup.contains(e.getSource())) {
				// Type cast the source of the event.
				JRadioButtonMenuItem button =
				    (JRadioButtonMenuItem)e.getSource();
				if (button.isSelected()) {
				    try {
						// Set the cursor into a wait mode while
						// changes are made
						root.setCursor(Cursor.getPredefinedCursor
							       (Cursor.WAIT_CURSOR));
						// Set the look and feel
						UIManager.setLookAndFeel(button.getActionCommand());
						button.setEnabled(true);
						SwingUtilities.updateComponentTreeUI(root);
						currentLookAndFeel = button;
						// Notify Action listeners that a change has been made.
						fireActionPerformed
							(new ActionEvent(this,
							                 ActionEvent.ACTION_PERFORMED,
							                 getActionCommand()));
						// Reset the cursor
						root.setCursor(Cursor.getPredefinedCursor
							       (Cursor.DEFAULT_CURSOR));
				    } catch (UnsupportedLookAndFeelException exc1) {
						// Error - unsupported L&F
						button.setEnabled(false);
						JOptionPane.showMessageDialog
						    (null,
						     "Unable to load the selected Look and Feel.", 
						     "Error Loading Look and Feel",
						     JOptionPane.ERROR_MESSAGE);
						if (! currentLookAndFeel.equals(button)) {
						    currentLookAndFeel.setSelected(true);
						}
				    }
				}
		    }
		} catch (java.lang.Throwable exc3) {
		    JOptionPane.showMessageDialog(null, "An unexpected error was encountered loading the selected Look and Feel.\n" + exc3.getMessage(), "Error Loading Look and Feel", JOptionPane.ERROR_MESSAGE);
		}
    }
}

/**
 * pac.swing.LookAndFeelButtonGroup
 * -
 * Provides the mutual exclusive properties of the Look And Feel menu items.
 *
 * <P>Revision History:
 * <BR>04/11/2001 This class was created.
 *
 * @author Philip A. Chapman
 */
class LookAndFeelButtonGroup extends javax.swing.ButtonGroup
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

    /**
     * Indicates if the object is one of the Look And Feel menu items managed
     * by this instance of the LookAndFeelButtonGroup class.
     * @param obj The object to test.
     * @return True if the object is one of the managed Look And Feel menu
     *         items or False if it is not.
     */
    boolean contains(Object obj)
    {
		return buttons.contains(obj);
    }

    /**
     * Enables or disables all the Look And Feel menu items at once.
     * @param enabled When true will cause all the Look And Feel menu items to
     *                be enabled.  When false, they will all be disabled.
     */
    void setItemsEnabled(boolean enabled)
    {
		for (Enumeration enm = buttons.elements(); enm.hasMoreElements();) {
		    ((JMenuItem)enm.nextElement()).setEnabled(enabled);
		}
    }
}
