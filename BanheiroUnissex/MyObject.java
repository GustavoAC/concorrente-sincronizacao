package BanheiroUnissex;

public class MyObject<T> {
	private T value;

	public MyObject(T value) {
		super();
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	
}
