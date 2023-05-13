package krause.vna.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * MySwing: Advanced Swing Utilites Copyright (C) 2005 Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * 
 * http://stackoverflow.com/questions/8076794/create-a-titled-border-with-the-title-as-a-jcheckbox
 * http://www.javalobby.org/java/forums/t33048.html
 * 
 */
public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener, SwingConstants {
	private int offset = 10;
	private Component comp;
	private JComponent container;
	private Rectangle rect;
	private Border border;
	private boolean mouseEntered = false;

	public ComponentTitledBorder(Component comp, JComponent container, Border border) {
		this.comp = comp;
		this.container = container;
		this.border = border;
		container.addMouseListener(this);
		container.addMouseMotionListener(this);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets borderInsets = border.getBorderInsets(c);
		Insets insets = getBorderInsets(c);
		int temp = (insets.top - borderInsets.top) / 2;
		border.paintBorder(c, g, x, y + temp, width, height - temp);
		Dimension size = comp.getPreferredSize();
		rect = new Rectangle(offset, 0, size.width, size.height);
		SwingUtilities.paintComponent(g, comp, (Container) c, rect);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		Dimension size = comp.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	private void dispatchEvent(MouseEvent me) {
		if (rect != null && rect.contains(me.getX(), me.getY())) {
			dispatchEvent(me, me.getID());
		}
	}

	private void dispatchEvent(MouseEvent me, int id) {
		Point pt = me.getPoint();
		pt.translate(-offset, 0);

		comp.setSize(rect.width, rect.height);
		comp.dispatchEvent(new MouseEvent(comp, id, me.getWhen(), me.getModifiersEx(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
		container.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		dispatchEvent(me);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent me) {
		// not implemented
	}

	@Override
	public void mouseExited(MouseEvent me) {
		if (mouseEntered) {
			mouseEntered = false;
			dispatchEvent(me, MouseEvent.MOUSE_EXITED);
		}
	}

	@Override
	public void mousePressed(MouseEvent me) {
		dispatchEvent(me);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		dispatchEvent(me);
	}

	@Override
	/*
	 * do nothing (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		// not implemented
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		if (rect == null) {
			return;
		}

		if (!mouseEntered && rect.contains(me.getX(), me.getY())) {
			mouseEntered = true;
			dispatchEvent(me, MouseEvent.MOUSE_ENTERED);
		} else if (mouseEntered) {
			if (!rect.contains(me.getX(), me.getY())) {
				mouseEntered = false;
				dispatchEvent(me, MouseEvent.MOUSE_EXITED);
			} else {
				dispatchEvent(me, MouseEvent.MOUSE_MOVED);
			}
		}
	}
}
