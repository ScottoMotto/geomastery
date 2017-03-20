package com.jayavery.jjmod.items;

import java.util.Set;
import com.google.common.collect.Sets;
import com.jayavery.jjmod.utilities.ToolType;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

/** Machete tool item. */
public class ItemMachete extends ItemToolAbstract {

    /** Set of vanilla blocks to harvest. */
    private static final Set<Block> EFFECTIVE_ON =
            Sets.newHashSet(new Block[] {Blocks.LEAVES, Blocks.LEAVES2});

    public ItemMachete(String name, ToolMaterial material) {

        super(1, -3.1F, material, EFFECTIVE_ON);
        ItemJj.setupItem(this, name, 1, CreativeTabs.TOOLS);
        this.setHarvestLevel(ToolType.MACHETE.toString(), 1);
    }
}