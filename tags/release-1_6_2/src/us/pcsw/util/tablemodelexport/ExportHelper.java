package us.pcsw.util.tablemodelexport;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * us.pcsw.util.tablemodelexport.ExportHelper
 * -
 * A type identifier used to identify what type of file the table model's
 * data should be exported to as well as some type based utility methods used
 * by the exporter.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 14, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
abstract class ExportHelper extends Object
{
	protected static final DateFormat DATE_FORMATTER = DateFormat.getDateInstance();
	protected static final NumberFormat INTEGER_FORMATTER = NumberFormat.getIntegerInstance();
	protected static final NumberFormat NUMBER_FORMATTER = NumberFormat.getInstance();
	
	// CONSTRUCTORS
	
	/**
	 * @param value The type index
	 */
	ExportHelper(String title)
	{
		super();
		setTitle(title);
	}
	
	// MEMBERS
	
	/*
	 * File types and related constants.  The second dimension elements are:
	 *		 0	-	File header text, may be null
	 *		 1	-	File footer text, may be null
	 * 		 2	-	Line beginning for column headers, may be null
	 * 		 3	-	Delimeter for column headers
	 * 		 4	-	Line ending for column headers, may be null
	 * 		 5	-	Line beginning for data row, may be null
	 * 		 6	-	Delimeter for data columns
	 * 		 7	-	Line ending for data row, may be null
	 */
	protected abstract String[] getConstantArray();
	
	String getDataDelimeter()
	{
		return getConstantArray()[6];
	}
	
	String getDataRowLineEnd()
	{
		return getConstantArray()[7];
	}
	
	String getDataRowLineStart()
	{
		return getConstantArray()[5];
	}
	
	public String getFileFooter()
	{
		if (getConstantArray()[1] == null) {
			return null;
		} else {
			return getConstantArray()[1].replaceAll("%t", getTitle());
		}
	}
	
	public String getFileHeader()
	{
		if (getConstantArray()[0] == null) {
			return null;
		} else {
			return getConstantArray()[0].replaceAll("%t", getTitle());
		}
	}
	
	String getHeaderDelimeter()
	{
		return getConstantArray()[3];
	}
	
	String getHeaderLineEnd()
	{
		return getConstantArray()[4];
	}
	
	String getHeaderLineStart()
	{
		return getConstantArray()[2];
	}
	
	private String title;
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	// MEMBERS
	
	/**
	 * Takes the data object and produces a String that can be used to export
	 *  the data.
	 */
	public String buildDataColumn(Object data)
	{
		if (data instanceof Calendar) {
			return DATE_FORMATTER.format(((Calendar)data).getTime());
		} else if (data instanceof Date) {
			return DATE_FORMATTER.format((Date)data);
		} else if (data instanceof Integer || data instanceof Long) {
			return INTEGER_FORMATTER.format(((Number)data).longValue());
		} else if (data instanceof Double || data instanceof Float) {
			return NUMBER_FORMATTER.format(((Number)data).doubleValue());
		} else if (data == null) {
			return "";
		} else {
			return data.toString().trim();
		}
	}
	
	public String buildColumnHeader(String columnName)
	{
		return buildDataColumn(columnName);
	}
}
