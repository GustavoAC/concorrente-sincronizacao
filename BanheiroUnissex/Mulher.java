
public class Mulher extends Pessoa {
	
	public Mulher(int id, Banheiro banheiro, int tempoNoBanheiro) {
		super(id, banheiro, tempoNoBanheiro);
	}
	
	@Override
	public void run() {
		banheiro.entrar(this);
		System.out.println("MULHER #"+ this.id +" usando o banheiro por " + this.tempoNoBanheiro + "segundos");
		try {
			Thread.sleep(tempoNoBanheiro * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("MULHER #"+ this.id +" saindo do banheiro");
		banheiro.sair(this);
	}
}
