package io.github.mcengine.currency.app;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class MCEngineCurrencyApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MCEngine Currency");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            String htmlContent = """
                <html>
                <body style='
                    font-family: "Segoe UI", Roboto, Arial, sans-serif;
                    text-align: center;
                    padding: 30px;
                    background-color: #f8f9fa;
                    color: #212529;
                '>
                    <h2 style='color: black;'>Welcome to <span style="font-weight: 600;">MCEngine Currency</span></h2>
                    <p style='margin: 10px 0;'>
                    <a href='https://mcengine.github.io/currency-website' style='
                        text-decoration: none;
                        color: #0d6efd;
                        font-weight: 500;
                    '>Visit Website</a>
                    </p>
                    <hr style='margin: 20px auto; border: none; border-top: 1px solid #dee2e6; width: 80%;' />
                    <p style='margin-top: 20px;'>
                    <a href='https://github.com/MCEngine' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Organization</a>
                    |
                    <a href='https://github.com/MCEngine/currency' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Repository</a>
                    |
                    <a href='https://mcengine.github.io/donation-website' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Donation</a>
                    </p>
                </body>
                </html>
                """;

            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditorKit(new HTMLEditorKit());
            editorPane.setText(htmlContent);
            editorPane.setEditable(false);
            editorPane.setOpaque(false);
            editorPane.setFocusable(false);
            editorPane.setContentType("text/html");

            editorPane.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(URI.create(e.getURL().toString()));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(null);
            frame.getContentPane().add(scrollPane);
            frame.setVisible(true);
        });
    }
}
