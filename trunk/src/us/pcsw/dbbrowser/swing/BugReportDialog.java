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
package us.pcsw.dbbrowser.swing;

import com.sun.image.codec.jpeg.JPEGCodec;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import us.pcsw.net.MIMEEncoder;

/**
 * us.pcsw.dbbrowser.BugReportDialog
 * -
 * Dialog to allow the user to post bug reports and feature requests back to
 * the pcsw.us web server.
 * 
 * @author phos
 *
 * <P><B>Revision History:</B><UL>
 * <LI>03/26/2003 Class Created</LI>
 * <LI>03/27/2003 Added initial implementation of HttpURLConnection.</LI>
 * <LI>03/28/2003 Added capability to capture and upload screenshot.</LI>
 * <LI>03/28/2003 Perfected MIME formatting and cleaned up debug code.</LI>
 * <LI>04/01/2003 Implemented cleaner MIMEEncoder object.</LI>
 * </UL></P>
 */
public final class BugReportDialog 
	extends JDialog
	implements ActionListener,
			   KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private static Object[] items = {"Bug Report", "Feature Request"};
	private JTextField nameText = new JTextField();
	private JTextField emailText = new JTextField();
	private JTextArea reportText = new JTextArea();
	private JComboBox reportType = new JComboBox(items);
	private JCheckBox screenCap = new JCheckBox("Send Screenshot");
	private JButton submitButton = new JButton("Submit Report");
	private JButton cancelButton = new JButton("Cancel");
	private Container contentPane = null;
	private JPanel buttonPanel = new JPanel();
	private boolean hasBeenEdited = false;
	
	
	public BugReportDialog(Frame parent) 
	{
		
		super(parent, "Bug Report/Feature Request", true);
		
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		contentPane = getContentPane();
		contentPane.setLayout(gb);
		
		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		JLabel label = new JLabel("Report Type:");
		gbc = new GridBagConstraints
			(0, 0,
			 1, 1,
			 1, 0,
			 GridBagConstraints.WEST,
			 GridBagConstraints.HORIZONTAL,
			 new Insets(12, 12, 24, 12),
			 0, 0);
		contentPane.add(label, gbc);
		
		reportType.addActionListener(this);
		gbc = new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE,        // gridx, gridy 
				3, 1,        // gridWidth, gridHeight
				1, 0,        // weightx, weghty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.HORIZONTAL, // fill
				new Insets(12, 12, 24, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(reportType, gbc);
		
		label = new JLabel("Reported By (Optional):");
		gbc = new GridBagConstraints
			(0, 1,
			 1, 1,
			 1, 0,
			 GridBagConstraints.WEST,
			 GridBagConstraints.HORIZONTAL,
			 new Insets(12, 12, 24, 12),
			 0, 0);
		contentPane.add(label, gbc);
		nameText.addKeyListener(this);
		gbc = new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE,        // gridx, gridy 
				3, 1,        // gridWidth, gridHeight
				1, 0,        // weightx, weghty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.HORIZONTAL, // fill
				new Insets(0, 12, 6, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(nameText, gbc);
		
		label = new JLabel("Email Address (Optional):");
		gbc = new GridBagConstraints
			(0, 2,
			 1, 1,
			 1, 0,
			 GridBagConstraints.WEST,
			 GridBagConstraints.HORIZONTAL,
			 new Insets(12, 12, 24, 12),
			 0, 0);
		contentPane.add(label, gbc);
		
		emailText.addKeyListener(this);
		gbc = new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE,        // gridx, gridy 
				3, 1,        // gridWidth, gridHeight
				1, 0,        // weightx, weghty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.HORIZONTAL, // fill
				new Insets(0, 12, 24, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(emailText, gbc);
		
		label = new JLabel("Report:");
		gbc = new GridBagConstraints
			(0, 3,
			 1, 1,
			 1, 0,
			 GridBagConstraints.WEST,
			 GridBagConstraints.HORIZONTAL,
			 new Insets(12, 12, 24, 12),
			 0, 0);
		contentPane.add(label, gbc);

		JScrollPane sp = new JScrollPane(reportText);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(300,200));
		
		gbc = new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE,        // gridx, gridy 
				3, 3,        // gridWidth, gridHeight
				1, 1,        // weightx, weghty
				GridBagConstraints.NORTH, // anchor
				GridBagConstraints.BOTH, // fill
				new Insets(0, 12, 6, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(sp, gbc);
		
		gbc = new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE,        // gridx, gridy 
				3, 1,        // gridWidth, gridHeight
				1, 1,        // weightx, weghty
				GridBagConstraints.NORTH, // anchor
				GridBagConstraints.BOTH, // fill
				new Insets(0, 12, 12, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(screenCap, gbc);
		
		buttonPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints(0, 0,        // gridx, gridy 
				1, 1,        // gridWidth, gridHeight
				1, 1,        // weightx, weghty
				GridBagConstraints.EAST, // anchor
				GridBagConstraints.HORIZONTAL, // fill
				new Insets(0, 0, 0, 0),      // insets
				0, 0);       // ipadx, ipady
		buttonPanel.add(cancelButton, gbc);
		
		gbc = new GridBagConstraints(1, 0,        // gridx, gridy 
				1, 1,        // gridWidth, gridHeight
				1, 1,        // weightx, weghty
				GridBagConstraints.EAST, // anchor
				GridBagConstraints.HORIZONTAL, // fill
				new Insets(0, 6, 0, 0),      // insets
				0, 0);       // ipadx, ipady
		buttonPanel.add(submitButton, gbc);
		
		submitButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(submitButton);
		
		gbc = new GridBagConstraints(1, GridBagConstraints.RELATIVE,        // gridx, gridy 
				2, 1,        // gridWidth, gridHeight
				0, 0,        // weightx, weghty
				GridBagConstraints.EAST, // anchor
				GridBagConstraints.NONE, // fill
				new Insets(0, 12, 12, 12),      // insets
				0, 0);       // ipadx, ipady	
		contentPane.add(buttonPanel, gbc);

		pack();
	}
	
	protected boolean hasBeenEdited()
	{
		return hasBeenEdited;
	}
	
	protected void hasBeenEdited(boolean value)
	{
		hasBeenEdited = value;
	}
	
	private void clearReport() 
	{
		nameText.setText("");
		reportText.setText("");
	}
	
	protected String formatFormFields(String name, String value, String boundary) 
	{
		String result = "";
		try {
			result += "--" + boundary + "\r\n";
			result += "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n";
			result += URLEncoder.encode(value, "US-ASCII") + "\r\n";
		} catch (Throwable t) {
			// do something...
		}
		return result;
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource().equals(submitButton)) {
			if (nameText.getText().length() > 0 && reportText.getText().length() > 0) {
				setVisible(false);	// Hide the bug window.
				try {
					URL server = new URL("http://pcsw.us/dbbrowser/bugreport/upload.php");
					HttpURLConnection repCon = (HttpURLConnection)server.openConnection();
					MIMEEncoder mimeEnc = new MIMEEncoder();
					
					mimeEnc.appendPart("nameText", nameText.getText(), "form-data");
					mimeEnc.appendPart("reportType", (String)reportType.getSelectedItem(), "form-data");
					mimeEnc.appendPart("emailText", emailText.getText(), "form-data");
					mimeEnc.appendPart("reportText", reportText.getText(), "form-data");
					mimeEnc.appendPart("imageType", "0", "form-data");
					
					repCon.setDoOutput(true);
					repCon.setRequestMethod("POST");
					repCon.setRequestProperty("Connection", "Keep-Alive");
					repCon.setRequestProperty("Accept-Language", "en");
					repCon.setRequestProperty("Accept-Charset", "iso-8859-1,*,utf-8");
					repCon.setRequestProperty("Accept", "*/*");
					repCon.setRequestProperty("Host", server.getHost());
					repCon.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + mimeEnc.getBoundaryString());

					if (screenCap.isSelected()) {
						Robot robot = new Robot();
						java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
						java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
						java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
						
						BufferedImage bufImg = robot.createScreenCapture(gc.getBounds());
						ByteArrayOutputStream bo = new ByteArrayOutputStream();
						
						JPEGCodec.createJPEGEncoder(bo).encode(bufImg);
						mimeEnc.appendPart("imageSize", String.valueOf(bo.size()), "form-data");
						
						mimeEnc.appendPart("screenshot", "dbbrowser_screenshot.jpg", bo, "binary", "image/jpeg", "form-data");
					} else {
						mimeEnc.appendPart("imageSize", "0", "form-data");						
					}
					
					repCon.setRequestProperty("Content-Length", String.valueOf(mimeEnc.getCompleteMIMEBody().length()));

					//	Send the entity body.
					OutputStream outStream = repCon.getOutputStream();
					outStream.write(mimeEnc.getBytes());
					outStream.close();

					if (repCon.getResponseCode() != 200) {
						JOptionPane.showMessageDialog(null, 
									"Error Submitting Bug Report!\nServer returned: " + 
									repCon.getResponseMessage(),
									"Error",
									JOptionPane.ERROR_MESSAGE);
					}
					
					InputStream inStream = repCon.getInputStream();
					byte[] buf = new byte[1024];
					int r = 0;
					if ((r = inStream.read(buf, 0, buf.length)) != 0) {
						JOptionPane.showMessageDialog(null, String.valueOf(r));
					}
					
					repCon.disconnect();
					// We can dispose of the window since there *shouldn't* 
					// be many bugs to report. :-)
					dispose();
					
				} catch (Throwable t) {
					JOptionPane.showMessageDialog(null, t.getClass().toString());
				}
			}
		} else if (e.getSource().equals(cancelButton)) {
			clearReport();
			dispose();
		} else if (e.getSource().equals(reportType)) {
			if ((String)reportType.getSelectedItem() != "Bug Report") {
				screenCap.setEnabled(false);
			} else {
				screenCap.setEnabled(true);
			}
		}
	}
	
	public void keyTyped(KeyEvent k) {}
	public void keyReleased(KeyEvent k){}
	public void keyPressed(KeyEvent k) {}

}
