package us.pcsw.util.tablemodelexport;

import us.pcsw.util.StringUtil;

/**
 * us.pcsw.util.tablemodelexport.HTMLExportHelper
 * -
 * A description of this class.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 14, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
final class HTMLExportHelper extends ExportHelper
{	
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
			"<html>\n<head>\n<title>%t</title>\n</head>\n<body>\n<table>\n",
			"</table>\n</html>\n",
			"<tr><th>", "</th><th>", "</th></tr>\n",
			"<tr><td>", "</td><td>", "</td></tr>\n"
		};

	/**
	 * @param value
	 */
	protected HTMLExportHelper(String title)
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
	
	/**
	 * @see us.pcsw.util.tablemodelexport.ExportHelper#buildDataColumn(java.lang.Object)
	 */
	public String buildDataColumn(Object data)
	{
		String s = super.buildDataColumn(data);
		if (s == null) {
			return null;
		} else {
			s = StringUtil.replaceHTMLEntities(s);
			return s;
		}
	}
}
