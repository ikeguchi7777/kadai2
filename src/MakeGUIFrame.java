import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MakeGUIFrame {
	public static void MakeMatchingGUI() {
		FrameBase frame = new FrameBase("test", 1000, 500);
		JPanel cardPanel;
		CardLayout layout;
		/* カード1 */
	    AddPanel card1 = new AddPanel();

	    /* カード2 */
	    SearchPanel card2 = new SearchPanel();

	    /* カード3 */
	    RemovePanel card3 = new RemovePanel();

	    cardPanel = new JPanel();
	    layout = new CardLayout();
	    cardPanel.setLayout(layout);

	    cardPanel.add(card1, "Add");
	    cardPanel.add(card2, "Search");
	    cardPanel.add(card3, "Remove");

	    ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e){
			    String cmd = e.getActionCommand();

			    switch (cmd) {
				case "Add":
				case "Search":
				case "Remove":
					layout.show(cardPanel, cmd);
					break;
				case "Exit":
					System.exit(0);
					break;
				default:
					System.out.println("ボタンの割り当てが存在しない");
					break;
				}
			}
		};

	    /* カード移動用ボタン */
	    JButton addButton = new JButton("追加");
	    addButton.addActionListener(listener);
	    addButton.setActionCommand("Add");

	    JButton searchButton = new JButton("検索");
	    searchButton.addActionListener(listener);
	    searchButton.setActionCommand("Search");

	    JButton removeButton = new JButton("削除");
	    removeButton.addActionListener(listener);
	    removeButton.setActionCommand("Remove");

	    JButton exitButton = new JButton("閉じる");
	    exitButton.addActionListener(listener);
	    exitButton.setActionCommand("Exit");

	    JPanel btnPanel = new JPanel();
	    btnPanel.add(addButton);
	    btnPanel.add(searchButton);
	    btnPanel.add(removeButton);
	    btnPanel.add(exitButton);

	    frame.add(cardPanel, BorderLayout.CENTER);
	    frame.add(btnPanel, BorderLayout.PAGE_END);

	    frame.setVisible(true);
	}

	public static void main(String[] args) {
		MakeMatchingGUI();
	}
}