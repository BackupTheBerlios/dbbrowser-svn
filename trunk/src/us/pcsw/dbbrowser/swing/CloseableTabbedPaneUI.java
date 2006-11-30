package us.pcsw.dbbrowser.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;


import java.util.*;

/**
 * Hmmm.. from the sun java forums, I think
 */
public class CloseableTabbedPaneUI extends BasicTabbedPaneUI
{

	private ImageIcon icon;
	private ActionListener listener;
	
	public CloseableTabbedPaneUI(ImageIcon icon , ActionListener listener){
		this.icon = icon;
		this.listener = listener;
	}
	
	
	/*
	 * override to return our layoutmanager
	 * 
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#createLayoutManager()
	 */
	protected LayoutManager createLayoutManager()
	{
		return new PlafLayout();
	}


	/*
	 * add 40 to the tab size to allow room for the close button and 8 to the
	 * height
	 * 
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#getTabInsets(int, int)
	 */
	protected Insets getTabInsets(int tabPlacement, int tabIndex)
	{
		// note that the insets that
		// are returned to us are not copies.
		Insets defaultInsets = (Insets)super.getTabInsets(tabPlacement,tabIndex).clone();
		
		defaultInsets.top = 0;
		defaultInsets.right += 12;
		defaultInsets.bottom = 0;
		return defaultInsets;
	}
	

	/**
	 * layout mgr
	 *
	 */
	protected class PlafLayout extends TabbedPaneLayout
	{

		Vector<CloseButton> closeButtons = new Vector<CloseButton>();

		public void layoutContainer(Container parent)
		{
			super.layoutContainer(parent);

			while(tabPane.getTabCount() > closeButtons.size())
				closeButtons.add(new CloseButton(closeButtons.size() , listener , icon));

			Rectangle rect = new Rectangle();
			int i;
			for(i = 0; i < tabPane.getTabCount(); i++)
			{
				rect = getTabBounds(i, rect);
				JButton closeButton = (JButton)closeButtons.get(i);
				closeButton.setLocation(rect.x + rect.width - 13 , rect.y + 3);
				closeButton.setSize(10, 10);
				tabPane.add(closeButton);
			}
			for(; i < closeButtons.size(); i++)
				tabPane.remove((JButton)closeButtons.get(i));
		}

		
		

	}
}

