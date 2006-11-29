package us.pcsw.swing;

import java.beans.SimpleBeanInfo;

/**
 * us.pcsw.swing.HorizontalGlueBeanInfo
 * -
 * Bean information file for HorizontalGlue.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Aug 2, 2004 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class HorizontalGlueBeanInfo extends SimpleBeanInfo
{
	static final String ICON_PATH16 = "/us/pcsw/resources/images/HorizontalGlue16.png";
	static final String ICON_PATH32 = "/us/pcsw/resources/images/HorizontalGlue32.png";
	
	/**
	 * Instantiates a new HorizontalGlueBeanInfo.
	 */
	public HorizontalGlueBeanInfo()
	{
		super();
	}

	public java.awt.Image getIcon(int iconKind) {
	    if (iconKind == ICON_MONO_16x16 ||
	        iconKind == ICON_COLOR_16x16 )
	    {
	        java.awt.Image img = loadImage(ICON_PATH16);
	        return img;
	    }
	    if (iconKind == ICON_MONO_32x32 ||
	        iconKind == ICON_COLOR_32x32 )
	    {
	        java.awt.Image img = loadImage(ICON_PATH32);
	        return img;
	    }
	    return null;
	}	
}