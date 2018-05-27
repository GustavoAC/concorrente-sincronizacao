
public abstract class Pessoa extends Thread {
	
	protected int id;
	protected Banheiro banheiro;
	protected int tempoNoBanheiro; // segundos

	public Pessoa(int id, Banheiro banheiro, int tempoNoBanheiro) {
		super();
		this.id = id;
		this.banheiro = banheiro;
		this.tempoNoBanheiro = tempoNoBanheiro;
	}
	
	public int getTempoUsandoBanheiro() {
		return this.tempoNoBanheiro;
	}
	
	@Override
	public abstract void run();
}
