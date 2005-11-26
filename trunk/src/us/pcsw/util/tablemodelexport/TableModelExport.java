package us.pcsw.util.tablemodelexport;

import java.awt.Component;
import java.awt.Cursor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.filechooser.FileFilter;

import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import us.pcsw.swing.WrappedOptionPane;

import us.pcsw.util.StringUtil;


/**
 * com.alliancemanaged.simis.gui.models.TableModelExport
 * -
 * A utility class which will save a table's data to file.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 12, 2005 This class was created by pchapman.</LI>
 * <LI>Oct 11, 2005 Fixed a bug in the exportData method where the outWriter
 *                  was not being closed and data was not being flushed to the
 *                  output stream.  pchapman</LI>
 * </UL></P>
 */
public class TableModelExport
{	
	public static void saveTableModel(
			Component parent, TableModel dataModel, String title
		)
		throws IOException
	{
		saveTableModel(parent, dataModel, title, TableModelExportType.list());
	}
	
	public static void saveTableModel(
			Component parent, TableModel dataModel, String title,
			TableModelExportType[] validTypes
		)
		throws IOException
	{
	    // Initialize
    	File file = null;
	    TableModelExportType exportType = null;
	    int response;
	    String s;
		
		// Lazely create the save result file chooser.
	    JFileChooser resultChooser = new JFileChooser();
	    FileFilter filter = resultChooser.getFileFilter();
		resultChooser.removeChoosableFileFilter(filter);
		for (int i = 0; i < validTypes.length; i++) {
			if (i == 0) {
				filter = validTypes[i].getFileFilter();
				resultChooser.addChoosableFileFilter(filter);
			} else {
				resultChooser.addChoosableFileFilter(
						validTypes[i].getFileFilter()
					);				
			}
		}
	    resultChooser.setDialogTitle("Save Data To File");
		resultChooser.setFileFilter(filter);

	    // Get the path/name of the file to save the file to.
	    response = resultChooser.showSaveDialog(parent);
    	if (response == JFileChooser.APPROVE_OPTION) {
			file = resultChooser.getSelectedFile();
			
			// Determine which file type is required.
			s = file.getName();
			for (int i = 0; i < validTypes.length; i++) {
				if (s.indexOf(validTypes[i].getFileExtension()) > 1) {
					exportType = validTypes[i];
					break;
				}
			}
			
			if (exportType == null) {
				// The chosen file is not of the correct type.
				StringBuffer msg = new StringBuffer("Please choose a file with the type ");
				String[] words = new String[validTypes.length];
				for (int i = 0; i < validTypes.length; i++) {
					words[i] = validTypes[0].getFileDescription();
				}
				msg.append(StringUtil.createGrammaticalList(words, StringUtil.GRAMMATICAL_LIST_TYPE_EXCLUSIVE));
				msg.append('.');
				WrappedOptionPane.showWrappedMessageDialog(
						parent, msg.toString(), "Invalid File type",
						WrappedOptionPane.INFORMATION_MESSAGE
					);
				// Try again.
				saveTableModel(parent, dataModel, title);
				return;
			}
			
			if (exportType == TableModelExportType.SQL) {
				title = JOptionPane.showInputDialog(
						null,
						"What is the name of the table " +
						"to insert the data into?",
						"Insert Table Name",
						JOptionPane.QUESTION_MESSAGE
					);
				if (title == null) {
					return; 
				}
			}
			
			// Test for file's existance and user's willingness to
			//  overwrite it.
			if (file.exists()) {
			    response = JOptionPane.showConfirmDialog
					(null, 
					 "Overwrite the existing file " + file.getName() + "?",
					 "Overwrite Existing File",
					 JOptionPane.YES_NO_OPTION);
			} else {
			    response = JOptionPane.YES_OPTION;
			}
			if (response == JOptionPane.YES_OPTION) {
			    // Save the data
				parent.setCursor(
						Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
					);
				try {
					FileOutputStream outStream = new FileOutputStream(file);
					exportData(exportType, dataModel, outStream, title);
					outStream.close();
				} catch (IOException ioe) {
				    parent.setCursor(
				    		Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
						);
				    throw ioe;
				}
			    parent.setCursor(
			    		Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
					);
			}
		}
	}
	
