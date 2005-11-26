package us.pcsw.swing;

/**
 * us.pcsw.swing.SearchAndReplaceWorker
 * -
 * Does the search and replace in a seperate thread.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Jan 8, 2003 This class was created.</LI>
 * </UL></P>
 */
class SearchAndReplaceWorker extends SwingWorker
{
	private int currentPosition = 0;
	private String replaceString = null;
	private SearchAndReplaceDialog sard = null;
	private StringBuffer sb = null;
	private String searchString = null;
	
	/**
	 * Constructor for SearchAndReplaceWorker.
	 */
	public SearchAndReplaceWorker
		(SearchAndReplaceDialog sard, String targetString, String searchString,
		 String replaceString, int startingPoint)
	{
		super();
		this.currentPosition = startingPoint;
		this.replaceString = replaceString;
		this.sard = sard;
		this.sb = new StringBuffer(targetString);
		this.searchString = searchString;
	}

	/**
	 * The code which is run within the thread.  Searches through the
	 * targetString starting at the position indicated by currentPosition.
	 * All matches to searchString are replaced with replaceString.
	 * @see us.pcsw.swing.SwingWorker#construct()
	 */
	public Object construct()
	{
		int endPosition = 0;
		int startPosition = 0;
		
    	if (searchString.length() == 0) {
    		// Nothing to search for.
    		return sb.toString();
    	}
		while (true)
		{
			startPosition = sb.toString().indexOf(searchString, currentPosition);
			if (startPosition > -1) {
			    endPosition = startPosition + searchString.length();
				sb.replace(startPosition, endPosition, replaceString);
				currentPosition = startPosition + replaceString.length();
			} else {
				break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * @see us.pcsw.swing.SwingWorker#finished()
	 */
	public void finished() {
		super.finished();
		sard.replaceAllFinished(get().toString());
	}
}
