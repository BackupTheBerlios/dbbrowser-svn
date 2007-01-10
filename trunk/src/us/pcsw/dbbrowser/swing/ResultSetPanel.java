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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.border.BevelBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import javax.swing.table.JTableHeader;

import us.pcsw.dbbrowser.CachingResultSetTableModel;
import us.pcsw.dbbrowser.Preferences;
import us.pcsw.dbbrowser.ResultSetTableModel;

import us.pcsw.swing.HorizontalStrut;

/**
 * us.pcsw.dbbrowser.swing.ResultSetPanel
 * -
 * A panel used to display result sets.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class ResultSetPanel extends JPanel
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;
	
	private static final String LOADED_CACHE_DESC = "Rows Cached: ";
	private static final String LOADED_COUNT_DESC = "Rows Loaded: ";
	private static final String X_DESC = "Row: ";
	private static final String Y_DESC = "Col: ";
	
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 */
	public ResultSetPanel()
	{
		// Initialize
		super(new BorderLayout());
		
		// Set up display
		results = new JTable(new CachingResultSetTableModel());
		results.setAutoCreateColumnsFromModel(true);
		results.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		results.setCellSelectionEnabled(true);
		resultsScrollPane = new JScrollPane();
		resultsScrollPane.setVerticalScrollBarPolicy
		    (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsScrollPane.setHorizontalScrollBarPolicy
		    (JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		resultsScrollPane.setViewportView(results);
		resultsScrollPane.setColumnHeaderView
		    (results.getTableHeader());
		resultsScrollPane.setPreferredSize(new Dimension(450,250));
		add(resultsScrollPane, BorderLayout.CENTER);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		infoLabel = new JLabel(" ");
		infoLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		Dimension dim = infoLabel.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		infoLabel.setPreferredSize(dim);
		statusPanel.add(infoLabel);
		positionXLabel = new JLabel(X_DESC);
		positionXLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		dim = positionXLabel.getPreferredSize();
		dim.width = 75;
		positionXLabel.setMaximumSize(dim);
		positionXLabel.setMinimumSize(dim);
		positionXLabel.setPreferredSize(dim);
		statusPanel.add(positionXLabel);
		statusPanel.add(new HorizontalStrut(2));
		dim = dim.getSize();
		positionYLabel = new JLabel(Y_DESC);
		positionYLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		positionYLabel.setMaximumSize(dim);
		positionYLabel.setMinimumSize(dim);
		positionYLabel.setPreferredSize(dim);
		statusPanel.add(positionYLabel);
		rowCountLabel = new JLabel(LOADED_COUNT_DESC);
		statusPanel.add(new HorizontalStrut(2));
		dim = dim.getSize();
		dim.width = 200;
		rowCountLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rowCountLabel.setMaximumSize(dim);
		rowCountLabel.setMinimumSize(dim);
		rowCountLabel.setPreferredSize(dim);
		statusPanel.add(rowCountLabel);
		add(statusPanel, BorderLayout.SOUTH);
		
		// Install listeners
		listener = new Listener();
		results.getTableHeader().addMouseListener(listener);
		results.getSelectionModel().addListSelectionListener(listener);
		results.getColumnModel().addColumnModelListener(listener);
	}
	
	// MEMBERS
	
	// Table for displaying results
	private JLabel infoLabel;
	private Listener listener;
	private JLabel positionXLabel;
	private JLabel positionYLabel;
	private JTable results;
	private JScrollPane resultsScrollPane;
	private JLabel rowCountLabel;
	
	ResultSetTableModel getResultSetTableModel()
	{
		return (ResultSetTableModel)results.getModel();
	}
	
	private void selectColumn(int colIndex)
	{
		results.setColumnSelectionInterval(colIndex, colIndex);
		results.getSelectionModel().setSelectionInterval(
				0, results.getModel().getRowCount() - 1
			);
	}
	
	void setResultSetTableModel(ResultSetTableModel model)
	{
		if (model == null) {
			model = new CachingResultSetTableModel();
		}
		results.setModel(model);
		updateRowCount();
		model.addTableModelListener(listener);
	}
	
	// METHODS

	void maximizeColumnHeader(JTableHeader tableHeader, int columnIndex)
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// The user clicked on the right boarder, resize the column
		// as per best fit.
		Component cellrenderer = null;
		int desiredWidth = 0;
		int maxWidth = 0;
		// Resize the column to best fit.
		int rowCount = 0;
		ResultSetTableModel rstm = getResultSetTableModel();
		if (rstm instanceof CachingResultSetTableModel) {
			rowCount = ((CachingResultSetTableModel)rstm).getCachedRowCount();
		} else {
			rowCount = rstm.getRowCount();
		}
		for (int i = 0; i < rowCount; i++) {
			cellrenderer = results.getCellRenderer(i, columnIndex).getTableCellRendererComponent(results, results.getValueAt(i, columnIndex), false, false, i, columnIndex);
			desiredWidth = cellrenderer.getPreferredSize().width;
			if (desiredWidth > maxWidth) {
				maxWidth = desiredWidth;
			}
		}
		tableHeader.getColumnModel().getColumn(columnIndex).setPreferredWidth(maxWidth + 10);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	void reloadPreferences()
	{
		results.setFont(Preferences.getResultsFont());
	}
	
	private void updatePosition()
	{
		int x = results.getSelectedRow() + 1;
		int y = results.getSelectedColumn() + 1;
		positionXLabel.setText(X_DESC + (x == 0 ? "" : String.valueOf(x)));
		positionYLabel.setText(Y_DESC + (y == 0 ? "" : String.valueOf(y)));
		
		BigDecimal total = null;
		if (
				results.getSelectedColumnCount() == 1 &&
				results.getSelectedRowCount() > 1
			)
		{
			// If possible, sum the selected cells
			int c = results.getSelectedColumn();
			Object o;
			total = BigDecimal.ZERO;
			int[] rows = results.getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				o = results.getValueAt(rows[i], c);
				try {
					total = total.add(new BigDecimal(o.toString()));
				} catch (NumberFormatException nfe) {
					total = null;
					break;
				}
			}
		} else if (
				results.getSelectedRowCount() == 1 &&
				results.getSelectedColumnCount() > 1
			)
		{
			// If possible, sum the selected cells
			int r = results.getSelectedRow();
			Object o;
			total = BigDecimal.ZERO;
			int[] columns = results.getSelectedColumns();
			for (int i = 0; i < columns.length; i++) {
				o = results.getValueAt(r, columns[i]);
				try {
					total = total.add(new BigDecimal(o.toString()));
				} catch (NumberFormatException nfe) {
					total = null;
					break;
				}
			}
		}
		if (total == null) {
			infoLabel.setText(" ");
		} else {
			infoLabel.setText("Sum: " + total.toString());
		}
	}
	
	private void updateRowCount()
	{
		ResultSetTableModel rstm = (ResultSetTableModel)results.getModel();
		int count = 0;
		String prefix = null;
		prefix = LOADED_COUNT_DESC;
		if (rstm instanceof CachingResultSetTableModel) {
			count = ((CachingResultSetTableModel)rstm).getCachedRowCount();
			if (! ((CachingResultSetTableModel)rstm).getCacheFull()) {
				prefix = LOADED_CACHE_DESC;
			}
		} else {
			count = rstm.getRowCount();
		}
		rowCountLabel.setText(prefix + String.valueOf(count));
	}
	
	private class Listener extends MouseAdapter
		implements
			ListSelectionListener, TableColumnModelListener, TableModelListener
	{
		Listener()
		{
			super();
		}
		
		// List Selection Listener
		
		public void valueChanged(ListSelectionEvent e)
		{
			updatePosition();
		}
	
		// Mouse Listener
		
		public void mouseClicked(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 2) {
					JTableHeader tableHeader = results.getTableHeader();
					if (e.getSource() == tableHeader) {
						// Determine which column header was double-clicked and what
						// position of it is in the container.
						int column = tableHeader.columnAtPoint(e.getPoint());
						Rectangle rect = tableHeader.getHeaderRect(column);
						// Allow 5 pixels to the left and right of the column's
						// right boarder for double-click
						int leftBound = rect.x + rect.width - 5;
						int rightBound = rect.x + rect.width + 5;
						if (e.getX() > leftBound && e.getX() < rightBound) {
							maximizeColumnHeader(tableHeader, column);
						}
					}
				} else if (e.getClickCount() == 1) {
					JTableHeader tableHeader = results.getTableHeader();
					if (e.getSource() == tableHeader) {
						// Determine which column header was clicked and what
						// position of it is in the container.
						int column = tableHeader.columnAtPoint(e.getPoint());
						Rectangle rect = tableHeader.getHeaderRect(column);
						// Discount 5 pixels to the left and right of the
						// column's right boarder for double-click
						int leftBound = rect.x;
						int rightBound = rect.x + rect.width - 5;
						if (e.getX() > leftBound && e.getX() < rightBound) {
							selectColumn(column);
						}
					}
				}
			}
		}
		
		// Table Model Column Listener
		
		 public void columnAdded(TableColumnModelEvent e){}
		 public void columnMarginChanged(ChangeEvent e) {}
		 public void columnMoved(TableColumnModelEvent e) {}
		 public void columnRemoved(TableColumnModelEvent e) {}
		 
		 public void columnSelectionChanged(ListSelectionEvent e)
		 {
			 updatePosition();
		 } 
		 
		 // Table Changed
		 
		 public void tableChanged(TableModelEvent e) {
			 updateRowCount();
		 }
	}
}
