
public class Main {

	public static void main(String[] args) {
		Banheiro bath = new Banheiro(5);
		Pessoa h1 = new Homem(1, bath, 2);
		Pessoa h2 = new Homem(2, bath, 3);
		Pessoa m = new Mulher(1, bath, 2);
		Pessoa m2 = new Mulher(2, bath, 2);
		
		h1.start();
		m.start();
		m2.start();
		h2.start();
		
		
	}
}
