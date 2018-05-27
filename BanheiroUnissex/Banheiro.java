import java.util.LinkedList;
import java.util.Queue;

public class Banheiro {
	
	private Queue<Pessoa> filaEspera;
	private final int maxPessoas;
	private int pessoasUsando = 0;
	private boolean homemUsando;
	
	public Banheiro(int maxPessoas) {
		this.filaEspera = new LinkedList<Pessoa>();
		this.maxPessoas = maxPessoas;
	}

	public void entrar(Pessoa p) {
		if (estaVazio()) {
			synchronized (this) {
				homemUsando = p instanceof Homem;
			}
		}
		if (temVaga() && pessoaMesmoSexo(p)) {
			synchronized (this) {
				pessoasUsando++;
			}
		} else {
			Pessoa pessoa = p;
			synchronized (pessoa) {
				synchronized (filaEspera) {
					filaEspera.add(pessoa);
				}
				try {
					pessoa.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sair(Pessoa p) {
		synchronized (this) {
			pessoasUsando--;
		}
		
		Pessoa proxFila = filaEspera.peek();
		if (proxFila != null && estaVazio()) {
			synchronized (this) {
				homemUsando = proxFila instanceof Homem;
			}
		}

		while (proxFila != null) {
			if(pessoaMesmoSexo(proxFila) && temVaga()){
				synchronized (this) {
					pessoasUsando++;
					synchronized (proxFila) {
						filaEspera.remove().notify();
					}
				}
			} else {
				break;
			}
			proxFila = filaEspera.peek();
		}
	}
	
	private boolean temVaga() {
		return pessoasUsando < maxPessoas;
	}
	
	private boolean estaVazio() {
		return pessoasUsando == 0;
	}
	
	private boolean pessoaMesmoSexo(Pessoa p) {
		return (homemUsando && p instanceof Homem) || (!homemUsando && p instanceof Mulher);
	}
	
}
