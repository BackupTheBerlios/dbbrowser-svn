/**
 * 
 */
package us.pcsw.dbbrowser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * us.pcsw.dbbrowser.LoadedResultSetTableModel
 * -
 * A subclass of ResultSetTableModel that loads the entire contents of the
 * ResultSet in at once so that the ResultSet can then be closed.
 * 
 * <STRONG>Note:</STRONG> For large result sets, this may use up large amounts
 * of memory.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public final class LoadedResultSetTableModel extends ResultSetTableModel
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 */
	public LoadedResultSetTableModel()
	{
		super();
		data = new ArrayList();
	}

	/**
	 * Creates a new instance and loads the resultset.
	 */
	public LoadedResultSetTableModel(ResultSet rs)
		throws SQLException
	{
		super(rs);
	}

	// MEMBERS
	
	// A list of Object arrays.  Each Object array is a row.  Each array
	// element is a cell.
	private List data;
	
	/**
	 * @see us.pcsw.dbbrowser.ResultSetTableModel#getValueAt(int, int, boolean)
	 */
	public Object getValueAt(int row, int column, boolean returnNulls)
	{
		Object o = ((Object[])data.get(row))[column];
		if (o == null) {
			o = Preferences.getRepresentationForNull();
		}
		return o;
	}
	
	/**
	 * @see us.pcsw.dbbrowser.ResultSetTableModel#isResultSetLoaded(java.sql.ResultSet)
	 */
	protected boolean isResultSetLoaded(ResultSet rs)
	{
		// I have no way of knowing.  It's unsafe to keep the ResultSet around
		// as it may get closed and who knows what else.  Can't keep its hash
		// because maybe the same ResultSet is reused by the JDBC
		// implentation; in which case, I'd return a false positive.
		return false;
	}

	/**
	 * @see us.pcsw.dbbrowser.ResultSetTableModel#loadResultSet(java.sql.ResultSet)
	 */
	protected void loadResultSet(ResultSet rs) throws SQLException
	{
		Object[] row = null;
		int colCount = getColumnCount();

		if (data == null) {
			data = new ArrayList();
		} else {
			data.clear();
		}
		while (rs.next()) {
			row = new Object[colCount];
			for (int i = 0; i < colCount; i++) {
				row[i] = rs.getString(i + 1);
				if (rs.wasNull()) {
					row[i] = null;
				}
			}
			data.add(row);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return data.size();
	}
}
