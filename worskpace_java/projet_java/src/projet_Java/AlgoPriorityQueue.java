package projet_Java;

import java.util.ArrayList;

public class AlgoPriorityQueue<T extends IComparable<T>> {
	private AlgoLinkedList<T> l = null;
	private AlgoSortChain<T> sort = new AlgoSortChain<T>();
	
	public AlgoPriorityQueue(AlgoComparator<T> comp) {
		sort.addComparator(comp);
	}
	
	public AlgoSortChain<T> getSort() {
		return sort;
	}

	public void setSort(AlgoSortChain<T> sort) {
		this.sort = sort;
	}

	public void add(T o) {
		
		AlgoLinkedList<T> next = new AlgoLinkedList<T>();
		next.setValue(o);
		
		if(this.l == null){
			this.l = next;
		}
		else{
			
			ArrayList<T> ObjectList = new ArrayList<T>();
			
			AlgoLinkedList<T> temp = this.l;
			
			while(temp != null){
				ObjectList.add(temp.getValue());
				temp = temp.getNext();
			}
			ObjectList.add(next.getValue());
			AlgoSort.Sort(ObjectList,sort);
			
			this.l = new AlgoLinkedList<T>();
			this.l.setValue(ObjectList.get(0));
			
			AlgoLinkedList<T> v1;
			AlgoLinkedList<T> v2;
			
			v2 = this.l;
			
			for(int i=1;i<ObjectList.size();i++){
				
				v1 = new AlgoLinkedList<T>();
				v1.setValue(ObjectList.get(i));
				
				v2.setNext(v1);
				v2 = v1;
			}		
		}	
	}

	public T peek() {
		
		if(l != null){
			return l.getValue();
		}
		else{
			return null;
		}
	}

	public T remove() {
		AlgoLinkedList<T> rem = this.l;
		
		if(l != null){
			this.l = rem.getNext();
			return rem.getValue();
		}
		else{
			return null;
		}
	}
	
	public T remove(T item) {
		if(this.l == null) return null;
		if(sort.compare(l.getValue(),item) == 0){
			if(this.l.getNext() == null)
				this.l = null;
			else
				this.l = this.l.getNext();
			return item;
		}
		else{
			AlgoLinkedList<T> temp = this.l.getNext();
			
			while(temp != null && sort.compare(temp.getValue(),item) != 0){
				temp = temp.getNext();
			}
			if(temp == null)
				return null;
			else {
				temp.remove();
				return item;
			}
		}
	}
}
