package clientApp;

import javax.swing.*;
import java.awt.*;


public class ApplicationFrame {

    private JFrame mainFrame;
    private JTextField sendTextField;
    private JTextArea textArea;

    public ApplicationFrame() {

        mainFrame = new JFrame();
        mainFrame.setTitle("Qiper v1.0 alpha");
        mainFrame.setBounds(450,120,350, 480);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setJMenuBar(menuBar());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(centerPanel(), BorderLayout.CENTER);
        mainFrame.add(bottomPanel(), BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        menuBar.add(fileMenu, BorderLayout.CENTER);
        return menuBar;
    }

    private JPanel centerPanel() {
        JPanel center = new JPanel();
        textArea = new JTextArea();
        textArea.setEditable(false);
        center.setLayout(new BorderLayout());
        center.add(textArea, BorderLayout.CENTER);
        return center;
    }

    private JPanel bottomPanel() {
        JPanel bottom = new JPanel();
        sendTextField = new JTextField();
        JButton btn = new JButton("Send");
        bottom.setLayout(new BorderLayout());
        bottom.add(sendTextField, BorderLayout.CENTER);
        bottom.add(btn, BorderLayout.EAST);
        btn.addActionListener(e -> sendMessage());
        sendTextField.addActionListener(actionEvent -> sendMessage());
        return bottom;
    }

    private void sendMessage() {
        String send = sendTextField.getText();
        textArea.append(send + "\n");
        sendTextField.setText("");
    }
}