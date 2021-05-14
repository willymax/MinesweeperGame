import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * This class creates a Minesweeper game on a GUI
 */

public class MinesweeperGame extends JFrame implements ActionListener {
    //declare component variables
    private JLabel statusBar;
    private JLabel timerLabel;
    private JMenu menu;
    private BoardPanel gameBoardPanel;
    private JMenuItem newItem;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem exit;

    /**
     * Class constructor
     */
    public MinesweeperGame() {
        this.setTitle("Minesweeper Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statusBar = new JLabel("");
        timerLabel = new JLabel("Time remaining: 10000");
        timerLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        timerLabel.setForeground(Color.GREEN);
        gameBoardPanel = new BoardPanel(statusBar, timerLabel);
        JMenuBar mb = new JMenuBar();
        menu = new JMenu("File");
        newItem = new JMenuItem("New");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        exit = new JMenuItem("Exit");
        newItem.addActionListener(this);
        open.addActionListener(this);
        save.addActionListener(this);
        exit.addActionListener(this);
        menu.add(newItem);
        menu.add(open);
        menu.add(save);
        menu.add(exit);
        mb.add(menu);
        this.add(statusBar, BorderLayout.SOUTH);
        this.add(timerLabel, BorderLayout.NORTH);
        this.add(gameBoardPanel);
        this.setJMenuBar(mb);
        this.pack();
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MinesweeperGame ex = new MinesweeperGame();
            ex.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) {
            gameBoardPanel.resetGame();
        }
        if (e.getSource() == save) {

        }
        if (e.getSource() == open) {

        }
        if (e.getSource() == exit) {

        }
    }
}