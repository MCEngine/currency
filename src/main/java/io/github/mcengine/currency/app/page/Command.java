package io.github.mcengine.currency.app.page;

public class Command {
    public static String getHtml() {
        return """
            <h3 style='color: #212529;'>SpigotMC Command Usage</h3>
            <p>This plugin provides the <code>/currency</code> command with the following subcommands:</p>
            <pre style='
                background-color: #f1f1f1;
                border: 1px solid #ccc;
                padding: 10px;
                text-align: left;
                font-family: monospace;
                font-size: 14px;
                overflow-x: auto;
            '>
commands:
  /currency:
    add <player> <coinType> <amount>
    cash <coinType> <amount>
    check <coinType>
    pay <player> <amount> <coinType> <note>
            </pre>
            <ul style='text-align: left; max-width: 700px; margin: 20px auto;'>
                <li><strong>/currency add &lt;player&gt; &lt;coinType&gt; &lt;amount&gt;</strong> - Add currency to a player (Admin only)</li>
                <li><strong>/currency cash &lt;coinType&gt; &lt;amount&gt;</strong> - Convert currency into a HeadDB item</li>
                <li><strong>/currency check &lt;coinType&gt;</strong> - Check your own currency balance</li>
                <li><strong>/currency pay &lt;player&gt; &lt;amount&gt; &lt;currencyType&gt; &lt;note&gt;</strong> - Pay currency to another player with a note</li>
            </ul>
        """;
    }
}
