import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;

interface BlockOperator {
    void operate(Block block);
}

public class GameArea {

    private final World world;
    private final Vector loc_s;
    private final Vector loc_l;

    private final Material ALIVE = REDSTONE_BLOCK;
    private final Material DEAD = AIR;
    GameArea(World world, Vector loc1, Vector loc2) {
        this.world = world;
        this.loc_s = new Vector(
                Math.min(loc1.getBlockX(), loc2.getBlockX()),
                Math.min(loc1.getBlockY(), loc2.getBlockY()),
                Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
        this.loc_l = new Vector(
                Math.max(loc1.getBlockX(), loc2.getBlockX()),
                Math.max(loc1.getBlockY(), loc2.getBlockY()),
                Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
    }

    private void operateOnBlocks(BlockOperator operator) {
        for (int x = loc_s.getBlockX(); x <= loc_l.getBlockX(); ++x) {
            for (int z = loc_s.getBlockZ(); z <= loc_l.getBlockZ(); ++z){
                operator.operate(world.getBlockAt(x, loc_s.getBlockY(), z));
            }
        }
    }

    public void setAllDead(){
        operateOnBlocks((block)->{
            block.setType(DEAD);
        });
    }

    private Material getNextState(Location loc){
        int num_dead = 0;
        int num_alive = 0;
        for (int x = loc.getBlockX() - 1 ; x <= loc.getBlockX() + 1 ; ++x){
            for (int z = loc.getBlockZ() - 1 ; z <= loc.getBlockZ() + 1 ; ++z){
                Material type = world.getBlockAt(x, loc.getBlockY(), z).getType();
                if (x != loc.getBlockX() || z != loc.getBlockZ()){
                    if (type == ALIVE)
                        ++num_alive;
                    else
                        ++num_dead;
                }
            }
        }

        Material self = world.getBlockAt(loc).getType();

        if (self == ALIVE && num_alive >= 2 && num_alive <= 3)
            return ALIVE;
        else if (self == DEAD && num_alive == 3)
            return ALIVE;
        else
            return DEAD;
    }

    public HashMap<Block, Material> getNextStates(){
        HashMap<Block, Material> states = new HashMap<>();
        operateOnBlocks((block)->{
            states.putIfAbsent(block, getNextState(new Location(world, block.getX(), block.getY(), block.getZ())));
        });
        return states;
    }

    public void setNextStates(Map<Block, Material> states){
        for (Map.Entry<Block, Material> pair : states.entrySet()){
            pair.getKey().setType(pair.getValue());
        }
    }
}