package projet_Java;

public class AlgoLinkedList<V> {
	
	private V value = null;
	private AlgoLinkedList<V> next = null;
	private AlgoLinkedList<V> previous = null;
	
	public V getValue()	{
		return value;
	}
	
	public void setValue(V value){
		this.value = value;
	}
	
	public AlgoLinkedList<V> getNext(){
		return next;
	}
	
	public void setNext(AlgoLinkedList<V> next){
		this.next = next;
		if(getNext() != null && getNext().getPrevious() != this)
			getNext().setPrevious(this);
	}
	
	public AlgoLinkedList<V> getPrevious() {
		return previous;
	}

	public void setPrevious(AlgoLinkedList<V> previous) {
		this.previous = previous;
		if(getPrevious() != null && getPrevious().getNext() != this)
			getPrevious().setNext(this);
	}
	
	public void remove(){
		
		if(getPrevious() != null)
			getPrevious().setNext(getNext());
		
		if(getNext() != null)
			getNext().setPrevious(getPrevious());	
		setNext(null);
		setPrevious(null);
	}
	
	public void setNewEndLinkedList(V val){
		
		if(getNext() == null)
			setNext(new AlgoLinkedList<V>(val));
		
		else
			getNext().setNewEndLinkedList(val);
	}
	
	public AlgoLinkedList (){
//		setValue(val);
	}
	public AlgoLinkedList (V val){
		setValue(val);
	}
	
}
