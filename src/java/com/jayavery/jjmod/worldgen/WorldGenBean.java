package com.jayavery.jjmod.worldgen;

import java.util.Random;
import com.jayavery.jjmod.init.ModBlocks;
import net.minecraft.world.World;

/** WorldGenerator for bean blocks. */
public class WorldGenBean extends WorldGenCrop {

    public WorldGenBean(World world, Random rand) {
        
        super(world, rand, ModBlocks.bean.getFullgrown(), 30, 5);
    }
}