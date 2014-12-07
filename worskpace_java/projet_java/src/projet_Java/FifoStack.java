package projet_Java;

public class FifoStack<V> {
	
	private AlgoLinkedList<V> topObject = null;
	private int size = 0;
	
//	public V pop()throws StackEmptyException{	
	public V pop(){	
		V topValue = null;
		AlgoLinkedList <V> newtopObject = null;
		
		if(!isEmpty())
		{
			topValue = peek();
			if(topObject.getNext() != null){
				topObject.getNext().setPrevious(null);
			}
			topObject = topObject.getNext();
				
//			topObject.remove();
			size --;
		}
		else{
			topObject = newtopObject;

		} // StackEmptyException
		
		return topValue;

	}
	
//	public V peek() throws StackEmptyException {
	public V peek() {
		V val = null;
		if(!isEmpty())
			val = topObject.getValue();
		else {} // StackEmptyException
		return val;		
	}
	
	public void push(V value)
	{
		if(isEmpty()){
			topObject = new AlgoLinkedList<V>(value);
		}
		else{
			topObject.setNewEndLinkedList(value);
		}		
		size++;			
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		if(size() == 0)
			return true;
		return false;
	}
	
	void clear()
	{
		while(!isEmpty())
			pop();
	}
	
//	public FifoStack (){
//		Objects = new AlgoLinkedList<V>
//	}

}
