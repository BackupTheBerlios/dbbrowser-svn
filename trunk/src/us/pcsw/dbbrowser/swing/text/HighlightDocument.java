package us.pcsw.dbbrowser.swing.text;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.Utilities;

import us.pcsw.dbbrowser.swing.text.lexers.JavaLexer;
import us.pcsw.dbbrowser.swing.text.lexers.SqlLexer;




public class HighlightDocument extends DefaultStyledDocument
{
	private JTextPane pane;
	private Style defaultAtts;
	private Lexer lexer;
	
	private static class JavaType implements SyntaxType{};
	private static class SqlType implements SyntaxType{};
	
	public static interface SyntaxType
	{
	}
	
	public static final SyntaxType JAVA_TYPE = new JavaType();
	public static final SyntaxType SQL_TYPE = new SqlType();
	
	
	public HighlightDocument(JTextPane pane , SyntaxType type)
	{
		this.pane = pane;
		
		initDefaultAtts(2);
		
		if(type == JAVA_TYPE) lexer = new JavaLexer();
		else lexer = new SqlLexer();
	}
	
	public HighlightDocument(JTextPane pane , Lexer lexer)
	{
		this.pane = pane;
		
		initDefaultAtts(2);
		
		this.lexer = lexer;
		
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	{
		if (str.length() == 0 || offs < 0) return;

		super.insertString(offs, str, defaultAtts);

		try
		{
			parseTokens(offs, str.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private synchronized void parseTokens(int offset , int len) throws BadLocationException, IOException
	{
		int start = offset;
		int length = len;
		int end = offset + len;
		
		String s = "";
		
		if(len < 10)
		{
			try
			{
				start = Utilities.getRowStart(pane , offset);
			}catch (Exception e) 
			{
				try
				{
					start = Utilities.getPreviousWord(pane, offset);
				}catch (Exception e2) 
				{
					try
					{
						start = Utilities.getWordStart(pane, offset);
					}catch (Exception e3) {}
				}
			}
			
			
			try
			{
				end = Utilities.getRowEnd(pane, offset);	
			}catch (Exception e) 
			{
				try
				{
					end = Utilities.getNextWord(pane, offset);
				}catch (Exception e1) 
				{
					try
					{
						end = Utilities.getWordEnd(pane, offset);
					}catch (Exception e5) {}
				}
			}
			
			
			length = end - start;
			
			if(start < 0 || length <= 0) return;
			
			s = getText(start , length);
		}
		else
		{
			s = getText(offset, len);
		}
		
		lexer.yyreset(new StringReader(s));
		
		byte i = -1 , eof = -1;
		
		while((i = lexer.yylex()) != eof)
		{
			Style style = lexer.getStyleForType(i);
			StyleConstants.setTabSet(style, StyleConstants.getTabSet(defaultAtts));
			StyleConstants.setFontFamily(style, StyleConstants.getFontFamily(defaultAtts));
			setCharacterAttributes(lexer.getStart() + start , lexer.yylength(),  style , false);
			pane.setParagraphAttributes(defaultAtts, true);
		}
	}
	
	private void initDefaultAtts(int width)
	{
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		StyleConstants.setBackground(def, UIManager.getDefaults().getColor("TextPane.background"));
		StyleConstants.setForeground(def, UIManager.getDefaults().getColor("TextPane.foreground"));
		
		TabStop[] tabStops = new TabStop[80];

		for (int i = 1; i < 81; i++)
		{
			tabStops[i - 1] = new TabStop(pane.getFontMetrics(pane.getFont()).charWidth('W') * i * width);
		}

		TabSet tabSet = new TabSet(tabStops);
		
		StyleConstants.setTabSet(def , tabSet);
		StyleConstants.setFontFamily(def, pane.getFont().getFamily());
		pane.setLogicalStyle(def);
		pane.setParagraphAttributes(def, true);
		defaultAtts = def;
	}
	
	
	public void setTabs(int width)
	{
		initDefaultAtts(width);
	}
	
	
}




















