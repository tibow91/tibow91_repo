package projet_Java;

/** Objet comparateur de classes comparables (héritant de IComparable) **/
public class AlgoComparator<T extends IComparable<T>> {
	
	public int compare(T o1, T o2)
	{
		if( o1 == null || o2 == null){
			System.out.println("One or both object to compare is null");
			System.exit(0);
		}
		return o1.compareTo(o2);
			
	}
	
	public boolean equals(Object obj)
	{		
		if(obj == null)
		{
			System.out.println("Object to compare is null");
		}
		else if(this == obj)
			return true;
		return false;
	}
	
}
