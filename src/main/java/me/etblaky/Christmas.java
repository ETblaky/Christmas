package me.etblaky;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ETblaky on 03/12/2016.
 */
public class Christmas extends JavaPlugin{

    public ArrayList<String> subCmds = new ArrayList<String>();
        public ArrayList<String> songs = new ArrayList<String>();
        public HashMap<Player, Boolean> isSnowing = new HashMap<Player, Boolean>();

    public void onEnable(){
        subCmds.add("song");
            songs.add("jingle_bell");
            songs.add("santa_town");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(isSnowing.get(p) != null &&isSnowing.get(p)){
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        if(cmd.getName().equalsIgnoreCase("christmas")){

            if(args.length < 1) { sender.sendMessage(Arrays.toString(subCmds.toArray()).replace("[", "").replace("]", "")); return true; }

            if(args[0].equalsIgnoreCase("song")){
                if(args.length < 2) { sender.sendMessage(Arrays.toString(songs.toArray()).replace("[", "").replace("]", "")); return true; }

                if(args[1].equalsIgnoreCase("jingle_bell")){
                    SongPlayer sp = new RadioSongPlayer(NBSDecoder.parse(new File(getDataFolder(), "jingle_bell.nbs")));
                    sp.setAutoDestroy(true);
                    for(Player p : Bukkit.getOnlinePlayers()){  sp.addPlayer(p); }
                    sp.setPlaying(true);
                }

                if(args[1].equalsIgnoreCase("santa_town")){
                    SongPlayer sp = new RadioSongPlayer(NBSDecoder.parse(new File(getDataFolder(), "santaTown.nbs")));
                    sp.setAutoDestroy(true);
                    for(Player p : Bukkit.getOnlinePlayers()){  sp.addPlayer(p); }
                    sp.setPlaying(true);
                }

            }

            if(args[0].equalsIgnoreCase("snow")){
                isSnowing.put((Player) sender, isSnowing.get(sender) == null || !isSnowing.get(sender));
            }

        }

        return true;
    }

    @EventHandler
    public void onSongFinishes(final SongDestroyingEvent e){
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                e.getSongPlayer().setPlaying(true);
            }
        }, 5 * 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        isSnowing.put(e.getPlayer(), false);
    }

}
