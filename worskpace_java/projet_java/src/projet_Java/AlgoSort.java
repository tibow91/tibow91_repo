package projet_Java;

import java.util.ArrayList;

public class AlgoSort {
	
	public static <T>
	void reverse(ArrayList<T> array) {
		ArrayList<T> tab = new ArrayList<T>(array);
		int taille = array.size();
		array.clear();
		for(int i=0; i< taille; i++)
			array.add(tab.get(taille-1-i));			
	}
	
	public static <T extends IComparable<T>>
	void Sort(ArrayList<T> array,AlgoSortChain<T> comp){
		
		if(array == null){
			System.out.println("Array to sort is null");
			return;
		}
		if(comp == null){
			System.out.println("Comparator to use is null");
			return;
		}
		
		int taille = array.size();
		boolean inOrder = false;

		while(!inOrder){
			inOrder = true;
			for(int i=0; i<(taille-1); i++){
				if(comp.compare(array.get(i),array.get(i+1)) > 0){
					T temp = array.get(i);
					array.remove(i);
					array.add(i+1,temp);
					inOrder = false;
				}
			}
			taille--;
		}
	}
}
