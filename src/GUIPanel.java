import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

class AddPanel extends JPanel {
	TextLog log;

	public AddPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("追加");
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, this);
		JPanel p1 = new JPanel();
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("追加");
		p1.add(text);
		p1.add(button);
		layout.putConstraint(SpringLayout.NORTH, p1, 5, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, p1, 200, SpringLayout.WEST, this);
		JPanel p2 = new JPanel();
		JTextField adress = new JTextField("ファイルのアドレスを入力してください。", 50);
		JButton button1 = new JButton("参照");
		JButton button2 = new JButton("追加");
		p2.add(adress);
		p2.add(button1);
		p2.add(button2);
		layout.putConstraint(SpringLayout.NORTH, p2, 5, SpringLayout.SOUTH, p1);
		layout.putConstraint(SpringLayout.WEST, p2, 200, SpringLayout.WEST, this);
		ActionListener listener1 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				addFromText(str);
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
					addFromFile(file);
				else
					adress.setText("正しいアドレスを入力してください。");

			}
		};
		button.addActionListener(listener1);
		button1.addActionListener(listener2);
		button2.addActionListener(listener3);
		log = new TextLog(15, 50);
		layout.putConstraint(SpringLayout.NORTH, log, 10, SpringLayout.SOUTH, p2);
		layout.putConstraint(SpringLayout.WEST, log, 200, SpringLayout.WEST, this);
		add(label);
		add(p1);
		add(p2);
		add(log);
	}

	private void addFromFile(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String s = reader.readLine();
			int count = 0;
			while (s != null) {
				if (!s.equals("")) {
					if (DataBase.getDataBase().insert(s))
						count++;
					else
						log.addLog(s + "はデータベースに追加できません。");
				}
				s = reader.readLine();
			}
			reader.close();
			log.addLog(count + "個のデータが追加されました。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFromText(String text) {
		if (!DataBase.getDataBase().insert(text))
			log.addLog(text + "はデータベースに追加できません。");
		else {
			log.addLog("追加完了");
		}
	}
}

class SearchPanel extends JPanel {
	TextLog log;
	DataBase dataBase = null;

	public SearchPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("検索");
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, this);
		JPanel panel = new JPanel();
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("検索");
		JButton button1 = new JButton("クリア");
		panel.add(text);
		panel.add(button);
		panel.add(button1);
		layout.putConstraint(SpringLayout.NORTH, panel, 5, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, panel, 0, SpringLayout.HORIZONTAL_CENTER, this);
		log = new TextLog(20, 70);
		layout.putConstraint(SpringLayout.NORTH, log, 5, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, log, 0, SpringLayout.HORIZONTAL_CENTER, this);
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				if (dataBase == null)
					dataBase = DataBase.getDataBase();
				(new Unifier()).search(str);
				ArrayList<String> varSets = Unifier.getVarSets();
				if (varSets.isEmpty()) {
					log.addLog("検索失敗");
					return;
				}
				for (String s : Unifier.getVarSets()){
					log.addLog(s);
				}
			}
		};
		ActionListener listener2 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataBase = DataBase.getDataBase();
				log.addLog("検索履歴がクリアされました。");
			}
		};
		button.addActionListener(listener);
		button1.addActionListener(listener2);
		add(label);
		add(panel);
		add(log);
	}
}

class RemovePanel extends JPanel {
	TextLog log;
	DataBase dataBase = null;

	public RemovePanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("削除");
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, this);
		add(label);
		JPanel panel = new JPanel();
		JTextField text = new JTextField("文を入力してください。", 50);
		JButton button = new JButton("削除");
		panel.add(text);
		panel.add(button);
		layout.putConstraint(SpringLayout.NORTH, panel, 5, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, panel, 0, SpringLayout.HORIZONTAL_CENTER, this);
		add(panel);
		log = new TextLog(20, 70);
		layout.putConstraint(SpringLayout.NORTH, log, 5, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, log, 0, SpringLayout.HORIZONTAL_CENTER, this);
		add(log);
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				if (dataBase == null)
					dataBase = DataBase.getDataBase();
				(new Unifier()).search(str);
				ArrayList<String> matchList = Unifier.getMatchList();
				if(!matchList.isEmpty()){
					for (String string : matchList) {
						if(DataBase.getDataBase().remove(string))
							log.addLog(string + "を削除しました。");
					}
				}else{
					log.addLog(str + "は見つかりませんでした。");
				}
			}
		};
		button.addActionListener(listener);

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

class TextLog extends JScrollPane {
	JTextArea area;
	JViewport viewport;

	public TextLog(int rows, int columns) {
		super();
		area = new JTextArea(rows, columns);
		EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
		area.setEditable(false);
		area.setBorder(border);
		setViewportView(area);
		viewport = getViewport();
	}

	public void addLog(String txt) {
		area.append(txt + "\n");
	}
}