package us.pcsw.dbbrowser.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;



public class Doc extends JPanel
{
	
	private JTabbedPane tabbedPane;
	private ActionListener listener;
	
	public Doc(JTabbedPane tabbedPane , ActionListener listener)
	{
		this.tabbedPane = tabbedPane;
		this.listener = listener;
		initGui();
	}
	
	private void initGui()
	{
		JButton close = new JButton(new ImageIcon(getClass().getResource("/us/pcsw/dbbrowser/resources/images/x.png")));
		close.setBorderPainted(false);
		close.setMargin(new Insets(1,1,1,1));
		close.setOpaque(false);
		close.addActionListener(listener);
		
		final JPanel header = new JPanel()
		{

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				//GradientPaint gp = new GradientPaint(0 , 0 ,new Color(170,170,170,255), getWidth(), getHeight() , Color.WHITE , true);
				
				Graphics2D g2d = (Graphics2D)g;
				
				//g2d.setPaint(gp);
				g2d.setColor(new Color(170,170,170,100));
				
				g2d.fill(getBounds());
				
			}
			
		};
		
		header.setLayout(new GridBagLayout());
		
		header.setPreferredSize(new Dimension(Integer.MAX_VALUE , 23));
		
		Bag bag = new Bag();
		header.add(Bag.spacer() , bag.fillX());
		header.add(close , bag.nextX().fillNone());
		
		setLayout(new GridBagLayout());
		
		bag = new Bag();
		
		add(header , bag.fillX());
		
		add(tabbedPane , bag.nextY().fillBoth());
		
	}
	
	
	
	
}
