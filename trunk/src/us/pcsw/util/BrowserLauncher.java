package us.pcsw.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple, static class to display a URL in the system browser.
 * 
 * Under Unix, the system browser is hard-coded to be 'netscape'. Netscape must
 * be in your PATH for this to work. This has been tested with the following
 * platforms: AIX, HP-UX and Solaris.
 * 
 * Under Windows, this will bring up the default browser under windows, usually
 * either Thunderbird, Mozilla or Microsoft IE. The default browser is
 * determined by the OS. This has been tested under Windows 95/98/NT.
 * 
 * Examples: BrowserLauncher.displayURL("http://www.javaworld.com")
 * BrowserLauncher.displayURL("file://c:\\docs\\index.html")
 * BrowserContorl.displayURL("file:///user/joe/index.html");
 * 
 * Note - you must include the url type -- either "http://" or "file://".
 */

public class BrowserLauncher
{
	// CONSTANTS
	private static final Logger logger = LoggerFactory.getLogger(BrowserLauncher.class);
	
	// Used to identify the windows platform.
	private static final String WIN_ID = "Windows";

	// The default system browser under windows.
	private static final String WIN_PATH = "rundll32";

	// The flag to display a url.
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

	// The default browser under unix- netscape.
	private static final String UNIX_PATH = "netscape";

	// The default browser under unix- mozilla.
	private static final String UNIX_PATH_NEXT = "mozilla";

	private static final String MAC_PATH = "open";

	// The flag to display a url.
	private static final String UNIX_FLAG = "-remote openURL";

	// CONSTRUCTORS
	
	/**
	 * Instantiates a new BrowserLauncher.
	 */
	public BrowserLauncher()
	{
		super();
	}
	
	/**
	 * Instantiates a new BrowserLauncher that will use the given alternative
	 * browser path to locate and launch the browser.
	 */
	public BrowserLauncher(String alternativeBrowserPath)
	{
		super();
		setAlternativeBrowserPath(alternativeBrowserPath);
	}
	
	// MEMBERS
	
	private String alternativeBrowser = null;
	public String getAlternativeBrowserPath()
	{
		return alternativeBrowser;
	}
	public void setAlternativeBrowserPath(String path)
	{
		alternativeBrowser = path;
	}

	// METHODS
	
