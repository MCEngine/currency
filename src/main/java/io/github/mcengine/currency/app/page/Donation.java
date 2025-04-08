package io.github.mcengine.currency.app.page;

public class Donation {
    public static String getHtml() {
        return """
            <h3 style='color: #212529;'>Support Development</h3>
            <p>Consider donating to keep the project going!</p>
            <a href='https://mcengine.github.io/donation-website' style='color: #0d6efd; text-decoration: none;'>
                Go to Donation Page
            </a>
        """;
    }
}
