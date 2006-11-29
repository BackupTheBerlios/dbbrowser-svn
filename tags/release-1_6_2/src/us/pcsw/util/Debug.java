package us.pcsw.util;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * us.pcsw.util.Debug
 *
 * A bug logging facility.
 *
 * <P>Revision History:
 * <BR>08/07/2000 This class was created.
 * <BR>11/27/2001 This class was improved.  Code was added to control what
 *                levels at which errors are written to logfile and stderr.
 *
 * @author Philip A. Chapman
 */
public final class Debug
{
    /**
     * The debug log for errors.
     */
    public static int ERR = 5;

    /**
     * The debug log for informational messages.
     */
    public static int INFO = 10;
    
    /**
     * The maximum size for a log file.
     */
    public static long MAX_LOG_SIZE = 1024000;

	/**
	 * Indicates that the file has not had data written to it since it was
	 * opened.
	 */
	private static boolean firstWrite = true;

	/**
	 * Indicates the lowest error level for which errors are to be written
	 * to stderr.  Default is 15
	 */
	private static int errSeverityLevel = 5;

	/**
	 * Indicates the lowest error level for which errors are to be written
	 * to the log file.  Default is 10
	 */
	private static int logSeverityLevel = 10;

	/**
	 * Holds a reference to the log file
	 */
	private static File logFile = null;

	/**
	 * The print stream to which debug messages are to be written, or null if
	 * debug messages are not to be written.
	 */
	private static PrintStream debugPrintStream = null;
	
	private static PrintStream errorPrintStream = null;

	private static DecimalFormat numberFormatter = null;

	/**
	 * The date use when doing timing profiles.
	 */
	private static Date timerDate = null;
	
	/**
	 * Used to cache timing profile messages until the profiling completes.
	 */
	private static StringBuffer timerBuffer = null;

	/**
	 * Begins a time profile session.  The current time is taken and a message
	 * buffer is began.  The buffer is not written to the log until
	 * commitTimeProfile() is called.  If another profile is in progress (has
	 * not yet been committed) commitTimeProfile() is first called to commit
	 * before the new one begins.
	 */
	public static void beginTimeProfile()
	{
		if (timerDate != null) {
			commitTimeProfile();
		}
		numberFormatter = new DecimalFormat("#,##0.0000");
		timerBuffer = new StringBuffer("Profile Begin.");
		timerDate = new Date();
	}

	/**
	 * Ends a time profile session.  The current time is taken and a final
	 * message is appended to the buffer.  The profile message buffer is then
	 * written to the log.
	 *
	 */
	public static void commitTimeProfile()
	{
		if (timerDate != null) {
			markTimeProfile("Profile Commit.");
			log(timerBuffer.toString());
			timerDate = null;
			timerBuffer = null;			
		}
	}

	/**
	 * Close the log file.
	 */
	public static void closeLogFile()
	{
		if (debugPrintStream != null && !debugPrintStream.equals(System.err)) {
			debugPrintStream.close();
		}
		debugPrintStream = null;
		logFile = null;
	}

	/**
	 * Errors with an error level equal to or greater than the returned level
	 * will be written to stderr.
	 * @return The minimum error level for which errors should be written to
	 *         stderr.
	 */
	public static int getErrSeverityLevel(int level)
	{
		return errSeverityLevel;
	}

	/**
	 * Errors with an error level equal to or greater than the returned level
	 * will be written to the log file.
	 * @return The minimum error level for which errors should be written to
	 *         the log file.
	 */
	public static int getLogSeverityLevel(int level)
	{
		return logSeverityLevel;
	}
	
	private static void initializeStream(PrintStream ps)
	{
		ps.println("\n" + (new Date()).toString());
		ps.print("CLASSPATH: ");
		ps.println(System.getProperty("java.class.path"));
		ps.print('\n');
	}

	/**
	 * Logs a Throwable's stack trace in the debug log file, if one has been
	 * initializes using setLogFile.  Error level 5 is used.
	 * @param throwable The Throwable for which the stack trace is to be
	 *                  logged.
	 */
	public static void log(Throwable throwable)
	{
		log(throwable, ERR);
	}

