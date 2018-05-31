package BanheiroUnissex;

import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		Banheiro banheiro = new Banheiro(5);
		Random rand = new Random();
		ArrayList<Pessoa> threads = new ArrayList<>();
		
		for(int i = 0; i < 30; i++){
			if (rand.nextInt(2) == 0) {
				threads.add(new Homem(i, banheiro, (rand.nextInt(3) + 1)));
			} else {
				threads.add(new Mulher(i, banheiro, (rand.nextInt(3) + 1)));
			}
		}
		
		System.out.print("[ ");
		for (Pessoa pessoa : threads) {
			if(pessoa instanceof Homem)
				System.out.print("H ");
			else
				System.out.print("M ");
		}
		System.out.print("]\n");
		
		for (Pessoa pessoa : threads) {
			pessoa.start();
		}
		
		for (Pessoa pessoa : threads) {
			try {
				pessoa.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("\n\nTodas as pessoas usaram o banheiro");
	}
}
