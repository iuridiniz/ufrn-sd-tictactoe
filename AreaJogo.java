import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JComponent;


public class AreaJogo extends JComponent {
	Image cross;
	Image table;
	Image circle;
	private int[][] tabuleiro;
	public static final int offset_i = 5;
	public static final int offset_j = 5;
	public AreaJogo() {
		/* Abrir imagens */
		this.cross = Toolkit.getDefaultToolkit().getImage("./cross.png");
		this.table = Toolkit.getDefaultToolkit().getImage("./table.png");
		this.circle = Toolkit.getDefaultToolkit().getImage("./circle.png");
		
	}
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(table, 0, 0, this);
		if (tabuleiro == null) {
			return;
		}
		for(int i=0; i < 3; i++) {
			for (int j=0; j < 3; j++) {
				switch (this.tabuleiro[i][j]) {
				case 0:
					/* Cross */
					g2.drawImage(cross, 100 * i + AreaJogo.offset_i, 100 * j + + AreaJogo.offset_j, this);
					break;
				case 1:
					/* Circulo */
					g2.drawImage(circle, 100 * i + AreaJogo.offset_i, 100 * j + AreaJogo.offset_j, this);
				default:
					break;
				}
			}
		}
	}
	
	public Point getReferencePosition(Point p) {
		Point rp = new Point(-1,-1);
		if (p.x < 98) {
			rp.x = 0;
		} else if (p.x < 197) {
			rp.x = 1; 
		} else if (p.x < 300) {
			rp.x = 2;
		}
		
		if (p.y < 98) {
			rp.y = 0;
		} else if (p.y < 197 ) {
			rp.y = 1;
		} else if (p.y < 300) {
			rp.y = 2;
		}
		return rp;
	}

	public void marcar(int[][] tabuleiro) {
		this.tabuleiro = tabuleiro;
		this.repaint();
	}
}

