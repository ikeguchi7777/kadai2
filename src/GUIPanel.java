import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

class AddPanel extends JPanel{
	public AddPanel() {
		JTextField text = new JTextField("文を入力してください。",50);
		JButton button = new JButton("add");

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);

			}
		};
		button.addActionListener(listener);
		add(text,BorderLayout.CENTER);
		add(button,BorderLayout.PAGE_END);
	}
}

class SearchPanel extends JPanel{
	public SearchPanel() {
		JTextField text = new JTextField("文を入力してください。",50);
		JButton button = new JButton("Search");

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);

			}
		};
		button.addActionListener(listener);
		add(text,BorderLayout.CENTER);
		add(button,BorderLayout.PAGE_END);
	}
}

class RemovePanel extends JPanel{
	public RemovePanel() {
		JTextField text = new JTextField("文を入力してください。",50);
		JButton button = new JButton("remove");

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);

			}
		};
		button.addActionListener(listener);
		add(text,BorderLayout.CENTER);
		add(button,BorderLayout.PAGE_END);
	}
}