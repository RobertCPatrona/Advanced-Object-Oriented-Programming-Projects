import javax.swing.*;
import java.awt.*;

/*This is the view class where we create a console which visualizes all important actions that happen in the threads.
 * We use a JScrollPane which contains a text area. The function addText is called by the threads whenever they execute an important action. */

public class View extends JFrame {

    JTextArea textArea = new JTextArea(40, 35);

    public View() {
        super("List of Actions");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.LIGHT_GRAY);
        setSize(500, 700);
        setLocationRelativeTo(null);

        JScrollPane scroll = new JScrollPane(textArea);
        this.add(scroll);

        setVisible(true);
    }

    public synchronized void addText(String str) {
        this.textArea.append(str + "\n");
        this.repaint();
    }

}
