package projet_Java;

public class AlgoSortChain<T extends IComparable<T>> {

	public AlgoComparator<T> comp;

	public int compare(T o1, T o2) {
		return this.comp.compare(o1, o2);
	}

	public void addComparator(AlgoComparator<T> arg0) {
		this.comp = arg0;
	}

	public void addComparator(int arg0, AlgoComparator<T> arg1) {
		// TODO Auto-generated method stub
		
	}
}