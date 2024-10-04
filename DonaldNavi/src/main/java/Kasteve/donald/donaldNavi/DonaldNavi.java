package Kasteve.donald.donaldNavi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class DonaldNavi extends JavaPlugin {

    public FileConfiguration config=getConfig();
    public Objective objective;
    public int linesnum;

    public String colorCode;
    public int memusage;

    @Override
    public void onEnable() {
       saveDefaultConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(this, 0, 10); // 1 tickごとに更新
    }
    private void updateScoreboard(Player player) {

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String title=getConfig().getString("title","DonaldNavi1.0Plugin");
        objective = scoreboard.registerNewObjective("sidebar", "dummy", ChatColor.translateAlternateColorCodes('%', title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        ReplaceAndShow(getlists(),player);
        player.setScoreboard(scoreboard);
    }

  public ArrayList<String> getlists(){
      List<String> listconf = config.getStringList("Lists");
      ArrayList<String> allaya = new ArrayList<>(listconf);
      linesnum=allaya.size();
      return allaya;
  }

  public void createBar(String Contents, int Lines){
        String replaced=ChatColor.translateAlternateColorCodes('%',Contents);
        Score score = objective.getScore(replaced);
        score.setScore(Lines+1);
  }

    public void ReplaceAndShow(List<String> STlist, Player player) {
        long usedRam = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long totalRam = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        double ur = (double) usedRam;
        double tr = (double) totalRam;
        double katei = (ur / tr) * 100;
        memusage = (int) katei;
        getMEMCC();
        for (int i = 0; i < linesnum; i++) {

            String thisline = STlist.get(i);
            int minusline = linesnum - i - 1;

            if (thisline.contains("[Location]")) {
                Location loc = player.getLocation();
                thisline = thisline.replace("[Location]", loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                createBar(thisline, minusline);
            } else if (thisline.contains("[Realtime]")) {
                int H = LocalTime.now().getHour();
                int M = LocalTime.now().getMinute();
                int S = LocalTime.now().getSecond();
                String time = H + ":" + M + ":" + S;
                thisline = thisline.replace("[Realtime]", time);
                createBar(thisline, minusline);
            } else if (thisline.contains("[Server-name]")) {
                thisline = thisline.replace("[Server-name]", getConfig().getString("Server.name", "unknown"));
                createBar(thisline, minusline);
            } else if (thisline.contains("[MEM-info]")) {
                thisline = thisline.replace("[MEM-info]", colorCode + usedRam + "MB / " + totalRam + "MB");
                createBar(thisline, minusline);
            } else if (thisline.contains("[MEM-Usage]")) {
                thisline = thisline.replace("[MEM-Usage]", colorCode+"Memory: "+ memusage + "%");
                createBar(thisline, minusline);
            } else if (thisline.contains("[PING]")) {
                thisline = thisline.replace("[PING]", "" + player.getPing());
                createBar(thisline, minusline);
            } else if (thisline.contains("[Player-info]")) {
                thisline = thisline.replace("[Player-info]", Bukkit.getOnlinePlayers().size()+" / "+ Bukkit.getMaxPlayers());
                createBar(thisline, minusline);
            }
        }
    }
    public void getMEMCC(){
        if (memusage >= 0 && memusage <= 40) {
            colorCode = "%b";
        } else if (memusage >= 41 && memusage <= 69) {
            colorCode = "%e";
        } else if (memusage >= 70) {
            colorCode = "%c";
        }

    }
    @EventHandler
    public void Playerj(PlayerJoinEvent event){
        reloadConfig();
    }
}