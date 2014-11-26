package projet_Java;

public class StackEmptyException extends Exception{
	private static final long serialVersionUID = 1L;

	public StackEmptyException() {
		super("Stack is empty, can't pop or peek");
	}
}
