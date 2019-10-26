import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

class AddPanel extends JPanel {
	public AddPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("追加");
		layout.putConstraint(SpringLayout.NORTH, label,5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label,0, SpringLayout.HORIZONTAL_CENTER, this);
		JPanel p1 = new JPanel();
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("add");
		p1.add(text);
		p1.add(button);
		layout.putConstraint(SpringLayout.NORTH, p1,5, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, p1,200, SpringLayout.WEST, this);
		JPanel p2 = new JPanel();
		JTextField adress = new JTextField("ファイルのアドレスを入力してください。", 50);
		JButton button1 = new JButton("参照");
		JButton button2 = new JButton("読み込み");
		p2.add(adress);
		p2.add(button1);
		p2.add(button2);
		layout.putConstraint(SpringLayout.NORTH, p2,5, SpringLayout.SOUTH, p1);
		layout.putConstraint(SpringLayout.WEST, p2,200, SpringLayout.WEST, this);
		ActionListener listener1 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);
				text.setText("");

			}
		};
		ActionListener listener2 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = System.getProperty("user.dir");
				JFileChooser filechooser = new JFileChooser(path);
				filechooser.setFileFilter(new TxtFilter());
				int selected = filechooser.showOpenDialog(p2);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					adress.setText(file.getAbsolutePath());
				}
			}
		};
		ActionListener listener3 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(adress.getText());
				if (file.exists())
					System.out.println(file.getName());
				else
					adress.setText("正しいアドレスを入力してください。");

			}
		};
		button.addActionListener(listener1);
		button1.addActionListener(listener2);
		button2.addActionListener(listener3);
		TextLog log = new TextLog(15, 50);
		layout.putConstraint(SpringLayout.NORTH, log,10, SpringLayout.SOUTH, p2);
		layout.putConstraint(SpringLayout.WEST, log,200, SpringLayout.WEST, this);
		add(label);
		add(p1);
		add(p2);
		add(log);
	}
}

class SearchPanel extends JPanel {
	public SearchPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("検索");
		layout.putConstraint(SpringLayout.NORTH, label,5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label,0, SpringLayout.HORIZONTAL_CENTER, this);
		JPanel panel = new JPanel();
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("Search");
		panel.add(text);
		panel.add(button);
		layout.putConstraint(SpringLayout.NORTH, panel,5, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, panel,0, SpringLayout.HORIZONTAL_CENTER, this);
		TextLog log = new TextLog(20, 70);
		layout.putConstraint(SpringLayout.NORTH, log,5, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, log,0, SpringLayout.HORIZONTAL_CENTER, this);
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);

			}
		};
		button.addActionListener(listener);
		add(panel);
		add(log);
	}
}

class RemovePanel extends JPanel {
	public RemovePanel() {
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("remove");

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				System.out.println(str);

			}
		};
		button.addActionListener(listener);
		add(text, BorderLayout.CENTER);
		add(button, BorderLayout.PAGE_END);
	}
}

class TxtFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String ext = getExtension(f);
		if (ext != null) {
			if (ext.equals("txt")) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	public String getDescription() {
		return "TXTファイル";
	}

	/* 拡張子を取り出す */
	private String getExtension(File f) {
		String ext = null;
		String filename = f.getName();
		int dotIndex = filename.lastIndexOf('.');

		if ((dotIndex > 0) && (dotIndex < filename.length() - 1)) {
			ext = filename.substring(dotIndex + 1).toLowerCase();
		}

		return ext;
	}
}

class TextLog extends JScrollPane{
	JTextArea area;
	public TextLog(int rows,int columns) {
		super();
		area = new JTextArea(rows, columns);
		EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
		area.setEditable(false);
		area.setBorder(border);
		setViewportView(area);
	}

	public void addLog(String txt) {
		area.insert(txt+"\n", 0);
	}
}