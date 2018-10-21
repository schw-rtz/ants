package se.devcode.graph.ants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import se.devcode.graph.ants.FindPathFrame.KLabel;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class FindPathFrame extends JFrame {

	public class KLabel extends JLabel {

		public KLabel(String string) {
			super(string);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);

			drawJakobsbergToSundbyberg(g);
		}

		private void drawJakobsbergToSundbyberg(Graphics g) {
			PairXY<Integer, Integer> p1 = getJakobsbergPoint();
			PairXY<Integer, Integer> p2 = getSundbybergPoint();

			g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}

	}

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblNewLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FindPathFrame frame = new FindPathFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FindPathFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1170, 756);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPanel(), BorderLayout.SOUTH);
		contentPane.add(getLblNewLabel(), BorderLayout.CENTER);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
		}
		return panel;
	}

	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new KLabel("");
			lblNewLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					double x = e.getX() / (double) lblNewLabel.getWidth();
					double y = e.getY() / (double) lblNewLabel.getHeight();

					System.out.println(x + ":" + y);
				}
			});
			lblNewLabel.addComponentListener(new ComponentListener() {

				@Override
				public void componentShown(ComponentEvent e) {
					scaleImage();
					drawlines();
				}

				@Override
				public void componentResized(ComponentEvent e) {
					scaleImage();
					drawlines();
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					scaleImage();
					drawlines();
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					scaleImage();
					drawlines();
				}
			});
			scaleImage();
		}
		return lblNewLabel;
	}

	protected void drawlines() {

	}

	private ImageIcon getImage() {
		ImageIcon imageIcon = new ImageIcon(FindPathFrame.class.getResource("/map.jpg"));
		int width2 = getLblNewLabel().getWidth();
		int height2 = getLblNewLabel().getHeight();
		Image image = null;
		if (width2 != 0 && height2 != 0) {
			image = imageIcon.getImage().getScaledInstance(width2, height2, Image.SCALE_DEFAULT);
		}
		return Optional.ofNullable(image).map(xx -> new ImageIcon(xx)).orElse(null);
	}

	protected void scaleImage() {
		getLblNewLabel().setIcon(getImage());
	}

	private PairXY<Integer, Integer> getJakobsbergPoint() {
		int x1 = Math.round(0.2714535901926445f * this.getWidth());
		int y1 = Math.round(0.3047895500725689f * this.getHeight());

		PairXY<Integer, Integer> p1 = new PairXY<Integer, Integer>(x1, y1);
		return p1;
	}

	private PairXY<Integer, Integer> getSundbybergPoint() {
		int x2 = Math.round(0.37653239929947463f * this.getWidth());
		int y2 = Math.round(0.41944847605224966f * this.getHeight());

		PairXY<Integer, Integer> p2 = new PairXY<Integer, Integer>(x2, y2);
		return p2;
	}
}
