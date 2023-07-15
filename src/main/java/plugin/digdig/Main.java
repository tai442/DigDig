package plugin.digdig;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.digdig.command.DigDigCommand;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        DigDigCommand digDigCommand = new DigDigCommand(this);
        Bukkit.getPluginManager().registerEvents(digDigCommand, this);
        getCommand("digDig").setExecutor(digDigCommand);

    }
}

