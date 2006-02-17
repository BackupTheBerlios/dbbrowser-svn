package us.pcsw.dbbrowser.bsh;

public interface DbbBshConstants
{
	/**
	 * The session object used by the DBBrowser's custom commands.  This
	 * object will not be of much use to script writers directly.
	 */
	public static final String VAR_BEAN_SHELL_SESSION = "_dbbBeanShellSession";
	
	/**
	 * A connection provider that the script can use to get a database
	 * connection.
	 */
	public static final String VAR_CONNECTION_PROVIDER = "_dbbConnectionProvider";
}
