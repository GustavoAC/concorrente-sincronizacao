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
			homemUsando = p instanceof Homem;
		}
		if (temVaga() && pessoaMesmoSexo(p)) {
			pessoasUsando++;
		} else {
			synchronized (p) {
				filaEspera.add(p);
				try {
					p.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sair(Pessoa p) {
		pessoasUsando--;
		Pessoa proxFila = filaEspera.peek();
		while (proxFila != null) {
//				System.out.println("Entrei!!!");
			if(estaVazio()){
				homemUsando = p instanceof Homem;
				synchronized (proxFila) {
					filaEspera.remove().notify();
				}
				break;
			}
			
			if(pessoaMesmoSexo(proxFila)){
				pessoasUsando++;
				synchronized (proxFila) {
					filaEspera.remove().notify();
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
