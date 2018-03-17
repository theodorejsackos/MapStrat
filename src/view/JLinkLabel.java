package view;

import javax.swing.*;
import java.awt.*;

public class JLinkLabel extends JLabel {

    public JLinkLabel(String text){
        super(text);//super("<html><font color=#4040FF>" + text + "</font></html>");
        setHorizontalAlignment(JLabel.CENTER);
        setFont(new Font("Sans-Serif", Font.PLAIN, 12));
        setForeground(new Color(50, 50, 255));
    }
}
