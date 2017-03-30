package com.jayavery.jjmod.blocks;

import com.jayavery.jjmod.init.ModItems;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomePlains;

/** Tomato crop block. */
public class BlockCropHarvestableTomato extends BlockCropHarvestableAbstract {
    
    public BlockCropHarvestableTomato() {
        
        super("tomato", 2, () -> ModItems.tomato, (rand) -> 1, 0.4F, 0.2F);
    }

    @Override
    public boolean isPermitted(Biome biome) {

        return biome instanceof BiomeForest || biome == Biomes.RIVER ||
                biome instanceof BiomePlains || biome == Biomes.BEACH ||
                biome instanceof BiomeJungle;
    }
}
