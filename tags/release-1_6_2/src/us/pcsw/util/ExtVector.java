package us.pcsw.util;

import java.util.Collection;
import java.util.Vector;

/**
 * us.pcsw.util.ExtVector
 * -
 * A description of this class.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Aug 11, 2004 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class ExtVector extends Vector
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ExtVector()
	{
		super();
	}
	
	/**
	 * @param initialCapacity
	 */
	public ExtVector(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	/**
	 * @param initialCapacity
	 * @param capacityIncrement
	 */
	public ExtVector(int initialCapacity, int capacityIncrement)
	{
		super(initialCapacity, capacityIncrement);
	}

	/**
	 * @param arg0
	 */
	public ExtVector(Collection arg0)
	{
		super(arg0);
	}

	/**
	 * Creates a new ExtVector instance that contains the elements in the
	 * array.
	 * @param elements The array to be used to populate the vector.
	 */
	public ExtVector(Object[] elements)
	{
		super();
		super.setSize(elements.length);
		for (int i = 0; i < elements.length; i++) {
			this.setElementAt(elements[i], i);
		}
	}
	
	/**
	 * Adds some of the elements from the array into the ExtVector.
	 * @param elements The array from which elements should be added.
	 * @param startIndex The index of the first element to add.
	 * @param elementCount The number of elements to add.
	 */
	public void add(Object[] elements, int startIndex, int elementCount)
	{
		if (elements != null) {
			for (int i = startIndex; i < elementCount && i < elements.length; i++) {
				add(elements[i]);
			}
		}
	}
	
	/**
	 * Adds all the elements from the array into the ExtVector.
	 * @param elements The array from which elements should be added.
	 */
	public void addAll(Object[] elements)
	{
		if (elements != null) {
			add(elements, 0, elements.length);
		}
	}
}
