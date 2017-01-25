package com.jj.jjmod.blocks;

import com.jj.jjmod.init.ModItems;
import com.jj.jjmod.utilities.ToolType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.BiomeSavanna;

public class BlockCropBlockfruitMelon extends BlockCropBlockfruit {
    
    public BlockCropBlockfruitMelon() {
        
        super("melon", 0.4F, 0.2F, ToolType.SICKLE, Blocks.MELON_BLOCK);
    }

    @Override
    public boolean isPermitted(Biome biome) {

        return biome instanceof BiomePlains || biome == Biomes.BEACH || biome instanceof BiomeJungle || biome instanceof BiomeSavanna;
    }

}
