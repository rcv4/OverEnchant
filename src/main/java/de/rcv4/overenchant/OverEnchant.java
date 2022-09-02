package de.rcv4.overenchant;

import de.rcv4.overenchant.listener.AnvilListener;
import org.bukkit.plugin.java.JavaPlugin;

public class OverEnchant extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        this.getLogger().info("Enabled!");
    }



}
