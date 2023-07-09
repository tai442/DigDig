package plugin.digdig;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.FireworkMeta;
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

