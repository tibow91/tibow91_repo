package projet_Java;

/** Interface à implémenter pour pouvoir comparer des classes entre elles **/
public interface IComparable<T> {
	
	public int compareTo(T o); // Définir dans les classes dérivées l'algorithme 
							   // selon lequel les classes seront comparées

}
