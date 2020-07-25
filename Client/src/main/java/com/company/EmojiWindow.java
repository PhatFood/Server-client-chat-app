package com.company;

import com.vdurmont.emoji.EmojiParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class EmojiWindow extends JFrame {
    public EmojiWindow(ClientInbox clientInbox, JTextField jTextFieldInput)
    {
        setSize(250,230);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        jPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String [] emojis = {":smile:",":grinning:",":relaxed:",":heart_eyes:",":kissing_closed_eyes:",":kissing_smiling_eyes:",":stuck_out_tongue_closed_eyes:",":flushed:",":pensive:",":unamused:",":persevere:",":joy:",":sleepy:",":cold_sweat:",":scream:",":kissing_heart:",":stuck_out_tongue_winking_eye:",":stuck_out_tongue:",":sob:",":mask:"};
        ArrayList<JButton> buttons = new ArrayList<>();

        for (String emoji : emojis)
        {
            buttons.add(new JButton(EmojiParser.parseToUnicode(emoji)));
        }

        for (JButton button : buttons)
        {
            jPanel.add(button);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = jTextFieldInput.getText() + button.getText();
                    jTextFieldInput.setText(message);
                }
            });
        }

        add(jPanel);

        setLocationRelativeTo(clientInbox);
        setAlwaysOnTop(true);
        setVisible(true);
    }
}
