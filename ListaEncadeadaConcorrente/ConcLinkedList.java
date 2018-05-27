import java.util.concurrent.Semaphore;

public class ConcLinkedList<T> {
	private Node<T> headNode;
	private Node<T> tailNode;
	private Node<T> tailSentinelNode;
	
	private Semaphore searchLock;
	private Semaphore searchPrivLock;
	private Semaphore insertLock;
	private Semaphore removeLock;
	
	private Semaphore activeSearchesLock;
	private int activeSearches = 0;
	private int currentId = 1;

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
		
		public boolean advance() {
			if (!isValid())
				return false;
			
			node = node.getNextNode();
			
			if (node == tailSentinelNode)
				return false;
			return true;
		}
	}
	
	public Iterator getNullIt() {
		return new Iterator(null);
	}
	
	public ConcLinkedList() {
		tailSentinelNode = new Node<T>();
		headNode = new Node<T>();
		headNode.setNextNode(tailSentinelNode);
		tailNode = headNode;
		searchLock = new Semaphore(1);
		insertLock = new Semaphore(1);
		removeLock = new Semaphore(1);
		searchPrivLock = new Semaphore(1);
		activeSearchesLock = new Semaphore(1);
		
		activeSearches = 0;
		currentId = 1;
	}
	
	public Iterator getHead() {
		return new Iterator(headNode.getNextNode());
	}
	
	private void privSearch(T value, Iterator result, int tId) throws InterruptedException {
		System.out.println("S-" + tId + ": Tentando iniciar busca");
		searchLock.acquire();
		activeSearchesLock.acquire();
		if (activeSearches == 0) {
			System.out.println("S-" + tId + ": Tentando garantir privilégio as buscas");
			searchPrivLock.acquire();
			System.out.println("S-" + tId + ": Garantido privilégio as buscas");
		}
		
		System.out.println("S-" + tId + ": Subindo buscas ativas de " + activeSearches + " para " + (activeSearches + 1));
		activeSearches++;
		activeSearchesLock.release();
		searchLock.release();
		System.out.println("S-" + tId + ": Iniciando busca");
		
		Node<T> curr = headNode.getNextNode();
		while (curr != tailSentinelNode && !curr.getValue().equals(value))
			curr = curr.getNextNode();
		
		System.out.println("S-" + tId + ": Tentando reduzir contador de busca");
		activeSearchesLock.acquire();
		System.out.println("S-" + tId + ": Reduzido contador de busca de " + activeSearches + " para " + (activeSearches - 1));
		activeSearches--;
		if (activeSearches == 0) {
			System.out.println("S-" + tId + ": Liberando privilégio de busca");
			searchPrivLock.release();
		}
		activeSearchesLock.release();
		
		result.node = curr;
	}
	
	public Thread getSearchT(T value, Iterator ret) {
		return new Thread() {
			private int id = currentId++;
			
			public void run(){
				try {
					privSearch(value, ret, id);
				} catch (InterruptedException e) {
					System.out.println("Erro na thread S-" + id);
					e.printStackTrace();
				}
			}
		};
	}
	
	private void privSearchNext(T value, Iterator ret, int tId) throws InterruptedException {
		System.out.println("S-" + tId + ": Tentando iniciar busca");
		searchLock.acquire();
		activeSearchesLock.acquire();
		if (activeSearches == 0) {
			System.out.println("S-" + tId + ": Tentando garantir privilégio as buscas");
			searchPrivLock.acquire();
			System.out.println("S-" + tId + ": Garantido privilégio as buscas");
		}
		
		System.out.println("S-" + tId + ": Subindo buscas ativas de " + activeSearches + " para " + (activeSearches + 1));
		activeSearches++;
		activeSearchesLock.release();
		searchLock.release();
		System.out.println("S-" + tId + ": Iniciando busca");
		
		Node<T> curr = headNode;
		while (curr.getNextNode() != tailSentinelNode && !curr.getNextNode().getValue().equals(value))
			curr = curr.getNextNode();
		
		if (curr.getNextNode() == tailSentinelNode)
			curr = null;

		System.out.println("S-" + tId + ": Tentando reduzir contador de busca");
		activeSearchesLock.acquire();
		System.out.println("S-" + tId + ": Reduzido contador de busca de " + activeSearches + " para " + (activeSearches - 1));
		activeSearches--;
		if (activeSearches == 0) {
			System.out.println("S-" + tId + ": Liberando privilégio de busca");
			searchPrivLock.release();
		}
		activeSearchesLock.release();
		
		ret.node = curr;
	}
	
	private void privInsert(T value, int tId) throws InterruptedException {
		System.out.println("I-" + tId + ": Tentando inserir");
		insertLock.acquire();
		
		System.out.println("I-" + tId + ": Inserindo");
		Node<T> newNode = new Node<T>(value);
		newNode.setNextNode(tailSentinelNode);
		tailNode.setNextNode(newNode);
		tailNode = newNode;
		
		System.out.println("I-" + tId + ": Liberando insert lock");
		insertLock.release();
	}
	
	public Thread getInsertT(T value) {
		return new Thread() {
			private int id = currentId++;

			public void run() {
				try {
					privInsert(value, id);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	private void privRemove(T value, int tId) throws InterruptedException {
		
		System.out.println("R-" + tId + ": Adquirindo removelock");
		removeLock.acquire();
		
		Iterator t = getNullIt();
		privSearchNext(value, t, tId);
		
		if (t == null || !t.isValid() || t.node.getNextNode() == tailSentinelNode) {
			System.out.println("R-" + tId + ": Falha na remoção, liberando removelock");
			removeLock.release();
			return;
		}
		
		Node<T> beforeTarget = t.node;
		
		System.out.println("R-" + tId + ": Bloqueando inicio de buscas");
		searchLock.acquire();
		System.out.println("R-" + tId + ": Esperando buscas acabarem");
		searchPrivLock.acquire();
		System.out.println("R-" + tId + ": Bloqueando inicio de inserções");
		insertLock.acquire();
		
		Node<T> afterRemoved = beforeTarget.getNextNode().getNextNode();
		beforeTarget.getNextNode().setNextNode(null);
		beforeTarget.setNextNode(afterRemoved);
		
		System.out.println("R-" + tId + ": Liberando locks");
		insertLock.release();
		searchPrivLock.release();
		searchLock.release();
		
		removeLock.release();
	}
	
	public Thread getRemoveT(T value) {
		return new Thread() {
			private int id = currentId++;

			public void run() {
				try {
					privRemove(value, id);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
//	public Thread getRemoveNextT(Iterator it) {
//		return new Thread() {
//			private int id = currentId++;
//			
//			public void run() {
//				try {
//					privRemoveNext(it, id);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//	}
//	
//	public Thread getSearchNextT(T value, Iterator ret) {
//		return new Thread() {
//			private int id = currentId++;
//
//			public void run() {
//				try {
//					privSearchNext(value, ret, id);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//	}
}
