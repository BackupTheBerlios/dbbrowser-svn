package us.pcsw.dbbrowser.swing.text;

import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;


public class StyleImpl extends SimpleAttributeSet implements Style
{

	private final String name;
	
	public StyleImpl(String name)
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void removeChangeListener(ChangeListener l)
	{
	}
	
	public void addChangeListener(ChangeListener l)
	{
	}

}
