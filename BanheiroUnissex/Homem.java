
public class Homem extends Pessoa {
	
	public Homem(int id, Banheiro banheiro, int tempoNoBanheiro) {
		super(id, banheiro, tempoNoBanheiro);
		
	}

	@Override
	public void run() {
		banheiro.entrar(this);
		System.out.println("HOMEM #"+ this.id +" usando o banheiro por " + this.tempoNoBanheiro + "segundos");
		try {
			Thread.sleep(tempoNoBanheiro * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(">>> HOMEM #"+ this.id +" saindo do banheiro");
		banheiro.sair(this);
	}

}
