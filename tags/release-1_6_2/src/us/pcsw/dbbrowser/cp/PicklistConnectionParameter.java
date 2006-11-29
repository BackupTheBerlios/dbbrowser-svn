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
package us.pcsw.dbbrowser.cp;

/**
 * us.pcsw.dbbrowser.cp.PicklistConnectionParameter
 * -
 * Allows the user to select from a list of options.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Apr 27, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public final class PicklistConnectionParameter extends ConnectionParameter
{
	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS
	
	/**
	 * Creates a new instance of PicklistConnectionParameter that does not
	 * allow multiselect and has no default value.
	 * @param name The name of the parameter.
	 * @param required Whether the parameter is required.
	 * @param picklist The list of object from which the user should choose.
	 */
	public PicklistConnectionParameter(
			String name,
			boolean required,
			String[] picklist
		)
	{
		this(name, required, picklist, false, null);
	}

	/**
	 * Creates a new instance of PicklistConnectionParameter with no default
	 * value.
	 * @param name The name of the parameter.
	 * @param required Whether the parameter is required.
	 * @param picklist The list of object from which the user should choose.
	 * @param allowMultiSelect Whether the user may choose more than one item.
	 */
	public PicklistConnectionParameter(
			String name,
			boolean required,
			String[] picklist,
			boolean allowMultiSelect
		)
	{
		this(name, required, picklist, allowMultiSelect, null);
	}
	

	/**
	 * Creates a new instance of PicklistConnectionParameter.
	 * @param name The name of the parameter.
	 * @param required Whether the parameter is required.
	 * @param picklist The list of object from which the user should choose.
	 * @param allowMultiSelect Whether the user may choose more than one item.
	 * @param value The default value.
	 */
	public PicklistConnectionParameter(
			String name,
			boolean required,
			String[] picklist,
			boolean allowMultiSelect,
			String[] value
		)
	{
		super(name, required, value);
		isMultiSelectAllowed(allowMultiSelect);
		this.picklist = picklist;
	}
	
	// MEMBERS
	
	/**
	 * A convenience method to determine if the indicated picklist item has
	 * been selected by the user.
	 * @param index The index of the item in the picklist.
	 */
	public boolean isItemSelected(int index)
	{
		boolean returnVal = false;
		String[] selected = (String[])getValue();
		for (int i = 0; selected != null && i < selected.length; i++) {
			if (picklist[index].equals(selected[i])) {
				returnVal = true;
				break;
			}
		}
		return returnVal;
	}
	
	private boolean allowMultiSelect;
	public boolean isMultiSelectAllowed()
	{
		return allowMultiSelect;
	}
	private void isMultiSelectAllowed(boolean allowed)
	{
		this.allowMultiSelect = allowed;
	}
	
	private String[] picklist;
	public String[] getPickList()
	{
		return picklist;
	}
	protected void setPickList(String[] picklist)
	{
		this.picklist = picklist;
	}
	
	public void setValue(Object value)
		throws IllegalArgumentException
	{
		String[] selections = null;
		if (value instanceof String[]) {
			selections = (String[])value;
		} else if (value instanceof Object[]) {
			selections = new String[((Object[])value).length];
			for (int i = 0; i < selections.length; i++) {
				selections[i] = ((Object[])value)[i].toString();
			}
		} else if (value instanceof Object) {
			selections = new String[]{value.toString()};
		}
		
		// Make sure they gave us the right number of values.
		if ((selections == null || selections.length == 0) && isRequired()) {
			StringBuffer msg = new StringBuffer();
			if (isMultiSelectAllowed()) {
				msg.append("At least o");
			} else {
				msg.append("O");
			}
			msg.append("ne item must be selected for parameter ");
			msg.append(getName());
			msg.append('.');
			throw new IllegalArgumentException(msg.toString());
		} else if (
				selections != null &&
				selections.length > 1 &&
				(! isMultiSelectAllowed()))
		{
			throw new IllegalArgumentException(
					"Only one item may be selected for parameter " +
					getName() + '.'
				);
		}
		// Make sure that the values they gave are of the list
		boolean found;
		for (int i = 0; selections != null && i < selections.length; i++) {
			found = false;
			for (int j = 0; j < picklist.length; j++) {
				if (selections[i].equals(picklist[j])) {
					found = true;
					break;
				}
			}
			if (! found) {
				throw new IllegalArgumentException(
						"Invalid value for parameter " +
						getName() +
						".  Only items in the pick list may be selected."
					);
			}
		}
		super.setValue(selections);
	}
}
