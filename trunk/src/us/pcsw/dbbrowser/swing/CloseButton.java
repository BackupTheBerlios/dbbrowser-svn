package us.pcsw.dbbrowser.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.UIResource;

public class CloseButton extends JButton implements UIResource
{

	
	private Icon[] iconset; 
	final int index;
	final Icon icon;
	
	public int getIndex(){
		return index;
	}

	public CloseButton(final int index , ActionListener listener , ImageIcon icon)
	{
		super();
		this.icon = icon;
		this.index = index;
		
		iconset = new Icon[]{ new CloseIcon(icon ), new CloseIcon(icon), new CloseIcon(icon),};
		
		
		
		setIcon(iconset[0]);
		setBorderPainted(false);
		setFocusable(false);
		setContentAreaFilled(false);
		setToolTipText("Close Tab ");
		addActionListener(listener);
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e)
			{
				setIcon(iconset[1]);
			}
			public void mouseExited(MouseEvent e)
			{
				setIcon(iconset[0]);
			}
			public void mousePressed(MouseEvent e)
			{
				setIcon(iconset[2]);
			}
		});
	}
	
	
	
	
	class CloseIcon implements Icon
	{

		final int sz = 10;
		final ImageIcon icon;

		public int getIconWidth()
		{
			return sz;
		}


		public int getIconHeight()
		{
			return sz;
		}

		


		public CloseIcon(ImageIcon icon)
		{
			this.icon = icon;
		}

		
	    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
	           g.drawImage(icon.getImage() , x, y, c);
	    }

	}
	
	
} 