package io.github.mcengine.currency.app.page;

public class Header {
    public static String getHtml() {
        return """
            <h2 style='color: black;'>Welcome to <span style="font-weight: 600;">MCEngine Currency</span></h2>
            <div style='margin: 20px 0;'>
                <a href='page:home' style='margin: 0 15px; color: #0d6efd; text-decoration: none;'>Home</a>
                <a href='page:command' style='margin: 0 15px; color: #0d6efd; text-decoration: none;'>Command</a>
                <a href='page:donation' style='margin: 0 15px; color: #0d6efd; text-decoration: none;'>Donation</a>
            </div>
        """;
    }
}
