package us.pcsw.swing;

import java.awt.Component;
import java.awt.HeadlessException;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import us.pcsw.util.StringUtil;

/**
 * WrappedOptionPane
 * -
 * An implementation of JMessageBox which line-wraps the message before
 * displaying it.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 25, 2004 Class Created By Philip A. Chapman.</LI>
 * </UL></P>
 */
public class WrappedOptionPane
	extends JOptionPane
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	public static void showWrappedMessageDialog
		(Component parent,String message,String title, int messageType)
	{
		JOptionPane.showMessageDialog(parent, StringUtil.wrapText(message), title, messageType);
	}
	
	/**
     * Brings up a dialog where the number of choices is determined
     * by the <code>optionType</code> parameter.
     * 
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                  <code>YES_NO_OPTION</code>, or
     *			<code>YES_NO_CANCEL_OPTION</code>
     * @return an int indicating the option selected by the user
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showWrappedConfirmDialog(Component parentComponent,
        Object message, String title, int optionType)
        throws HeadlessException
	{
    	return JOptionPane.showConfirmDialog
			(parentComponent, StringUtil.wrapText(message.toString()), title, optionType);
    }
    
    /**
     * Shows a question-message dialog requesting input from the user
     * parented to <code>parentComponent</code>.
     * The dialog is displayed on top of the <code>Component</code>'s
     * frame, and is usually positioned below the <code>Component</code>. 
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *		dialog
     * @param message  the message to display
     * @exception HeadlessException if
     *    <code>GraphicsEnvironment.isHeadless</code> returns
     *    <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showWrappedInputDialog(Component parentComponent, Object message)
    	throws HeadlessException
	{
        return JOptionPane.showInputDialog(parentComponent, StringUtil.wrapText(message.toString()));
    }

    /**
     * Shows a question-message dialog requesting input from the user and
     * parented to <code>parentComponent</code>. The input value will be
     * initialized to <code>initialSelectionValue</code>.
     * The dialog is displayed on top of the <code>Component</code>'s
     * frame, and is usually positioned below the <code>Component</code>.  
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *		dialog
     * @param message the <code>Object</code> to display
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     * @since 1.4
     */
    public static String showWrappedInputDialog(
    		Component parentComponent, Object message, 
			Object initialSelectionValue)
    {
        return JOptionPane.showInputDialog(
        		parentComponent, StringUtil.wrapText(message.toString()),
				initialSelectionValue);
    }
    

    /**
     * Prompts the user for input in a blocking dialog where the
     * initial selection, possible selections, and all other options can
     * be specified. The user will able to choose from
     * <code>selectionValues</code>, where <code>null</code> implies the
     * user can input
     * whatever they wish, usually by means of a <code>JTextField</code>. 
     * <code>initialSelectionValue</code> is the initial value to prompt
     * the user with. It is up to the UI to decide how best to represent
     * the <code>selectionValues</code>, but usually a
     * <code>JComboBox</code>, <code>JList</code>, or
     * <code>JTextField</code> will be used.
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *			dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the
     *			dialog title bar
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *			<code>INFORMATION_MESSAGE</code>,
     *			<code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *			or <code>PLAIN_MESSAGE</code>
     * @param icon     the <code>Icon</code> image to display
     * @param selectionValues an array of <code>Object</code>s that
     *			gives the possible selections
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     * @return user's input, or <code>null</code> meaning the user
     *			canceled the input
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Object showWrappedInputDialog(
    		Component parentComponent, Object message, String title,
			int messageType, Icon icon, Object[] selectionValues,
			Object initialSelectionValue)
        throws HeadlessException 
	{
    	return JOptionPane.showInputDialog(
    			parentComponent, message, title, messageType, icon,
		        selectionValues, initialSelectionValue);
    }
}