	public static void exportData(
			TableModelExportType exportType, TableModel dataModel,
			OutputStream outStream, String title
		)
		throws IOException
	{
		int columns;
		Object obj;
		String s;
		ExportHelper helper = null;

		if (exportType == TableModelExportType.CSV) {
			helper = new CSVExportHelper(title);
		} else if (exportType == TableModelExportType.HTML) {
			helper = new HTMLExportHelper(title);
		} else if (exportType == TableModelExportType.SQL) {
			helper = new SQLExportHelper(title);
		} else if (exportType == TableModelExportType.XLS) {
			exportDataToXML(exportType, dataModel, outStream, title);
			return;
		}
		
		Writer outWriter = new OutputStreamWriter(outStream);
		
	    // Append a file header if needed
		s = helper.getFileHeader();
	    if (s != null) {
	    	outWriter.write(s);
	    }
	    
	    // Create column headers.
	    s = helper.getHeaderLineStart();
	    if (s != null) {
	    	outWriter.write(s);
	    }
		columns = dataModel.getColumnCount();
	    for (int i = 0; i < columns; i++) {
			if (i > 0) {
				outWriter.write(helper.getHeaderDelimeter());
			}
			outWriter.write(helper.buildColumnHeader(dataModel.getColumnName(i)));
	    }
	    s = helper.getHeaderLineEnd();
	    if (s != null) {
	    	outWriter.write(s);
	    }

		// Write the data, dataModel.getRowCount() may
		// change if not all results are cached, so make the
		// method call each time.
	    for (int i = 0; i < dataModel.getRowCount(); i++) {
	    	s = helper.getDataRowLineStart();
	    	if (s != null) {
	    		outWriter.write(s);
	    	}
			if (i < dataModel.getRowCount()) {
			    for (int j = 0; j < columns; j++) {
					obj = dataModel.getValueAt(i, j);
					if (j > 0) {
						outWriter.write(helper.getDataDelimeter());
					}
					outWriter.write(helper.buildDataColumn(obj));
			    }
		    	s = helper.getDataRowLineEnd();
		    	if (s != null) {
		    		outWriter.write(s);
		    	}
			}
	    }
		
	    // Append a file footer if needed
		s = helper.getFileFooter();
	    if (s != null) {
	    	outWriter.write(s);
	    }
	    
	    outWriter.close();
	}
	
	private static void exportDataToXML(
			TableModelExportType exportType, TableModel dataModel,
			OutputStream outStream, String title
		)
		throws IOException
	{
		Object o = null;
		HSSFCell cell;
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet(title);

	    HSSFRow row = sheet.createRow(0);
	    HSSFFont font = wb.createFont();
	    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    HSSFCellStyle style = wb.createCellStyle();
	    style.setFont(font);
	    for (int y = 0; y < dataModel.getColumnCount(); y++) {
	    	cell = row.createCell((short)y);
	    	cell.setCellValue(dataModel.getColumnName(y));
	    	cell.setCellStyle(style);
	    }
	    
	    // Rows and and cells are 0 based.
	    for (int i = 0; i < dataModel.getRowCount(); i++) {
	    	row = sheet.createRow(i + 1);
	    	for (int y = 0; y < dataModel.getColumnCount(); y++) {
	    		o = dataModel.getValueAt(i, y);
	    		// Create a cell and put a value in it.
	    		cell = row.createCell((short)y);
	    		if (o != null) {
	    			if (o instanceof Boolean) {
	    				cell.setCellValue(((Boolean)o).booleanValue());
	    			} else if (o instanceof Calendar) {
	    				cell.setCellValue((Calendar)o);
	    			} else if (o instanceof Date) {
	    				cell.setCellValue((Date)o);
	    			} else if (o instanceof Number) {
	    				cell.setCellValue(((Number)o).doubleValue());
	    			} else {
	    				cell.setCellValue(o.toString());
	    			}
	    		}
	    	}
	    }

	    // Write the output to a file
	    wb.write(outStream);
	}
}
