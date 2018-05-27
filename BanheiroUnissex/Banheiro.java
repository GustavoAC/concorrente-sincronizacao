package BanheiroUnissex;

import java.util.LinkedList;
import java.util.Queue;

public class Banheiro {
	
	private Queue<Pessoa> filaEspera;
	private final int maxPessoas;
	private MyObject<Integer> pessoasUsando;
	private MyObject<Boolean> homemUsando;
	
	public Banheiro(int maxPessoas) {
		this.filaEspera = new LinkedList<Pessoa>();
		this.maxPessoas = maxPessoas;
		pessoasUsando = new MyObject<Integer>(0);
		homemUsando = new MyObject<Boolean>(null);
	}

	public void entrar(Pessoa p) {
		boolean needsToWait = false;
		
		synchronized (p) {
			synchronized (pessoasUsando) {
				synchronized (homemUsando) {
					if (estaVazio()) {
							homemUsando.setValue(p instanceof Homem);
					}
	
					synchronized (filaEspera) {
						if (temVaga() && pessoaMesmoSexo(p) && filaEspera.isEmpty()) {
							pessoasUsando.setValue(pessoasUsando.getValue()+1);
						} else {
							filaEspera.add(p);
							needsToWait = true;
						}
					}
				}
			}
			
			if (needsToWait) {
				try {
					p.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sair() {
		synchronized (pessoasUsando) {
			pessoasUsando.setValue(pessoasUsando.getValue()-1);
		}
		
		boolean moved = true;
		
		synchronized (filaEspera) {
			synchronized (homemUsando) {
				synchronized (pessoasUsando) {
					while (true && moved) {
						moved = false;
						Pessoa proxFila = filaEspera.peek();
						if (proxFila == null) break;
						synchronized (proxFila) {
							if (pessoaMesmoSexo(proxFila)) {
								if (temVaga()) {
									pessoasUsando.setValue(pessoasUsando.getValue()+1);
									proxFila.notify();
									filaEspera.remove();
									moved = true;
								}
							} else {
								// espera o banheiro esvaziar e toma controle
								if (estaVazio()) {
									homemUsando.setValue(proxFila instanceof Homem);
									pessoasUsando.setValue(pessoasUsando.getValue()+1);
									proxFila.notify();
									filaEspera.remove();
									moved = true;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean temVaga() {
		return pessoasUsando.getValue() < maxPessoas;
	}
	
	private boolean estaVazio() {
		return pessoasUsando.getValue() == 0;
	}
	
	private boolean pessoaMesmoSexo(Pessoa p) {
		return (homemUsando.getValue().booleanValue() && p instanceof Homem) || (!homemUsando.getValue().booleanValue() && p instanceof Mulher);
	}
	
}
