package ListaEncadeadaConcorrente;

public class Node<T> {
	private T value;
	private Node<T> nextNode;
	
	public Node(T value) {
		this.value = value;
		nextNode = null;
	}
	
	public Node() {
		value = null;
		nextNode = null;
	}

	public T getValue() {
		return value;
	}

	public Node<T> getNextNode() {
		return nextNode;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setNextNode(Node<T> nextNode) {
		this.nextNode = nextNode;
	}
}
