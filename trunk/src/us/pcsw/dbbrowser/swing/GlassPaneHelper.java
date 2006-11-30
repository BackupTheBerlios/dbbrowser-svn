package us.pcsw.dbbrowser.swing;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class GlassPaneHelper
{

	private Component oldGlassPane;
	private JFrame frame;
	private JComponent pane;
	private Rectangle bounds;
	
	
	public GlassPaneHelper(){}	
	
	public void drawOnGlassPane(JFrame aFrame , JComponent aPane , Rectangle aBounds , boolean doBlock)
	{
		frame = aFrame;
		pane = aPane;
		bounds = aBounds;
		
		oldGlassPane = frame.getGlassPane();
		
		JComponent gp = new OurGlassPane();
		
		frame.setGlassPane(gp);

		frame.getGlassPane().setVisible(true);

		if(doBlock)gp.addMouseListener(new MouseAdapter(){});
	}
	
	
	
	public void dispose()
	{
		if(frame == null)
			{
			System.err.println("FRAME IS NULL");
			return;
			}
		frame.setGlassPane(oldGlassPane);
		frame.getGlassPane().setVisible(false);
	}
	
	
	private class OurGlassPane extends JComponent
	{
		
		public OurGlassPane()
		{
			initGui();
		}
		
		
		private void initGui()
		{
			setOpaque(false);
			setLayout(null);
			add(pane);
			pane.setBounds(bounds);
		}
	}
}
