/*
 * DBBrowser is software for browsing the structure and contents of databases.
 * Copyright (C) 2001 Philip A. Chapman
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *                     Free Software Foundation, Inc.
 *                    51 Franklin Street, Fifth Floor
 *                      Boston, MA  02110-1301, USA.
 */
package us.pcsw.dbbrowser.swing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import us.pcsw.dbbrowser.ResultSetTableModel;

/**
 *  us.pcsw.dbbrowser.swing.ReportPrinter
 * -
 * This class creates a report based on a resultset.
 *
 * <P><B>Revision History:</B><UL>
 * <LI> Nov 19, 2003 This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public class ReportPrinter
	implements Printable
{
	ReportColumn columnWidths[] = null;
	
	/**
	 * The resultset table model which contains the data to be reported.
	 */
	ResultSetTableModel resultsetTblModel = null;
	/**
	 * Creates a new instance.
	 * @param rtm The resultset table model which contains the data to be reported.
	 */
	public ReportPrinter(ResultSetTableModel rtm)
	{
		super();
		
		resultsetTblModel = rtm;
		
		calculateColumnCharacterWidths();
	}

	private void calculateColumnCharacterWidths()
	{
		int column;
		Object obj;
		int row;
		
		columnWidths = new ReportColumn[resultsetTblModel.getColumnCount()];
		for (row = 0; row < resultsetTblModel.getRowCount(); row++) {
			for (column = 0; column < columnWidths.length; column++) {
				obj = resultsetTblModel.getValueAt(row, column, true);
				if (columnWidths[column] == null) {
					columnWidths[column] = new ReportColumn();
				}
				if (obj == null) {
					columnWidths[column].addRow(0);
				} else {
					columnWidths[column].addRow(obj.toString().length());
				}	
			}
		}
	}

	private Font calculateColumnFontWidths(Graphics graphics)
	{
		boolean calculated = false;
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		int fontWidth;
		int i;
		int panelWidth;
		int totalCharWidth;
		
		// Try to find a font size that will allow all columns to be displayed
		// in full.
		while (! calculated && font.getSize() > 7) {
			calculated = true;  // Assume this font size will work.
			panelWidth = graphics.getClipBounds().width; // Get the width of the area we can draw on.
			fontWidth = graphics.getFontMetrics(font).getMaxAdvance(); // Get the maximum character width
			
			// Determine how many characters there are in total;
			totalCharWidth = columnWidths.length - 1;
			for (i = 0; i < columnWidths.length; i++) {
				totalCharWidth += columnWidths[i].getMaxWidth();
			}
			
			// Account for one character's space between each column.
			panelWidth = panelWidth - ((columnWidths.length - 1) * fontWidth);
						 
			for (i = 0; i < columnWidths.length; i++) {
				columnWidths[i].setPercentOfWhole
					(columnWidths[i].getMaxWidth() / totalCharWidth);
				panelWidth =
					panelWidth - columnWidths[i].getMaxWidth() * fontWidth;
				if (panelWidth < 0) {
					calculated = false;
					break;
				}
			}
			
			// The font didn't work.  Decrease the font size; but go no lower
			// than size 8.
			if (! calculated && font.getSize() > 8) {
				font  =
					new Font("Monospaced", Font.PLAIN, font.getSize() + 1);
			}
		}
		
		if (! calculated) {
			 //	Try to find a font size that will allow the average line of all
			 // columns to be displayed.
			while (! calculated && font.getSize() > 7) {
				calculated = true;
				panelWidth = graphics.getClipBounds().width;
				fontWidth = graphics.getFontMetrics(font).getMaxAdvance();

				// Determine how many characters there are in total;
				totalCharWidth = columnWidths.length - 1;
				for (i = 0; i < columnWidths.length; i++) {
					totalCharWidth += columnWidths[i].getAverageWidth();
				}
				
				// Account for one character's space between each column.
				panelWidth = panelWidth - ((columnWidths.length - 1) * fontWidth);
				
				for (i = 0; i < columnWidths.length; i++) {
					columnWidths[i].setPercentOfWhole
						(columnWidths[i].getAverageWidth() / totalCharWidth);
					panelWidth =
						panelWidth - columnWidths[i].getMaxWidth() * fontWidth;
					if (panelWidth < 0) {
						calculated = false;
						break;
					}
				}
			}
		}
		
		// Calculate how wide each column can be based on the chosen font.
		panelWidth = graphics.getClipBounds().width;
		fontWidth = graphics.getFontMetrics(font).getMaxAdvance();
		totalCharWidth = panelWidth / fontWidth - (columnWidths.length - 1);
		for (i = 0; i < columnWidths.length; i++) {
			columnWidths[i].setPaintWidth
				(
					new Float
						(
							totalCharWidth / columnWidths[i].getPercentOfWhole()
						).intValue()
				);
		}
		
		return font;
	}

	public void paint(Graphics g, int pageIndex)
	{
		Font font;
		int firstLine;
		int fontHeight;
		int fontWidth;
		int i;
		int j;
		int lastLine;
		int lineCount;
		String str;
		int xPos;
		int yPos;
		
		// Determine what font to use
		font = calculateColumnFontWidths(g);
		g.setFont(font);
		fontWidth = g.getFontMetrics().getMaxAdvance();
		
		// Determine how many lines can fit on the drawing area.
		fontHeight = g.getFontMetrics(font).getHeight();
		lineCount = g.getClipBounds().height / fontHeight;
		
		// Determine which lines are to be drawn; be sure to account for the
		// column headers.
		if (pageIndex == 0) {
			firstLine = 0;
			lastLine = lineCount - 1;
			
			// Print the column headers;
			xPos = 0;
			yPos = 0;
			for (i = 0; i < columnWidths.length; i++) {
				str = resultsetTblModel.getColumnName(i).substring(0, columnWidths[i].getPaintWidth() - 1);
				g.drawString(str, xPos, yPos);
				xPos = xPos + (fontWidth * (columnWidths[i].getPaintWidth() + 1));
			}
			
			xPos = 0;
			yPos = fontHeight;
		} else {
			firstLine = pageIndex * lineCount  - 1;
			lastLine = firstLine + lineCount - 1;
			
			xPos = 0;
			yPos = 0;
		}
		
		// Print the rows.
		for (i = firstLine; i <= lastLine; i++) {
			for (j = 0; j < columnWidths.length; j++) {
				str = resultsetTblModel.getValueAt(i, j, false).toString().substring(0, columnWidths[i].getPaintWidth() - 1);
				g.drawString(str, xPos, yPos);
				xPos = xPos + (fontWidth * (columnWidths[i].getPaintWidth() + 1));
			}
		}
	}

	/**
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
		throws PrinterException
	{
		// TODO Auto-generated method stub
		return 0;
	}
}

class ReportColumn extends Object
{
	private int maxColumnWidth = -1;
	private int minColumnWidth = 0;
	private int paintWidth = 0;
	private float percentOfWhole = 0;
	private int rowCount = 0;
	private int totalColumnWidth = 0;
	
	ReportColumn()
	{
		super();
	}
	
	void addRow(int rowWidth)
	{
		rowCount++;
		totalColumnWidth += rowWidth;
		if (rowWidth > maxColumnWidth) {
			maxColumnWidth = rowWidth;
		} else if (minColumnWidth == -1 || rowWidth < minColumnWidth) {
			minColumnWidth = rowWidth;
		}
	}
	
	void addRow(String rowValue)
	{
		addRow(rowValue.length());
	}
	
	int getAverageWidth()
	{
		return totalColumnWidth / rowCount;
	}
	
	int getPaintWidth()
	{
		return paintWidth;
	}
	
	float getPercentOfWhole()
	{
		return percentOfWhole;
	}
	
	int getMaxWidth()
	{
		return maxColumnWidth; 
	}
	
	int getMinWidth()
	{
		return minColumnWidth;
	}
	
	void setPaintWidth(int width)
	{
		paintWidth = width;
	}
	
	void setPercentOfWhole(float percent)
	{
		percentOfWhole = percent;
	}
}	