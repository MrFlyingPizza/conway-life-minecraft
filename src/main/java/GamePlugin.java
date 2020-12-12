import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class GamePlugin extends JavaPlugin {

    Location loc1 , loc2;
    GameArea gameArea;
    boolean running = false;

    @Override
    public void onEnable() {
        super.onEnable();



        Objects.requireNonNull(getCommand("setpos")).setExecutor((commandSender, command, s, strings) -> {
            if (commandSender instanceof Player){
                if (Integer.parseInt(strings[0]) == 1)
                    loc1 = ((Player) commandSender).getLocation();
                else if (Integer.parseInt(strings[0]) == 2)
                    loc2 = ((Player) commandSender).getLocation();
                return true;
            }
            return false;
        });

        Objects.requireNonNull(getCommand("startconway")).setExecutor((commandSender, command, s, strings) -> {
            if (running) return false;
            int delay = Integer.parseInt(strings[0]);
            gameArea = new GameArea(loc1.getWorld(), loc1.toVector(), loc2.toVector());
            //gameArea.setAllDead();
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, ()->{
                gameArea.setNextStates(gameArea.getNextStates());
            }, 0, delay);
            running = true;
            return true;
        });

        Objects.requireNonNull(getCommand("endconway")).setExecutor((commandSender, command, s, strings) -> {
            if (!running) return false;
            Bukkit.getScheduler().cancelTasks(this);
            running = false;
            return true;
        });
    }
}