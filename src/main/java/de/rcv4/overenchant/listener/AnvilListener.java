package de.rcv4.overenchant.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class AnvilListener implements Listener {

    private final Map<Enchantment, Integer> maxLevels;
    private final Logger logger;

    public AnvilListener(Logger logger, Map<String, Object> enchantmentLevelLimits){
        this.logger = logger;
        this.maxLevels = parseMaxLevels(enchantmentLevelLimits);
    }

    private Map<Enchantment, Integer> parseMaxLevels(Map<String, Object> enchantmentLevelLimits){
        Map<Enchantment, Integer> result = new HashMap<>();
        enchantmentLevelLimits.forEach((key, level) -> {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));

            if( enchantment == null || !(level instanceof Integer levelInt)){
                logger.warning("Couldn't load key '"+ key +"'");
                return;
            }

            if (levelInt == -1 || enchantment.getMaxLevel() > levelInt) {
                return;
            }

            logger.info(enchantment.getKey().toString() + " - set maximum level to " + level);
            result.put(enchantment, levelInt);

        });
        return result;
    }

    @EventHandler
    public void anvilListener(PrepareAnvilEvent e){
        ItemStack is = e.getResult();
        if( is == null ){ return; }

        ItemStack itemStack1 = e.getInventory().getItem(0);
        ItemStack itemStack2 = e.getInventory().getItem(1);
        if( itemStack1 == null || itemStack2 == null ){ return; }

        Map<Enchantment, Integer> enchantments1 = getEnchantments(itemStack1);
        Map<Enchantment, Integer> enchantments2 = getEnchantments(itemStack2);
        Map<Enchantment, Integer> merged = getMergedEnchantments(enchantments1, enchantments2);

        for(var ench : getEnchantments(is).entrySet()){
            if(merged.containsKey(ench.getKey())){
                setEnchantment(is, ench.getKey(), merged.get(ench.getKey()));
            }
        }
    }

    private Map<Enchantment, Integer>getMergedEnchantments(Map<Enchantment, Integer>... itemEnchantments){
        Map<Enchantment, Integer> result = new HashMap<>();
        for (Map<Enchantment, Integer> enchantmentMap : itemEnchantments) {
            enchantmentMap.forEach(((enchantment, level) -> {

                int current = result.getOrDefault(enchantment, 0);
                result.put(enchantment, getUplevel(enchantment, level, current));

            }));
        }
        return result;
    }

    private void setEnchantment(ItemStack itemStack, Enchantment enchantment, int value){
        if(enchantment.getMaxLevel() == 1) return;
        if(itemStack.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta){
            enchantmentStorageMeta.removeStoredEnchant(enchantment);
            enchantmentStorageMeta.addStoredEnchant(enchantment, value, true);
            itemStack.setItemMeta(enchantmentStorageMeta);
            return;
        }
        itemStack.removeEnchantment(enchantment);
        itemStack.addUnsafeEnchantment(enchantment, value);
    }
    private Map<Enchantment, Integer> getEnchantments(ItemStack itemStack){
        if(itemStack.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta){
            return enchantmentStorageMeta.getStoredEnchants();
        }
        return itemStack.getEnchantments();
    }

    private int getUplevel(Enchantment enchantment, int level1, int level2) {
        int newLevel;
        if(level1 == level2) {
            newLevel = level1 + 1;
        } else {
            newLevel = Math.max( level1, level2 );
        }
        return maxLevels.getOrDefault( enchantment, Integer.MAX_VALUE ) < newLevel ? level1 : newLevel;
    }
}
