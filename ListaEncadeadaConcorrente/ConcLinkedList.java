import java.util.concurrent.locks.ReentrantLock;

public class ConcLinkedList<T> {
	private Node<T> headNode;
	private Node<T> tailNode;
	private Node<T> tailSentinelNode;
	
	private ReentrantLock searchLock;
	private ReentrantLock searchPrivLock;
	private ReentrantLock insertLock;
	private ReentrantLock removeLock;
	
	private ReentrantLock activeSearchesLock;
	private int activeSearches = 0;
	
	public class Iterator {
		private Node<T> node;
		
		private Iterator (Node<T> _node) {
			node = _node;
		}
		
		public T getValue() throws Exception {
			if (!isValid())
				throw new Exception("Iterador invalido");
			return node.getValue();
		}
		
		public boolean isValid() {
			return (node != null &&
					node.getNextNode() != null);
		}
		
		public void advance() throws Exception {
			if (!isValid())
				throw new Exception("Iterador invalido");
			node = node.getNextNode();
			
			if (node == tailSentinelNode) node = null;
		}
	}
	
	public ConcLinkedList() {
		tailSentinelNode = new Node<T>();
		headNode = new Node<T>();
		headNode.setNextNode(tailSentinelNode);
		tailNode = headNode;
		searchLock = new ReentrantLock();
		insertLock = new ReentrantLock();
		removeLock = new ReentrantLock();
		searchPrivLock = new ReentrantLock();
		activeSearchesLock = new ReentrantLock();
		
		activeSearches = 0;
	}
	
	private void privSearch(T value, Iterator result) {
		System.out.println("Tentando iniciar busca");
		searchLock.lock();
		activeSearchesLock.lock();
		if (activeSearches == 0) {
			System.out.println("Tentando garantir privilégio as buscas");
			searchPrivLock.lock();
			System.out.println("Garantido privilégio as buscas");
		}
		activeSearches++;
		activeSearchesLock.unlock();
		searchLock.unlock();
		System.out.println("Iniciando busca");

		
		Node<T> curr = headNode.getNextNode();
		while (curr != null && !curr.getValue().equals(value))
			curr = curr.getNextNode();
		
		System.out.println("Tentando reduzir contador de busca");
		activeSearchesLock.lock();
		activeSearches--;
		if (activeSearches == 0)
			searchPrivLock.unlock();
		activeSearchesLock.unlock();
		System.out.println("Reduzido contador de busca");
		
		result = new Iterator(curr);
	}
	
	public Thread getSearchT(T value, Iterator ret) {
		return new Thread() {
			public void run(){
				privSearch(value, ret);
			}
		};
	}
	
	private void privSearchNext(T value, Iterator ret) {
		System.out.println("Tentando iniciar busca");
		searchLock.lock();
		activeSearchesLock.lock();
		if (activeSearches == 0) {
			System.out.println("Tentando garantir privilégio as buscas");
			searchPrivLock.lock();
			System.out.println("Garantido privilégio as buscas");
		}
		activeSearches++;
		activeSearchesLock.unlock();
		searchLock.unlock();
		System.out.println("Iniciando busca");
		
		Node<T> curr = headNode;
		while (curr.getNextNode() != null && !curr.getNextNode().getValue().equals(value))
			curr = curr.getNextNode();
		
		ret = null;
		
		System.out.println("Tentando reduzir contador de busca");
		activeSearchesLock.lock();
		activeSearches--;
		if (activeSearches == 0)
			searchPrivLock.unlock();
		activeSearchesLock.unlock();
		System.out.println("Reduzido contador de busca");
		
		ret = new Iterator(curr);
	}
	
	public Thread searchNext(T value, Iterator ret) {
		return new Thread() {
			public void run() {
				privSearchNext(value, ret);
			}
		};
	}
	
	private void privInsert(T value) {
		System.out.println("Tentando inserir");
		insertLock.lock();
		
		System.out.println("Inserindo");
		Node<T> newNode = new Node<T>(value);
		tailNode.setNextNode(newNode);
		tailNode = newNode;
		
		System.out.println("Liberando insert lock");
		insertLock.unlock();
	}
	
	public Thread insert(T value) {
		return new Thread() {
			public void run() {
				privInsert(value);
			}
		};
	}
	
	private void privRemoveNext(Iterator t, Boolean b) {
		
		System.out.println("Adquirindo removelock");
		removeLock.lock();
		
		if (t == null || !t.isValid() || t.node.getNextNode() == tailSentinelNode) {
			b = new Boolean(false);
			System.out.println("Falha na remoção, liberando removelock");
			removeLock.unlock();
		}
		
		Node<T> beforeTarget = t.node;
		
		System.out.println("Bloqueando inicio de buscas");
		searchLock.lock();
		System.out.println("Esperando buscas acabarem");
		searchPrivLock.lock();
		System.out.println("Bloqueando inicio de inserções");
		insertLock.lock();
		
		beforeTarget.setNextNode(beforeTarget.getNextNode().getNextNode());
		beforeTarget.getNextNode().setNextNode(null);
		
		System.out.println("Liberando locks");
		insertLock.unlock();
		searchPrivLock.unlock();
		searchLock.unlock();
		
		removeLock.lock();
		b = new Boolean(true);
	}
	
	public Thread remove(T value, Boolean ret) {
		return new Thread() {
			public void run() {
				Iterator beforeTarget = null;
				privSearchNext(value, beforeTarget);
				removeNext(beforeTarget, ret);
			}
		};
	}
	
	public Thread removeNext(Iterator it, Boolean ret) {
		return new Thread() {
			public void run() {
				privRemoveNext(it, ret);
			}
		};
	}
}
