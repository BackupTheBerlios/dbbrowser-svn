package us.pcsw.swing;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.DefaultFormatter;

/**
 * us.pcsw.swing.RegExpFormatter
 * -
 * Provides regular expression formatting for JFormattedTextFields.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Oct 16, 2002 This class was created.</LI>
 * </UL></P>
 */
public class RegExpFormatter
	extends DefaultFormatter
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

    private Pattern pattern;
    private Matcher matcher;
    
	/**
	 * Constructor for RegExpFormatter.
	 */
	public RegExpFormatter()
	{
		super();
	}

    /**
     * Creates a regular expression based <code>AbstractFormatter</code>.
     * <code>pattern</code> specifies the regular expression that will
     * be used to determine if a value is legal.
     */
    public RegExpFormatter(String pattern)
    	throws PatternSyntaxException
    {
        this();
        setPattern(Pattern.compile(pattern));
    }

    /**
     * Creates a regular expression based <code>AbstractFormatter</code>.
     * <code>pattern</code> specifies the regular expression that will
     * be used to determine if a value is legal.
     */
    public RegExpFormatter(Pattern pattern)
    {
        this();
        setPattern(pattern);
    }

    /**
     * Sets the pattern that will be used to determine if a value is
     * legal.
     */
    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Returns the <code>Pattern</code> used to determine if a value is
     * legal.
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    /**
     * Sets the <code>Matcher</code> used in the most recent test
     * if a value is legal.
     */
    protected void setMatcher(Matcher matcher)
    {
        this.matcher = matcher;
    }

    /**
     * Returns the <code>Matcher</code> from the most test.
     */
    protected Matcher getMatcher()
    {
        return matcher;
    }

    /**
     * Parses <code>text</code> returning an arbitrary Object. Some
     * formatters may return null.
     * <p>
     * If a <code>Pattern</code> has been specified and the text
     * completely matches the regular expression this will invoke
     * <code>setMatcher</code>.
     *
     * @throws ParseException if there is an error in the conversion
     * @param text String to convert
     * @return Object representation of text
     */
    public Object stringToValue(String text)
    	throws ParseException
    {
        Pattern pattern = getPattern();

        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);

            if (matcher.matches()) {
                setMatcher(matcher);
                return super.stringToValue(text);
            }
            throw new ParseException("Pattern did not match", 0);
        }
        return text;
    }
}
