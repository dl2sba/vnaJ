package krause.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

public class JRoundButton2 extends JButton {
	public JRoundButton2() {
		this(null, null);
	}

	public JRoundButton2(Icon icon) {
		this(null, icon);
	}

	public JRoundButton2(String text) {
		this(text, null);
	}

	public JRoundButton2(Action a) {
		this();
		setAction(a);
	}

	public JRoundButton2(String text, Icon icon) {
		setModel(new DefaultButtonModel());
		init(text, icon);
		if (icon == null) {
			return;
		}
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setBackground(Color.BLACK);
		setContentAreaFilled(false);
		setFocusPainted(false);
		// setVerticalAlignment(SwingConstants.TOP)
		setAlignmentY(Component.TOP_ALIGNMENT);
		initShape();
	}

	protected transient Shape shape;
	protected transient Shape base;

	protected void initShape() {
		if (!getBounds().equals(this.base)) {
			Dimension s = getPreferredSize();
			this.base = getBounds();
			this.shape = new Ellipse2D.Double(0, 0, s.width - 1.0, s.height - 1.0);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Icon icon = getIcon();
		Insets i = getInsets();
		int iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
		return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
	}

	@Override
	protected void paintBorder(Graphics g) {
		initShape();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		// g2.setStroke(new BasicStroke(1.0f))
		g2.draw(this.shape);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	@Override
	public boolean contains(int x, int y) {
		initShape();
		return this.shape.contains(x, y);
		// or return super.contains(x, y) && ((image.getRGB(x, y) >> 24) & 0xff) > 0
	}
}
