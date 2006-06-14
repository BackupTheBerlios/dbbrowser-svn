/*
 * Created on Apr 1, 2003
 *
 */
package us.pcsw.net;

import java.net.URLEncoder;
import java.io.OutputStream;

/**
 * @author phos
 *
 * <P><B>Revision History:</B><UL>
 * <LI>04/01/2003 Class Created</LI>
 * <LI>04/02/2003 Added name argument to file appendPart method</LI>
 * <LI>04/03/2003 Made changes so that method comments would be processed by
 *                javadoc.  PAC </LI>
 * </UL></P>
 */
public class MIMEEncoder extends Object {

	private String bString = "";
	private String bodyString = "";
	
	/**
	 * Generic constructor.
	 */
	public MIMEEncoder()
	{
		bString = "-------------" + String.valueOf(System.currentTimeMillis()) + "foobar";
	}

	/**
	 * Append a basic MIME part to the entity body
	 * @param name Name of MIME part
	 * @param value Value of MIME part
	 */
	public void appendPart(String name, String value, String contentDisposition)
	{
		try {
			bodyString += "--" + bString + "\r\n";
			bodyString += "Content-Disposition: " + contentDisposition + "; name=\"" + name + "\"\r\n\r\n";
			bodyString += URLEncoder.encode(value, "US-ASCII") + "\r\n";
		} catch (Throwable t) {
			// do something...
		}
	}
	
	/**
	 * Append file data MIME part to the entity body
	 * @param fileName File name of file to add
	 * @param bo OutPutStream to get data from
	 * @param contentTransferEncoding Content transfer encoding of the MIME part
	 * @param contentType Content type of the MIME part
	 * @param contentDisposition Content disposition of the MIME part
	 */
	public void appendPart(String name, 
						   String fileName, 
						   OutputStream bo, 
						   String contentTransferEncoding, 
						   String contentType,
						   String contentDisposition)
	{
		
		bodyString += "--" + bString + "\r\n";
		bodyString += "Content-Disposition: " + contentDisposition + "; name=\"" + name  + "\"; " +
					  "filename=\"" + fileName + "\"\r\n";
		bodyString += "Content-Type: " + contentType + "\r\n";
		bodyString += "Content-Transfer-Encoding: " + contentTransferEncoding + "\r\n";
		bodyString += "\r\n";
		bodyString += bo.toString() + "\r\n\r\n\r\n";
	}
	
	/**
	 * Retrieves completed MIME entity body. Call this after appending all MIME parts.
	 */
	public String getCompleteMIMEBody()
	{
		return bodyString + "--" + bString + "--";
	}
	
	/**
	 * Returns current MIME entity body.
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return bodyString;
	}
	
	/**
	 * Returns the entity body as a byte array.
	 */
	public byte[] getBytes()
	{
		return this.getCompleteMIMEBody().getBytes();
	}
	
	/**
	 * Returns the boundnary string being used in this MIME entity body
	 */
	public String getBoundaryString()
	{
		return bString;
	}

}
