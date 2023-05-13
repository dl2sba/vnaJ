package krause.vna.gui.raw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;

import krause.common.exception.ProcessingException;
import krause.common.gui.DocumentSizeFilter;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNARawCommentField extends JPanel implements PropertyChangeListener, ActionListener {

	private JTextArea txtComment;
	private Window owner = null;

	/**
	 * 
	 * @param owner
	 * @param saveMode
	 *            true, comment is editable
	 */
	public VNARawCommentField(Window owner, boolean saveMode) {
		setLayout(new MigLayout("", "[grow,fill]", "[]"));
		setBorder(new TitledBorder(VNAMessages.getString("VNARawCommentField.title")));

		if (saveMode) {
			add(new JLabel(VNAMessages.getString("VNARawCommentField.hint")), "wrap");
		}
		txtComment = new JTextArea(10, 40);
		txtComment.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtComment.setLineWrap(true);
		txtComment.setWrapStyleWord(true);
		txtComment.setEditable(saveMode);
		txtComment.setVisible(saveMode);
		txtComment.setOpaque(true);

		AbstractDocument pDoc = (AbstractDocument) txtComment.getDocument();

		pDoc.setDocumentFilter(new DocumentSizeFilter(512));

		JScrollPane scrollPane = new JScrollPane(txtComment);

		add(scrollPane, "growy");
	}

	public void setText(String string) {
		txtComment.setText(string);
	}

	public String getText() {
		return txtComment.getText();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String pname = e.getPropertyName();
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(pname)) {
			// Ok, the user selected a file in the chooser
			File file = (File) e.getNewValue();

			String cmt;
			try {
				cmt = new VNARawHandler(owner).readComment(file);
				txtComment.setBackground(getBackground());
				if (cmt != null) {
					txtComment.setText(cmt);
					txtComment.setVisible(true);
				} else {
					txtComment.setText("");
					txtComment.setVisible(false);
				}
			} catch (ProcessingException ex) {
				txtComment.setBackground(Color.RED);
				txtComment.setText(ex.getLocalizedMessage());
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
