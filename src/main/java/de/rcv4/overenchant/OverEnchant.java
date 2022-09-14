package de.rcv4.overenchant;

import de.rcv4.overenchant.listener.AnvilListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class OverEnchant extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AnvilListener(getLogger(), loadConfig()), this);
        this.getLogger().info("Enabled!");
    }

    private Map<String, Object> loadConfig() {

        getConfig().options().header("Limits lower than the default max limits will be ignored.\nYou can set the value to -1 to ignore the limit");

        ConfigurationSection section = this.getConfig().getConfigurationSection("enchantment_level_limits");
        if( section == null){
            section = this.getConfig().createSection("enchantment_level_limits");
        }

        section.addDefault("loyalty", 127);
        section.addDefault("luck_of_the_sea", 84);
        section.addDefault("lure", 5);
        section.addDefault("quick_charge", 5);

        getConfig().options().copyHeader(true);
        getConfig().options().copyDefaults(true);
        saveConfig();

        return section.getValues(false);
    }



}
