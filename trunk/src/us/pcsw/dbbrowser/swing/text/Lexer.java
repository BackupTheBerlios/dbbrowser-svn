package us.pcsw.dbbrowser.swing.text;

import javax.swing.text.Style;


public interface Lexer
{

	/** This character denotes the end of file */
	public static final int YYEOF = -1;
	
	/** lexical states */
	public static final int String = 1;
	public static final int YYINITIAL = 0;

	public Style getStyleForType(byte type);
	
	
	/**
	 * Closes the input stream.
	 */
	public abstract void yyclose() throws java.io.IOException;

	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>ZZ_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public abstract void yyreset(java.io.Reader reader);

	/**
	 * Returns the current lexical state.
	 */
	public abstract int yystate();

	/**
	 * Enters a new lexical state
	 *
	 * @param newState the new lexical state
	 */
	public abstract void yybegin(int newState);

	/**
	 * Returns the text matched by the current regular expression.
	 */
	public abstract String yytext();

	/**
	 * Returns the character at position <tt>pos</tt> from the 
	 * matched text. 
	 * 
	 * It is equivalent to yytext().charAt(pos), but faster
	 *
	 * @param pos the position of the character to fetch. 
	 *            A value from 0 to yylength()-1.
	 *
	 * @return the character at position pos
	 */
	public abstract char yycharat(int pos);

	/**
	 * Returns the length of the matched text region.
	 */
	public abstract int yylength();

	/**
	 * Pushes the specified amount of characters back into the input stream.
	 *
	 * They will be read again by then next call of the scanning method
	 *
	 * @param number  the number of characters to be read again.
	 *                This number must not be greater than yylength()!
	 */
	public abstract void yypushback(int number);

	public abstract int getStart();

	/**
	 * Resumes scanning until the next regular expression is matched,
	 * the end of input is encountered or an I/O-Error occurs.
	 *
	 * @return      the next token
	 * @exception   java.io.IOException  if any I/O-Error occurs
	 */
	public abstract byte yylex() throws java.io.IOException;

	
	
}