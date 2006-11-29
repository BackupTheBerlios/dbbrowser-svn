package us.pcsw.swing;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A basic implementation of FileFilter that filters files based on a list of
 * extensions.
 *
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 *
 * Example - create a new filter that filters out all files
 * but gif and jpg image files:
 *
 * <PRE>
 *     JFileChooser chooser = new JFileChooser();
 *     BasicFileFilter filter = new BasicFileFilter(new String[] {"gif", "jpg"},
 *                                                  "JPEG & GIF Images")
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 * </PRE>
 *
 * <P>Revision History:
 * <BR>05/22/2001 This class was created.
 *
 * @author Philip A. Chapman
 */
public class BasicFileFilter extends javax.swing.filechooser.FileFilter {

    // There are collection objects which provide faster searching, but it is
    // unlikely that there will ever be more than a few extensions for each
    // filter.  The vector has less overhead than some faster collections with
    // an acceptable insert speed.  Also, Vector is syncronized.
    protected Vector extensionList = null;
    protected String description = null;
    private String fullDescription = null;
    protected boolean useExtensionsInDescription = true;

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new BasicFileFilter("jpg");
     *
     * @see #addExtension
     */
    public BasicFileFilter(String extension) {
	this(extension, null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new BasicFileFilter("jpg", "JPEG Image Images");
     *
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public BasicFileFilter(String extension, String description) {
	addExtension(extension);
 	if(description != null) setDescription(description);
    }

    /**
     * Creates a file filter from the given string array.
     * Example: new BasicFileFilter(String {"gif", "jpg"});
     *
     * Note that the "." before the extension is not needed and will be
     * ignored.
     *
     * @see #addExtension
     */
    public BasicFileFilter(String[] extensions) {
	this(extensions, null);
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example:
     *   new BasicFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     *
     * Note that the "." before the extension is not needed and will be
     * ignored.
     *
     * @see #addExtension
     */
    public BasicFileFilter(String[] extensions, String description) {
	// It's fairly likely that no more extensions will be added.  Go ahead
	// and optimize the Hashtable for the number of extensions provided.
	addExtensions(extensions);	
 	if(description!=null) setDescription(description);
    }

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     *
     * Files that begin with "." are ignored.
     *
     * @see #getExtension
     * @see javax.swing.filechooser.FileFilter#accepts
     */
    public boolean accept(File f) {
	if (f != null) {
	    if (f.isDirectory()) {
		return true;
	    }
	    String extension = getExtension(f);
	    if (extensionList.contains(extension)) {
		return true;
	    };
	}
	return false;
    }

    /**
     * Adds a filetype extension to filter against.
     *
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     *
     *   BasicFileFilter filter = new BasicFileFilter("jpg");
     *   filter.addExtension("tif");
     *
     * Note that the "." before the extension is not needed and will be
     * ignored.
     */
    public void addExtension(String extension) {
	if (extensionList == null) {
	    extensionList = new Vector(1, 1);
	}
	if (extension.startsWith(".")) {
	    if (extension.length() == 1) {
		return;
	    } else {
		extension = extension.substring(1).toLowerCase();
	    }
	} else {
	    extension = extension.toLowerCase();
	}
	if (! extensionList.contains(extension)) {
	    extensionList.add(extension);
	    fullDescription = null;
	}
    }

    /**
     * Adds filetype extensions to filter against.
     *
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".gif", ".jpg" and ".tif":
     *
     *   BasicFileFilter filter = new BasicFileFilter("gif");
     *   filter.addExtensions(String {"jpg", "tif"});
     *
     * Note that the "." before the extension is not needed and will be
     * ignored.
     */
    public void addExtensions(String extensions[]) {
	if (extensionList == null) {
	    extensionList = new Vector(extensions.length, 1);
	} else {
	    extensionList.ensureCapacity(extensionList.size() +
					 extensions.length);
	}
	for (int i = 0; i < extensions.length; i++) {
	    addExtension(extensions[i]);
	}
    }

    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see javax.swing.filechooser.FileFilter#getDescription
     */
    public String getDescription()
    {
	StringBuffer sb = null;
	if (fullDescription == null) {
	    if (description == null || isExtensionListInDescription()) {
		sb = new StringBuffer();
		if (description != null) {
		    sb.append(description);
		    sb.append(' ');
		}
		sb.append('(');
		// build the description from the extension list
		Enumeration extensions = extensionList.elements();
		if (extensions != null) {
		    sb.append('.');
		    sb.append(extensions.nextElement());
		    while (extensions.hasMoreElements()) {
				sb.append(", ");
				sb.append('.');
				sb.append(extensions.nextElement());
		    }
		}
		sb.append(')');
		fullDescription = sb.toString();
	    } else {
		fullDescription = description;
	    }
	}
	return fullDescription;
    }

    /**
     * Return the extension portion of the file's name.
     *
     * @return The extension portion of the file's name, or a zero length
     *         String if the file has no extension.
     * @see #getExtension
     * @see javax.swing.filechooser.FileFilter#accept
     */
     public static String getExtension(File f) {
	if (f != null) {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if (i > 0 && i < filename.length() - 1) {
		return filename.substring(i + 1).toLowerCase();
	    }
	}
	return "";
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     */
    public boolean isExtensionListInDescription() {
	return useExtensionsInDescription;
    }

    /**
     * Removes a filetype extension from the filter.
     */
    public void removeExtension(String extension) {
	if (extensionList != null) {
	    if (extension.startsWith(".")) {
		if (extension.length() > 1) {
		    extension = extension.substring(1).toLowerCase();
		}
	    } else {
		extension = extension.toLowerCase();
	    }
	    if (extensionList.contains(extension)) {
		extensionList.remove(extension);
		fullDescription = null;
	    }
	}
    }

    /**
     * Removes filetype extensions from the filter.
     */
    public void removeExtension(String extensions[]) {
	if (extensionList != null) {
	    for (int i = 0; i < extensions.length; i++) {
		removeExtension(extensions[i]);
	    }
	}
    }

    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public void setDescription(String description) {
	// Avoiding a rebuild of the fullDescription would require several
	// tests here that will most likely result in the fullDescription being
	// set to null for rebuild.  Those several tests probably will not buy
	// much in performance and would make the code a little messy.
	this.description = description;
	fullDescription = null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     */
    public void setExtensionListInDescription(boolean b) {
	// Avoid rebuilding the fullDescription if not necessary.
	if (useExtensionsInDescription != b) {
	    useExtensionsInDescription = b;
	    fullDescription = null;
	}
    }
}
