package io.github.mcengine.currency.app.page;

public class Home {
    public static String getHtml() {
        return """
            <table align='center' width='800' cellpadding='10' style='background-color: #ffffff; border-radius: 12px; margin-top: 50px;'>
                <tr>
                    <td>
                        <h2 style='text-align: center; color: #343a40;'>Welcome to MCEngine Currency</h2>
                        <p style='text-align: center;'>
                            This is the homepage for <strong>MCEngine Currency</strong> — a powerful Minecraft plugin to manage virtual currencies with ease.
                        </p>
                        <div style='text-align: center;'>
                            <h4>Features:</h4>
                            <ul style='margin: 0 auto; display: table; text-align: center; list-style-type:none;'>
                                <li>Multi-coin system: Coin, Copper, Silver, and Gold</li>
                                <li>MySQL & SQLite support</li>
                                <li>Deposit and withdraw via in-game items</li>
                                <li>Real-time balance updates and logs</li>
                                <li>Custom commands for players and admins</li>
                            </ul>
                        </div>
                        <p style='text-align: center; margin-top: 30px;'>
                            Use the menu above to explore more about how to use commands, handle donations, and integrate listeners.
                        </p>
                    </td>
                </tr>
            </table>
        """;
    }
}
