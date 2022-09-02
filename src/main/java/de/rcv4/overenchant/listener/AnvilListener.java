package de.rcv4.overenchant.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AnvilListener implements Listener {

    @EventHandler
    public void anvilListener(PrepareAnvilEvent e){
        ItemStack is = e.getResult();
        if( is == null ){ return; }

        ItemStack itemStack1 = e.getInventory().getItem(0);
        ItemStack itemStack2 = e.getInventory().getItem(1);
        if( itemStack1 == null || itemStack2 == null ){ return; }

        Map<Enchantment, Integer> enchantments1 = getEnchantments(itemStack1);
        Map<Enchantment, Integer> enchantments2 = getEnchantments(itemStack2);
        Map<Enchantment, Integer> merged = Stream.of(enchantments1, enchantments2).flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::getUplevel));

        for(var ench : getEnchantments(is).entrySet()){
            if(merged.containsKey(ench.getKey())){
                setEnchantment(is, ench.getKey(), merged.get(ench.getKey()));
            }
        }
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

    private int getUplevel(int a, int b) {
        if(a == b) {
            return a + 1;
        }
        return Math.max(a, b);
    }
}