	/**
	 * Display a file in the system browser. If you want to display a file, you
	 * must include the absolute path name.
	 * 
	 * @param url
	 *            the file's url (the url must start with either "http://" or
	 *            "file://").
	 */
	public void displayURL(String url) {
		boolean windows = isWindowsPlatform();
		String cmd = null;
		try {
			if (alternativeBrowser != null && alternativeBrowser.length() > 0) {
				cmd = alternativeBrowser + " " + url;
				try {
					Runtime.getRuntime().exec(cmd);
					return;
				} catch (Throwable t) {
					logger.error("Error opening browser with command '{}'", cmd, t);
				}
			}
			if (windows) {
				// cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
				cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
				Runtime.getRuntime().exec(cmd);
			} else if (System.getProperty("os.name").equals("Linux")) {
				// Under Unix, Mozilla or Netscape has to be running for the
				// "-remote" command to work. So, we try sending the command
				// and check for an exit value. If the exit command is 0, it
				// worked, otherwise we need to start the browser.
				// cmd = 'netscape -remote openURL(http://www.javaworld.com)'
				try {
					logger.info("Trying with mozilla");
					cmd = UNIX_PATH_NEXT + " " + UNIX_FLAG + "(" + url + ")";
					Process p = Runtime.getRuntime().exec(cmd);
					try {
						// wait for exit code -- if it's 0, command worked,
						// otherwise we need to start the browser up.
						int exitCode = p.waitFor();
						if (exitCode != 0) {
							// Command failed, start up the browser
							// cmd = 'mozilla http://www.javaworld.com'
							cmd = UNIX_PATH_NEXT + " " + url;
							p = Runtime.getRuntime().exec(cmd);
						}
					} catch (InterruptedException x2) {
						logger.error("BrowserLauncher.displayURL(String) : Error bringing up browser, cmd='{}'", cmd, x2);
					}
				} catch (IOException x1) {
					logger.info("Trying netscape");
					cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
					Process p = Runtime.getRuntime().exec(cmd);
					try {
						// wait for exit code -- if it's 0, command worked,
						// otherwise we need to start the browser up.
						int exitCode = p.waitFor();
						if (exitCode == 0) {
							// Command failed, start up the browser
							// cmd = 'netscape http://www.javaworld.com'
							cmd = UNIX_PATH + " " + url;
							p = Runtime.getRuntime().exec(cmd);
						}
					} catch (InterruptedException x) {
						logger.error("BrowserLauncher.displayURL(String) : Error bringing up browser, cmd='{}'", cmd, x);
					}
				}
			} else if (System.getProperty("os.name").equals("Mac OS X"))//FOR
																		// MACINTOSH
			{
				try {
					logger.info("BrowserLauncher.displayURL(String) : Invoking Browser for MAC OS. Inside BrowserLauncher.java");
					String macurl = getMacURL(url);
					cmd = MAC_PATH + " " + macurl;
					logger.info("BrowserLauncher.displayURL(String) : cmd to be exceuted: '{}'", cmd);
					Runtime.getRuntime().exec(cmd);
				} catch (IOException x1) {
					logger.info("BrowserLauncher.displayURL(String) : Trying with mozilla");
					cmd = UNIX_PATH_NEXT + " " + UNIX_FLAG + "(" + url + ")";
					Process p = Runtime.getRuntime().exec(cmd);
					try {
						// wait for exit code -- if it's 0, command worked,
						// otherwise we need to start the browser up.
						int exitCode = p.waitFor();
						if (exitCode != 0) {
							// Command failed, start up the browser
							// cmd = 'mozilla http://www.javaworld.com'
							cmd = UNIX_PATH_NEXT + " " + url;
							p = Runtime.getRuntime().exec(cmd);
						}
					} catch (InterruptedException x2) {
						logger.error("BrowserLauncher.displayURL(String) : Error bringing up browser, cmd='{}'", cmd, x2);
					}
				}
			}
		} catch (IOException x) {
			// couldn't exec browser
			logger.error("BrowserLauncher.displayURL(String) : Could not invoke browser, command='{}'", cmd,x);
		}
	}

	/**
	 * Try to determine whether this application is running under Windows or
	 * some other platform by examing the "os.name" property.
	 * 
	 * @return true if this application is running under a Windows OS
	 */
	private static boolean isWindowsPlatform() {
		String os = System.getProperty("os.name");
		if (os != null && os.startsWith(WIN_ID))
			return true;
		else
			return false;

	}

	private static String getMacURL(String url)
	{
		String space = " ";
		int ii = url.indexOf(space);
		String finalurl = "";
		while (ii != -1) {
			int noteindex1 = 0, noteindex2 = 0;
			int index = url.indexOf(space);
			char uchar[] = url.toCharArray();
			for (int i = index; i >= 0; i--) {
				char ch = uchar[i];
				if (ch == '/') {
					noteindex1 = i;
					break;
				}
			}
			for (int i = index; i <= url.length(); i++) {
				char ch = uchar[i];
				if (ch == '/') {
					noteindex2 = i;
					break;
				}
			}
			String newurl = url.substring(0, noteindex1 + 1);
			newurl = newurl + "\"";
			newurl = newurl + url.substring(noteindex1 + 1, noteindex2);
			newurl = newurl + "\"";
			url = url.substring(noteindex2, url.length());
			ii = url.indexOf(space);
			finalurl = finalurl + newurl;
		}
		finalurl = finalurl + url;
		return finalurl;
	}

	/**
	 * Simple example.
	 */
	public static void main(String[] args) {
		System.out.println(args[0]);
		BrowserLauncher launcher = new BrowserLauncher();
		if (args.length == 0) {
			launcher.displayURL("http://www.google.com");
		} else {
			launcher.displayURL(args[0]);
		}
	}
}