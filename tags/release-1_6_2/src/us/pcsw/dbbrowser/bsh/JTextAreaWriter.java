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
package us.pcsw.dbbrowser.bsh;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JTextArea;

/**
 * A writer used to get output from the beanshell interpreter and write it to
 * a JTextArea.  No other class should manipulate the JTextArea's text member
 * other than this class.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class JTextAreaWriter extends Writer
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance of JTextAreaWriter.
	 * 
	 *  @param textArea The JTextArea to which output will be written.
	 */
	public JTextAreaWriter(JTextArea textArea)
	{
		super();
		buffer = new CharArrayWriter();
		setJTextArea(textArea);
	}
	
	// MEMBERS
	
	private CharArrayWriter buffer;
	
	private Object lock = new Object();
	
	private JTextArea tArea;
	public JTextArea getJTextArea()
	{
		return tArea;
	}
	public void setJTextArea(JTextArea jTextArea)
	{
		synchronized(lock){
			this.tArea = jTextArea;
		}
	}
	
	// METHODS
	
	/**
	 * Clears the text in the control.
	 */
	public void clear()
	{
		synchronized(lock){
			tArea.setText("");
		}
	}

	/**
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException
	{
		flush();
		// Nothing else to do
	}

	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException
	{
		synchronized(lock) {
			buffer.close();
			tArea.append(buffer.toString());
			int pos = tArea.getText().length() - 1;
			tArea.setCaretPosition(pos);
			buffer.reset();
		}
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		synchronized(lock){
			buffer.write(cbuf, off, len);
		}
	}
}
