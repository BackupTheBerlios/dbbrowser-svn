package us.pcsw.util.tablemodelexport;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import us.pcsw.swing.BasicFileFilter;
import us.pcsw.util.BaseEnum;

/**
 * us.pcsw.util.tablemodelexport.TableModelExportType
 * -
 * An emumeration that defines the types of exports available.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 14, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class TableModelExportType extends BaseEnum
{
	/*
	 * 		 0	-	File extension
	 * 		 1	-	File type description
	 */
	private static final String[][] FILE_TYPE_DATA =
	{
		{
			"csv", "Comma segmented values"
		},{
			"html", "Web page"
		},{
			"sql", "SQL script"
		},{
			"xls", "Microsoft Excel Spreadsheet"
		}
	};
	
	private static final TableModelExportType[] LIST = {
			new TableModelExportType(0),
			new TableModelExportType(1),
			new TableModelExportType(2),
			new TableModelExportType(3)
	};
	
	public static final TableModelExportType CSV = LIST[0];
	public static final TableModelExportType HTML = LIST[1];
	public static final TableModelExportType SQL = LIST[2];
	public static final TableModelExportType XLS = LIST[3];
	
	// CONSTRUCTORS
	
	/**
	 * @param value
	 */
	private TableModelExportType(int value)
	{
		super(value);
	}
	
	// MEMBERS
	
	public String getFileDescription()
	{
		return FILE_TYPE_DATA[getValue()][1];
	}

	public String getFileExtension()
	{
		return FILE_TYPE_DATA[getValue()][0];
	}
	
	public FileFilter getFileFilter()
	{
		return new BasicFileFilter(getFileExtension(), getFileDescription());
	}
	
	// METHODS
	
	public static TableModelExportType fromFile(File file)
	{
		for (int i = 0; i < LIST.length; i++) {
			if (file.getName().indexOf(LIST[i].getFileExtension()) > 1) {
				return LIST[i];
			}
		}
		return null;
	}
	
	public static TableModelExportType[] list()
	{
		return LIST;
	}
	
	public String toString()
	{
		return getFileDescription();
	}
}
