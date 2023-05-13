/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.vna.gui.about;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAAboutDialog extends KrauseDialog {
	public VNAAboutDialog(VNAMainFrame f) {
		super(f.getJFrame(), true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		JLabel l;
		URL url = getClass().getResource(VNAMessages.getString("VNAAboutDialog.filename"));
		l = new JLabel(new ImageIcon(url));
		l.setToolTipText(VNAMessages.getString("VNAAboutDialog.tooltip"));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(VNAMessages.getString("Application.URL")));
				} catch (IOException e1) {
					ErrorLogHelper.exception(this, "mouseClicked", e1);
				}
			}
		});
		getContentPane().add(l, BorderLayout.CENTER);
		setTitle(VNAMessages.getString("VNAAboutDialog.title"));
		//
		addEscapeKey();
		showCentered(f.getJFrame());
	}

	@Override
	protected void doDialogCancel() {
		setVisible(false);
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	@Override
	protected void doDialogInit() {
	}
}
