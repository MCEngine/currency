package io.github.mcengine.currency.app;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

import io.github.mcengine.currency.app.page.*;

public class MCEngineCurrencyApp {
    private static JEditorPane editorPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MCEngine Currency");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            editorPane = new JEditorPane();
            editorPane.setEditorKit(new HTMLEditorKit());
            editorPane.setEditable(false);
            editorPane.setFocusable(false);
            editorPane.setContentType("text/html");

            loadPage("home");

            editorPane.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String url = e.getDescription();
                    if (url.startsWith("page:")) {
                        loadPage(url.substring(5));
                    } else {
                        try {
                            Desktop.getDesktop().browse(URI.create(url));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Failed to open link: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            frame.getContentPane().add(scrollPane);

            frame.setVisible(true);
        });
    }

    private static void loadPage(String page) {
        String content = switch (page) {
            case "command" -> Command.getHtml();
            case "donation" -> Donation.getHtml();
            case "home" -> Home.getHtml();
            case "listener" -> Listener.getHtml();
            default -> "<p style='color: red;'>Page not found</p>";
        };

        String htmlContent = """
            <html>
            <body style='
                font-family: "Segoe UI", Roboto, Arial, sans-serif;
                text-align: center;
                padding: 30px;
                background-color: #f8f9fa;
                color: #212529;
            '>
        """ + Header.getHtml() + content + Footer.getHtml() + """
            </body>
            </html>
        """;

        editorPane.setText(htmlContent);
        editorPane.setCaretPosition(0); // Scroll to top
    }
}
