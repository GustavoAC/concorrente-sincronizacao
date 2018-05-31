package ListaEncadeadaConcorrente;

import java.util.ArrayList;

public class ListTester {
	public static void main(String[] args) {
		ArrayList<Thread> threads = new ArrayList<>();
		ConcLinkedList<Integer> list = new ConcLinkedList<>();
		
		// Inserções
		for (int i = 1; i <= 20; i++) {
			threads.add(list.getInsertT(new Integer(i)));
		}
		
		// Buscas
		ArrayList<ConcLinkedList<Integer>.Iterator> iterators = new ArrayList<>();
		for (int i = 1; i <= 20; i += 2) {
			ConcLinkedList<Integer>.Iterator ret = list.getNullIt();
			threads.add(list.getSearchT(new Integer(i), ret));
			iterators.add(ret);
		}
		
		// Remoções
		for (int i = 11; i <= 20; i += 2) {
			threads.add(list.getRemoveT(new Integer(i)));
		}
		
		// Lançar todas as threads
		for (Thread t : threads) {
			t.start();
		}
		
		// Esperar por todas as threads
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("\nIteradores de pesquisa:");
		
		for (ConcLinkedList<Integer>.Iterator it : iterators) {
			try {
				System.out.println("Valor it de busca: " + it.getValue());
			} catch (Exception e) {
				System.out.println("Valor it de busca: it invalido");
			}
		}		
		
		System.out.println("\nLista atual:");
		ConcLinkedList<Integer>.Iterator it = list.getHead();
		do {
			try {
				System.out.println("Valor it: " + it.getValue());
			} catch (Exception e) {
				System.out.println("Erro de it");
			}
		} while (it.advance());
	}
}
