package io.github.mcengine.currency.app.page;

public class Footer {
    public static String getHtml() {
        return """
            <hr style='margin: 20px auto; border: none; border-top: 1px solid #dee2e6; width: 80%;' />
            <p style='margin-top: 20px;'>
                <a href='https://github.com/MCEngine' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Organization</a>
                |
                <a href='https://github.com/MCEngine/currency' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Repository</a>
                |
                <a href='https://mcengine.github.io/donation-website' style='margin: 0 10px; color: #0d6efd; text-decoration: none;'>Donation</a>
            </p>
        """;
    }
}
