package projet_Java;

/** Interface � impl�menter pour pouvoir comparer des classes entre elles **/
public interface IComparable<T> {
	
	public int compareTo(T o); // D�finir dans les classes d�riv�es l'algorithme 
							   // selon lequel les classes seront compar�es

}
