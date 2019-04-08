package org.eclipse.vtp.framework.util;

/**
*	A utility class that provides the quick sort algorithm for
*	classes that implement the Comparable interface
*
*	@see Comparable
*	@author Trip Gilman
*/
public class QuickSort
{
	/**
	*	Returns the quick sorted Comparable array.  The compareTo function
	*	of the Comparable interface provides the positional comparisons.
	*
	*	@param comps The array to sort.
	*	@return Comparable[] The original array in sorted order.
	**/
	@SuppressWarnings("rawtypes")
	public static Comparable[] comparableSort(Comparable[] comps)
	{
		return comparableSort(comps, 0, comps.length - 1);
	}

	/**
	*	Quick sorts the section of the given array specified between the src index
	*	and the end index inclusive.  The sorted original array is then returned
	*	for ease-of-use reasons.
	*
	*	@param comps The array to sort.
	*	@param src The first element of the array section to sort.
	*	@param end The last element of the array section to sort.
	*	@return Comparable[] The original array with the specified section sorted.
	**/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Comparable[] comparableSort(Comparable[] comps, int src, int end)
	{
        Comparable tmp_comp = null;
		int es = end - src;

		if(comps == null)
		{
			return null;
		}
		else if(es < 1)
		{
			return comps;
		}
		else if(es == 1)
		{
			if(comps[src].compareTo(comps[end]) > 0)
			{
				tmp_comp = comps[src];
				comps[src] = comps[end];
				comps[end] = tmp_comp;
			}
			return comps;
		}

		int cent = es/2 + src;
		Comparable pivot = comps[cent];
		int det = 0;
		if(comps[src].compareTo(pivot) > 0)
		{
			det+= 4;
		}
		if(pivot.compareTo(comps[end]) > 0)
		{
			det += 2;
		}
		if(comps[end].compareTo(comps[src]) > 0)
		{
			det += 1;
		}
		switch(det)
		{
			case 0:  //a
				pivot = comps[src];
				comps[src] = comps[end];
				break;

			case 1:  //b
				comps[cent] = comps[end];
				break;

			case 2:  //a
				pivot = comps[src];
				comps[src] = comps[end];
				break;

			case 3:  //c
				pivot = comps[end];
				break;

			case 4:  //c
				pivot = comps[end];
				break;

			case 5:  //a
				pivot = comps[src];
				comps[src] = comps[end];
				break;

			case 6:  //b
				comps[cent] = comps[end];
				break;

			case 7:  //a
				pivot = comps[src];
				comps[src] = comps[end];
				break;
		}
		comps[end] = pivot;

		int lp = src;
		int rp = end - 1;
        int resl;
        int resr;
		while(rp > lp)
		{
			resl = comps[lp].compareTo(pivot);
			resr = comps[rp].compareTo(pivot);
			if((resl >= 0) && (resr < 0))
			{
                tmp_comp = comps[rp];
                comps[rp] = comps[lp];
                comps[lp] = tmp_comp;
				rp--;
				lp++;
			}
			else if(resl >= 0)
			{
				rp--;
			}
			else if(resr < 0)
			{
				lp++;
			}
			else if((resl < 0) && (resr >= 0))
			{
				rp--;
				lp++;
			}
		}
		if(rp == lp)
		{
			if(comps[lp].compareTo(pivot) > 0)
			{
				if((lp == src))
				{
					return comps;
				}
				comparableSort(comps, src, lp - 1);
				comparableSort(comps, lp, end);
			}
			else
			{
				comparableSort(comps, src, lp);
				comparableSort(comps, lp + 1, end);
			}
		}
		else
		{
			comparableSort(comps, src, rp);
			comparableSort(comps, lp, end);
		}
		return comps;
	}
}