	/**
	 * Logs a Throwable's stack trace in the debug log file, if one has been
	 * initializes using setLogFile.
	 * @param throwable The Throwable for which the stack trace is to be
	 *                  logged.
	 * @param errorLevel The error level of the exception.
	 */
	public static void log(Throwable throwable, int errorLevel)
	{
		if (errorLevel >= errSeverityLevel) {
			if (errorPrintStream == null) {
				StringBuffer logPath = new StringBuffer(System.getProperty("user.home"));
			    logPath.append(File.separator);
			    logPath.append("pcsw_us_error.log");
			    try {
			    	File logFile = new File(logPath.toString());
			    	if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
			    		logFile.delete();
			    	}
				    errorPrintStream = new PrintStream(new FileOutputStream(logFile));
			    } catch (IOException ioe) {}
			}
			if (errorPrintStream != null) {
				if (firstWrite) {
					initializeStream(errorPrintStream);
					firstWrite = false;
				}
				throwable.printStackTrace(errorPrintStream);
				errorPrintStream.flush();
			}
		}
	}

	/**
	 * Writes the given message to the debug log file if one has been
	 * initialized using setLogFile. Error level 5 is used.
	 * @param msg The message to log (\n is appended to the string).
	 */
	public static void log(String msg)
	{
		log(msg, INFO);
	}

	/**
	 * Writes the given message to the debug log file if one has been
	 * initialized using setLogFile. Error level 10 is used.
	 * @param msg The message to log (\n is appended to the string).
	 * @param errorLevel The error level of the message.
	 */
	public static void log(String msg, int errorLevel)
	{
		if (debugPrintStream != null && errorLevel >= logSeverityLevel) {
			if (firstWrite) {
				initializeStream(debugPrintStream);
				firstWrite = false;
			}
			debugPrintStream.print(msg + '\n');
			debugPrintStream.flush();
		}
	}

	/**
	 * Causes this class to send debug messages to stderr rather than a file.
	 * If a log file has been assigned by setLogFile, it will be closed.
	 */
	public static void logToStdErr()
	{
		if (logFile != null) {
			closeLogFile();
		}
		debugPrintStream = System.err;
		firstWrite = false;
	}

	/**
	 * Records the difference in decimal seconds between this call and the
	 * last time either markTimeProfile or beginTimeProfile was called.  The
	 * provided message appended to the profile message buffer along with the
	 * time difference.
	 * @param message A message to identify the mark in the time profile.
	 */
	public static void markTimeProfile(String message)
	{
		Date now = new Date();
		if (timerDate != null) {
			float timediff = ((now.getTime() - timerDate.getTime()) / 1000);
			timerBuffer.append('\n');
			timerBuffer.append(numberFormatter.format(timediff));
			timerBuffer.append('\t');
			timerBuffer.append(message);
			timerDate = now;	
		}
	}

	/**
	 * Indicates whether debugging is enabled.
	 */
	public static boolean on()
	{
		return (debugPrintStream != null);
	}

	/**
	 * Set the debug log file.  If this class is currently set to write to
	 * another file, it will be closed and the new file will be opened.
	 * @exception IOException Indicates an error initializing the file for
	 *                        output.
	 * @param logFile The file to which debug messages should be logged.
	 */
	public static void setLogFile(File file)
		throws IOException
	{
		if (logFile != null) {
			closeLogFile();
		}
		logFile = file;
		debugPrintStream =
			new PrintStream(new FileOutputStream(file.getPath(), true));
		if (errorPrintStream != null) {
			errorPrintStream.close();
		}
		errorPrintStream = debugPrintStream;
		firstWrite = true;
	}

	public static void setLogFile(File file, long maxSize)
		throws IOException
	{
		// If the logfile is over the specified length, delete it first.
		if (file.exists() && file.length() > maxSize) {
			file.delete();
		}
		setLogFile(file);
	}

	/**
	 * Errors with an error level equal to or greater than the provided level
	 * will be written to stderr.
	 * @param level The minimum error level for which errors should be written
	 *              to stderr.  Default is 15.
	 */
	public static void setErrSeverityLevel(int level)
	{
		errSeverityLevel = level;
	}

	/**
	 * Errors with an error level equal to or greater than the provided level
	 * will be written to the logfile.
	 * @param level The minimum error level for which errors should be written
	 *              to the logfile.  Default is 10.
	 */
	public static void setLogSeverityLevel(int level)
	{
		logSeverityLevel = level;
	}
}
