package us.pcsw.swing;

import java.awt.Dimension;

/**
 * us.pcsw.swing.HorizontalStrut
 * -
 * An invisible component which takes up no vertical space and a fixed
 * horizontal space.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Aug 2, 2004 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class HorizontalStrut extends FillerComponent
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new horizontal strut with a width of 0.
	 */
	public HorizontalStrut() {
		this(0);
	}
	
	/**
	 * Creates a new horizontal strut with the given width.
	 * @param width The width of the strut.
	 */
	public HorizontalStrut(int width) {
		super(
				new Dimension(width,0), new Dimension(width,0),
				new Dimension(width, Short.MAX_VALUE)
			);
	}
}
