package me.etblaky;

import com.xxmicloxx.NoteBlockAPI.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by ETblaky on 03/12/2016.
 */
public class Christmas extends JavaPlugin implements Listener {

    public static Christmas instance;

    public static SongPlayer sp;

    public ArrayList<String> subCmds = new ArrayList<String>();
        public static ArrayList<String> songs = new ArrayList<String>();
        public static boolean isSnowing = false;

    public void onEnable(){

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new ChristmasGUI(), this);

        instance = this;

        subCmds.add("song");
        subCmds.add("head");
            songs.add("jingle_bell");
            songs.add("santa_town");
            songs.add("wish_merry");

        verifyConfigFolder();
        startSchedulers();

        ChristmasGUI.setUp();

    }

    public void startSchedulers(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(isSnowing){
                        for(int i = 0; i < 25; i++){
                            Random rand1 = new Random();
                            Random rand2 = new Random();
                            p.getWorld().spawnEntity(p.getLocation().add(rand1.nextInt((20 - -20) + 1) + -20, 25, rand2.nextInt((20 - -20) + 1) + -20), EntityType.SNOWBALL);
                        }
                    }
                }
            }
        }, 10, 10);
    }

    public void verifyConfigFolder(){
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                saveDefaultConfig();
            }

            for(String s : songs){
                if (!new File(getDataFolder(), s + ".nbs").exists()) {
                    saveResource(s + ".nbs", false);
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        if(cmd.getName().equalsIgnoreCase("christmas")){

            if(args.length < 1) { sender.sendMessage(Arrays.toString(subCmds.toArray()).replace("[", "").replace("]", "")); return true; }

            if(args[0].equalsIgnoreCase("head")){
                if(!sender.hasPermission("christmas.head")) { sender.sendMessage(ChatColor.RED + "Você não tem permissão para fazer isso!"); return true;}

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner("Santa");
                meta.setDisplayName(ChatColor.DARK_RED + "Christmas Time!");
                skull.setItemMeta(meta);
                ((Player) sender).getInventory().addItem(skull);
            }

            if(args[0].equalsIgnoreCase("song")){
                System.out.println(sender.hasPermission("christmas.song"));
                if(!sender.hasPermission("christmas.song")) { sender.sendMessage(ChatColor.RED + "Você não tem permissão para fazer isso!"); return true;}
                if(args.length < 2 || (!songs.contains(args[1]) && !args[1].equalsIgnoreCase("stop"))) { sender.sendMessage(Arrays.toString(songs.toArray()).replace("[", "").replace("]", "")); return true; }

                if(sp != null) { sp.destroy(SongDestroyingEvent.StopCause.MANUALLY_DESTROYED); }

                if(!args[1].equalsIgnoreCase("stop")) {
                    sp = new RadioSongPlayer(NBSDecoder.parse(new File(getDataFolder(), args[1] + ".nbs")));
                    sp.setAutoDestroy(true);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        sp.addPlayer(p);
                    }
                    sp.setPlaying(true);
                }

                if(args[1].equalsIgnoreCase("stop")){
                    if(sp == null) { sp = new RadioSongPlayer(NBSDecoder.parse(new File(getDataFolder(), "jingle_bell.nbs"))); }
                    sp.destroy(SongDestroyingEvent.StopCause.MANUALLY_DESTROYED);
                }

            }

            if(args[0].equalsIgnoreCase("snow")){
                if(!sender.hasPermission("christmas.snow")) { sender.sendMessage(ChatColor.RED + "Você não tem permissão para fazer isso!"); return true;}
                isSnowing = !isSnowing;
            }

        }

        return true;
    }

    @EventHandler
    public void onSongFinishes(final SongDestroyingEvent e){
        if(e.getStoppedCause().equals(SongDestroyingEvent.StopCause.MANUALLY_DESTROYED)) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                SongPlayer sp = new RadioSongPlayer(e.getSongPlayer().getSong());
                sp.setAutoDestroy(true);
                for(Player p : Bukkit.getOnlinePlayers()){  sp.addPlayer(p); }
                sp.setPlaying(true);
            }
        }, 5 * 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(this.getConfig().getBoolean("onlineOps") && !e.getPlayer().isOp()) return;

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("Santa");
        meta.setDisplayName(ChatColor.DARK_RED + "Christmas Time!");
        skull.setItemMeta(meta);
        e.getPlayer().getInventory().setItem(this.getConfig().getInt("Slot"), skull);
    }

    @EventHandler
    public void onClickSantasHead(PlayerInteractEvent e){
        if(!e.getPlayer().hasPermission("christmas.head")) { e.getPlayer().sendMessage(ChatColor.RED + "Você não tem permissão para fazer isso!"); return;}

        if(e.getPlayer().getInventory().getItemInMainHand().getType() != Material.SKULL_ITEM) return;
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) return;
        if(!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Christmas Time!")) return;

        e.getPlayer().openInventory(ChristmasGUI.mainInv);
        e.setCancelled(true);

    }

}
