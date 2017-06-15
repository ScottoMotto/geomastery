/*******************************************************************************
 * Copyright (C) 2017 Jay Avery
 * 
 * This file is part of Geomastery. Geomastery is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.geomastery.worldgen;

import java.util.Random;
import jayavery.geomastery.main.GeoBlocks;
import net.minecraft.world.World;

/** WorldGenerator for potato crops. */
public class WorldGenPotato extends WorldGenCrop {

    public WorldGenPotato(World world, Random rand) {
        
        super(world, rand, GeoBlocks.POTATO.getFullgrown(), 20, 5);
    }
}
