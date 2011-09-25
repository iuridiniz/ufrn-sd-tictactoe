import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.swing.JButton;

import javax.swing.JFrame;


import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class TicTacToe extends JFrame implements WindowListener, CommListener, ActionListener{
	
	private CommBasic comm;
	private AreaJogo areaJogo;
	
	private JButton botaoCliente;
	private JButton botaoServidor;
	private JButton botaoDesconectar;
	private Container areaMensagem;
	
	private JTextArea mensagens;
	private JTextField entryMensagem;
	
	private int numeroJogador;
	private int jogadorAtual;
	private int tabuleiro[][];

	public static void main(String[] args) throws IOException, InterruptedException {
		TicTacToe window = new TicTacToe();
		window.setVisible(true);
	}

	public void windowOpened(WindowEvent e) {
		// Nothing
		
	}

	public void windowClosing(WindowEvent e) {
		if (comm != null) {
			comm.setEnd();
		}
		this.setVisible(false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			System.err.println("Sleep interrompido");
			e1.printStackTrace();
		}
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
		// Nothing
	}

	public void windowIconified(WindowEvent e) {
		// Nothing
		
	}

	public void windowDeiconified(WindowEvent e) {
		// Nothing
		
	}

	public void windowActivated(WindowEvent e) {
		// Nothing
		//System.out.println(e.getWindow().getWidth());
		//System.out.println(e.getWindow().getHeight());
	}

	public void windowDeactivated(WindowEvent e) {
		// Nothing
		
	}
	
	TicTacToe() {
		/* eu mesmo capturo os eventos da janela */
		this.addWindowListener(this);
		this.setSize(311,457);
		this.setMinimumSize(new Dimension(311,457));
		this.setResizable(false);
		
		/* Criando os botoes */
		this.botaoCliente = new JButton("cliente");
		this.botaoServidor = new JButton("servidor");
		
		/* eu mesmo capturo os eventos dos botoes */
		this.botaoCliente.addActionListener(this);
		this.botaoServidor.addActionListener(this);
		
		this.mostrarTelaInicial();
		//this.mostrarTelaTicTac();
	}

	private void mostrarTelaInicial() {
		this.botaoCliente.setEnabled(true);
		this.botaoServidor.setEnabled(true);
		this.setTitle("TicTacToe");
		this.getContentPane().removeAll();
		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(this.botaoCliente);
		this.getContentPane().add(this.botaoServidor);
		this.repaint();
		this.setSize(this.getWidth()-1, this.getHeight()+1);
		this.setSize(this.getWidth()+1, this.getHeight()-1);
	}

	public void clearAll(CommEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void markPosition(CommEvent e) {
		/* o outro jogar marcou algo */
		this.marcarPosicao(e.pos, this.jogadorAtual);
		this.trocarDeJogador();
		this.alguemGanhou();
	}

	public void newMessage(CommEvent e) {
		this.mensagens.append("outro> " + e.message + "\n");
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(this.botaoCliente)) {
			this.iniciarModoCliente();
			
		} else if (o.equals(this.botaoServidor)) {
			this.iniciarModoServidor();
		} else if (o.equals(this.entryMensagem)) {
			this.mandarMensagem((JTextField) o);
		} else if (o.equals(this.botaoDesconectar)) {
			this.desconectar();
		}
		
	}

	private void desconectar() {
		if (this.comm != null) {
			this.comm.setEnd();
			this.comm = null;
		}
		this.mostrarTelaInicial();
	}

	private void mandarMensagem(JTextField field) {
		this.mensagens.append(Integer.toString(this.numeroJogador) + "> "+ field.getText() + "\n");
		if (this.comm != null) {
			try {
				this.comm.sendEvent(new CommEvent(CommEvent.EVENT_MESSAGE, field.getText(), null));
			} catch (IOException e) {
				System.err.println("Nao consegui enviar a mensagem");
				e.printStackTrace();
			}
		}
		field.setText("");
	}

	private void iniciarModoServidor() {
		System.err.println("Iniciando modo servidor");
		
		try {
			this.setTitle(this.getTitle() + ": Esperando alguem conectar");
			this.comm = new CommServer();
		} catch (InterruptedIOException e) {
			JOptionPane.showMessageDialog(
				this,
				"Ninguem conectou em 5 segundos",
			    "Erro",
			    JOptionPane.ERROR_MESSAGE
			);
			
			this.mostrarTelaInicial();
			
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					this,
					"Nao pude abrir o modo servidor",
				    "Erro",
				    JOptionPane.ERROR_MESSAGE
			);
			
			this.mostrarTelaInicial();
			return;
		}
		this.numeroJogador = 0; /* sou o X */
		this.jogadorAtual = 0;
		this.comm.addCommEventListener(this);
		this.mostrarTelaTicTac();
	}

	private void iniciarModoCliente() {
		System.err.println("Iniciando modo cliente");
		try {
			String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Qual o servidor?",
                    "Servidor",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "127.0.0.1");
			this.comm = new CommClient(s);
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(
					this,
					"Nao conseguir resolver o nome do servidor",
				    "Erro",
				    JOptionPane.ERROR_MESSAGE
			);
			this.mostrarTelaInicial();
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					this,
					"Nao consegui conectar",
				    "Erro",
				    JOptionPane.ERROR_MESSAGE
			);
			this.mostrarTelaInicial();
			return;
		}
		this.numeroJogador = 1; /* sou o O */
		this.jogadorAtual = 0;
		this.comm.addCommEventListener(this);
		this.mostrarTelaTicTac();
	}
	
	private void mostrarTelaTicTac() {
		this.botaoDesconectar = new JButton("desconectar");
		this.areaMensagem = new Container();
		this.mensagens = new JTextArea();
		this.entryMensagem = new JTextField();
		
		Container content = this.getContentPane();
		
		this.setTitle("Conectado");
		this.botaoDesconectar.addActionListener(this);
		this.mensagens.setEditable(false);
		
		/* Criar area de mensagens */
		this.areaMensagem.setPreferredSize(new Dimension(this.getWidth(), 100));
		this.areaMensagem.setLayout(new BorderLayout());
		this.areaMensagem.add(new JScrollPane(mensagens), BorderLayout.CENTER);
		this.areaMensagem.add(entryMensagem, BorderLayout.SOUTH);
		
		/* Limpar a tela */
		content.removeAll();
		content.setLayout(new BorderLayout());
		
		entryMensagem.addActionListener(this);
		
		/* adicionar componentes */
		content.add(areaMensagem, BorderLayout.SOUTH);
		content.add(botaoDesconectar, BorderLayout.NORTH);
		
		//System.err.println("Mostrando a tela do tictac");
		//this.getContentPane();
		
		/* Criar Area de Jogo */
		this.criarAreaDeJogo();
		this.repaint();
		/* Forcar o paint */
		this.setSize(this.getWidth()-1, this.getHeight()+1);
		this.setSize(this.getWidth()+1, this.getHeight()-1);
		/* é minha vez de jogar? entao avisar */
		if (this.jogadorAtual == this.numeroJogador) {
			JOptionPane.showMessageDialog(
					this,
					"Voce começa",
				    "Sua vez",
				    JOptionPane.WARNING_MESSAGE
			);
			if (this.comm != null) {
				try {
					this.comm.sendEvent(new CommEvent(CommEvent.EVENT_MESSAGE, "eu jogo primeiro",null));
				} catch (IOException e) {
					System.err.println("Nao consegui enviar a mensagem");
					e.printStackTrace();
				}
			}
		}
	}

	private void criarAreaDeJogo() {
		final TicTacToe thiz = this;
		this.areaJogo = new AreaJogo();
		
		/* obter eventos de clique de mouse */
		this.areaJogo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				thiz.mouseClicked(e);
			}
		});
		
		/* iniciar tabuleiro */
		this.tabuleiro = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.tabuleiro[i][j] = -1;
			}
		}
		this.getContentPane().add(this.areaJogo, BorderLayout.CENTER);
	}

	protected void mouseClicked(MouseEvent e) {
		Point p = this.areaJogo.getReferencePosition(e.getPoint());
		//System.out.println();
		System.err.println(this.tabuleiro[p.x][p.y]);
		this.tentarMarcar(p);
	}

	private void tentarMarcar(Point p) {
		/* é minha vez? */
		if (this.jogadorAtual == this.numeroJogador) {
			/* é um local vazio? */
			if (this.tabuleiro[p.x][p.y] == -1) {
				/* posso marcar entao */
				this.marcarPosicao(p, this.numeroJogador);
				/* avisar da marcacao */
				this.notificarMarcacao(p);
				/* trocar de jogador */
				this.trocarDeJogador();
			}
		} else {
			/* não é minha vez */
			JOptionPane.showMessageDialog(
					this,
					"Não é sua vez ainda",
				    "oops",
				    JOptionPane.WARNING_MESSAGE
			);
		}
		/* Verificar se alguem ganhou */
		this.alguemGanhou();
	}
	private void trocarDeJogador() {
		/* trocar de jogador */
		this.jogadorAtual = Math.abs(this.jogadorAtual - 1);
	}
	private void notificarMarcacao(Point p) {
		if (this.comm != null) {
			try {
				this.comm.sendEvent(new CommEvent(CommEvent.EVENT_MARK,null,p));
			} catch (IOException e) {
				System.err.println("Nao consegui enviar a mensagem");
				e.printStackTrace();
			}
		}
	}
	private void alguemGanhou() {
		int ganhou = -1;
		int t[][] = this.tabuleiro;
		/*FIXME: METODO BURRO para saber */
		/* vertical */
		for(int j=0; j<3; j++) {
			if (ganhou == -1) {
				for(int i=1; i<3; i++) {
					if (t[i][j] != t[i-1][j]) {
						ganhou = -1;
						break;
					} else {
						ganhou = t[i][j];
					}
				}
			}
		}
		/* horizontal */
		if (ganhou == -1) {
			for(int i=0; i<3; i++){
				if (ganhou == -1) {
					for(int j=1; j<3; j++){
						if (t[i][j] != t[i][j-1]) {
							ganhou = -1;
							break;
						} else {
							ganhou = t[i][j];
						} 
					}
				}
			}
		}
		/* diagonal direta */
		if (ganhou == -1) {
			for(int i = 1; i < 3; i++) {
				if (t[i][i] != t[i-1][i-1]){
					ganhou = -1;
					break;
				} else {
					ganhou = t[i][i];
				}
			}
		}
		/* diagnoal inversa */
		if (ganhou == -1) {
			for(int i = 1, j = 2; i < 3; i++, j--) {
				if (t[i][j-1] != t[i-1][j]){
					ganhou = -1;
					break;
				} else {
					ganhou = t[i][j-1];
				}
			}
		}
		
		if (ganhou != -1) {
			if (this.numeroJogador == ganhou) {
				/* eu ganhei */
				JOptionPane.showMessageDialog(
						this,
						"Voce ganhou, parabens",
					    "Resultado",
					    JOptionPane.WARNING_MESSAGE
				);	
			} else {
				JOptionPane.showMessageDialog(
						this,
						"Voce perdeu, mais sorte da proxima vez",
					    "Resultado",
					    JOptionPane.WARNING_MESSAGE
				);
			}
		}
	}

	private void marcarPosicao(Point p, int i) {
		/* minha tabela */
		this.tabuleiro[p.x][p.y] = i;
		/* atualizar graficos */
		this.areaJogo.marcar(this.tabuleiro);
		this.repaint();
	}

	public void disconnected(CommEvent ev) {
		JOptionPane.showMessageDialog(
				this,
				"Desconectado pela outra ponta",
			    "Desconectado",
			    JOptionPane.WARNING_MESSAGE
		);
		this.desconectar();
		
	}
	
}

