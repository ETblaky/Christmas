package me.etblaky;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongDestroyingEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;


/**
 * Created by ETblaky on 04/12/2016.
 */
public class ChristmasGUI implements Listener{

    public static Inventory mainInv = Bukkit.createInventory(null, 9, ChatColor.RED + "Change your config here:");
    public static Inventory songInv = Bukkit.createInventory(null, 9, ChatColor.RED + "Choose the song here:");

    public static void setUp(){
        ItemStack snow = new ItemStack(Material.SNOW_BALL);
        ItemMeta snowMeta = snow.getItemMeta();
        snowMeta.setDisplayName(ChatColor.YELLOW + "Toggle Snow");
        snow.setItemMeta(snowMeta);

        ItemStack song = new ItemStack(Material.RECORD_3);
        ItemMeta songMeta = song.getItemMeta();
        songMeta.setDisplayName(ChatColor.YELLOW + "Choose The Song");
        song.setItemMeta(songMeta);

        /* ##X###X##  */
        mainInv.setItem(2, snow);
        mainInv.setItem(6, song);

        ItemStack stop = new ItemStack(Material.RECORD_3);
        ItemMeta stopMeta = stop.getItemMeta();
        stopMeta.setDisplayName(ChatColor.RED + "Stop the song");
        stop.setItemMeta(stopMeta);

        songInv.setItem(0, stop);

        for(String s : Christmas.songs){
            ItemStack is = new ItemStack(Material.RECORD_3);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(s);
            is.setItemMeta(im);

            songInv.addItem(is);
        }

    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e){
        if (e.getInventory().getName().equals(mainInv.getName())) {
            if (e.getCurrentItem().getItemMeta() == null) return;

            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Toggle Snow")) {
                Christmas.isSnowing = !Christmas.isSnowing;
            }

            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Choose The Song")) {
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();

                e.getWhoClicked().openInventory(songInv);

                return;
            }

            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
        }

        if (e.getInventory().getName().equals(songInv.getName())) {
            if (e.getCurrentItem().getItemMeta() == null) return;

            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Stop the song")){
                Christmas.sp.destroy(SongDestroyingEvent.StopCause.MANUALLY_DESTROYED);

                e.setCancelled(true);
                e.getWhoClicked().closeInventory();

                return;
            }


            if(Christmas.sp != null) { Christmas.sp.destroy(SongDestroyingEvent.StopCause.MANUALLY_DESTROYED); }

            Christmas.sp = new RadioSongPlayer(NBSDecoder.parse(new File(Christmas.instance.getDataFolder(), e.getCurrentItem().getItemMeta().getDisplayName() + ".nbs")));
            Christmas.sp.setAutoDestroy(true);
            for(Player p : Bukkit.getOnlinePlayers()){  Christmas.sp.addPlayer(p); }
            Christmas.sp.setPlaying(true);

            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
        }
    }



}
