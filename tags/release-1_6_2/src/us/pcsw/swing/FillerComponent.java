package us.pcsw.swing;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

/**
 * An implementation of a lightweight component that participates in layout but
 * has no view.
 * <p>
 * <strong>Warning: </strong> Serialized objects of this class will not be
 * compatible with future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Swing. As of 1.4, support for long term storage of all
 * JavaBeans <sup><font size="-2">TM </font> </sup> has been added to the
 * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
 * <p>
 * <B>NOTE:</B> This code was taken directly from javax.swing.Box's inline
 * class, Filler.  I created this class and its subclasses to ease the use of
 * the filler class with other layouts besides BoxLayout and from within an IDE.
 */
public class FillerComponent extends JComponent
	implements Accessible
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor to create shape with the given size ranges.
	 * 
	 * @param min
	 *            Minimum size
	 * @param pref
	 *            Preferred size
	 * @param max
	 *            Maximum size
	 */
	public FillerComponent(Dimension min, Dimension pref, Dimension max) {
		reqMin = min;
		reqPref = pref;
		reqMax = max;
	}
	
    /**
     * Creates an invisible "glue" component 
     * that can be useful in a Box
     * whose visible components have a maximum width
     * (for a horizontal box)
     * or height (for a vertical box).
     * You can think of the glue component
     * as being a gooey substance
     * that expands as much as necessary
     * to fill the space between its neighboring components.
     *
     * <p>
     *
     * For example, suppose you have
     * a horizontal box that contains two fixed-size components.
     * If the box gets extra space,
     * the fixed-size components won't become larger,
    * so where does the extra space go?
     * Without glue,
     * the extra space goes to the right of the second component.
     * If you put glue between the fixed-size components,
     * then the extra space goes there.
     * If you put glue before the first fixed-size component,
     * the extra space goes there,
     * and the fixed-size components are shoved against the right
     * edge of the box.
     * If you put glue before the first fixed-size component
     * and after the second fixed-size component,
     * the fixed-size components are centered in the box.
     *
     * <p>
     *
     * To use glue,
     * call <code>Box.createGlue</code>
     * and add the returned component to a container.
     * The glue component has no minimum or preferred size,
     * so it takes no space unless excess space is available.
     * If excess space is available, 
     * then the glue component takes its share of available
     * horizontal or vertical space,
     * just like any other component that has no maximum width or height.
     *
     * @return the component
     */
    public FillerComponent()
    {
    	this(
    			new Dimension(0,0), new Dimension(0,0),
				new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)
			);
    }

	/**
	 * Change the size requests for this shape. An invalidate() is propagated
	 * upward as a result so that layout will eventually happen with using the
	 * new sizes.
	 * 
	 * @param min
	 *            Value to return for getMinimumSize
	 * @param pref
	 *            Value to return for getPreferredSize
	 * @param max
	 *            Value to return for getMaximumSize
	 */
	public void changeShape(Dimension min, Dimension pref, Dimension max) {
		reqMin = min;
		reqPref = pref;
		reqMax = max;
		invalidate();
	}

	// ---- Component methods ------------------------------------------

	/**
	 * Returns the minimum size of the component.
	 * 
	 * @return the size
	 */
	public Dimension getMinimumSize() {
		return reqMin;
	}

	/**
	 * Returns the preferred size of the component.
	 * 
	 * @return the size
	 */
	public Dimension getPreferredSize() {
		return reqPref;
	}

	/**
	 * Returns the maximum size of the component.
	 * 
	 * @return the size
	 */
	public Dimension getMaximumSize() {
		return reqMax;
	}

	// ---- member variables ---------------------------------------

	private Dimension reqMin;

	private Dimension reqPref;

	private Dimension reqMax;

	/////////////////
	// Accessibility support for Box$FillerComponent
	////////////////

	/**
	 * The currently set AccessibleContext object.
	 */
	protected AccessibleContext accessibleContext = null;

	/**
	 * Gets the AccessibleContext associated with this Box.FillerComponent. For box
	 * fillers, the AccessibleContext takes the form of an AccessibleBoxFiller.
	 * A new AccessibleAWTBoxFiller instance is created if necessary.
	 * 
	 * @return an AccessibleBoxFiller that serves as the AccessibleContext of
	 *         this Box.FillerComponent.
	 */
	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleBoxFiller();
		}
		return accessibleContext;
	}

	/**
	 * This class implements accessibility support for the
	 * <code>Box.FillerComponent</code> class.
	 */
	protected class AccessibleBoxFiller extends AccessibleAWTComponent
	{
		// CONSTANTS
		
		private static final long serialVersionUID = 1L;

		// AccessibleContext methods
		//
		/**
		 * Gets the role of this object.
		 * 
		 * @return an instance of AccessibleRole describing the role of the
		 *         object (AccessibleRole.FILLER)
		 * @see AccessibleRole
		 */
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.FILLER;
		}
	}
}