package us.pcsw.util.tablemodelexport;

/**
 * us.pcsw.util.tablemodelexport.SQLExportHelper
 * -
 * Helps export table model data into a series of insert statements.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 14, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
final class SQLExportHelper extends ExportHelper
{
	StringBuffer insertBodySB;
	String insertBody;
	
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
	private static final String[] CONSTANTS =
		{
			null, null,
			"insert into %t (", "", ") values ",
			"(", ",", ");\n"
		};

	public SQLExportHelper(String title)
	{
		super(title);
	}

	/**
	 * @see us.pcsw.util.tablemodelexport.ExportHelper#getConstantArray()
	 */
	protected String[] getConstantArray()
	{
		return CONSTANTS;
	}
		
	private boolean first = true;
	public String buildColumnHeader(String columnName)
	{
		if (first) {
			first = false;
		} else {
			insertBodySB.append(',');
		}
		insertBodySB.append(columnName);
		return "";
	}
	
	public String buildDataColumn(Object data)
	{
		if (data instanceof Integer || data instanceof Long) {
			return INTEGER_FORMATTER.format(((Number)data).longValue());
		} else if (data instanceof Double || data instanceof Float) {
			return NUMBER_FORMATTER.format(((Number)data).doubleValue());
		} else if (data == null) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer("'");
			sb.append(super.buildDataColumn(data));
			sb.append("'");
			return sb.toString();
		}
	}
	
	String getDataRowLineStart()
	{
		return insertBody;
	}
	
	String getHeaderLineEnd()
	{
		insertBodySB.append(super.getHeaderLineEnd());
		insertBodySB.append(super.getDataRowLineStart());
		insertBody = insertBodySB.toString();
		return "";
	}
	
	String getHeaderLineStart()
	{
		insertBodySB = new StringBuffer(CONSTANTS[2].replace("%t", getTitle()));
		return "";
	}
}
