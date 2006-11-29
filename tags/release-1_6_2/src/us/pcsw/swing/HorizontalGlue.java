package us.pcsw.swing;

import java.awt.Dimension;

/**
 * us.pcsw.swing.HorizontalGlue
 * -
 * An invisible component which takes up no vertical space, and as much
 * horizontal space as possible.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Aug 2, 2004 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class HorizontalGlue extends FillerComponent
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

    /**
     * Creates a horizontal glue component.
     *
     * @return the component
     */
    public HorizontalGlue()
    {
    	super(new Dimension(0,0), new Dimension(0,0), 
			  new Dimension(Short.MAX_VALUE, 0));
    }
}
