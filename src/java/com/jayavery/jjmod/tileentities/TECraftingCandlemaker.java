package com.jayavery.jjmod.tileentities;

import com.jayavery.jjmod.blocks.BlockNew;
import com.jayavery.jjmod.init.ModBlocks;
import com.jayavery.jjmod.init.ModItems;
import com.jayavery.jjmod.tileentities.TECraftingCandlemaker.EnumPartCandlemaker;
import com.jayavery.jjmod.utilities.IMultipart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TECraftingCandlemaker extends
        TECraftingAbstract<EnumPartCandlemaker> {
    
    @Override
    protected EnumPartCandlemaker partByOrdinal(int ordinal) {
        
        return EnumPartCandlemaker.values()[ordinal];
    }
    
    /** Enum defining parts of the whole Candlemaker structure. */
    public enum EnumPartCandlemaker implements IMultipart {

        FRONT("front"), BACK("back");

        private final String name;

        private EnumPartCandlemaker(String name) {

            this.name = name;
        }

        @Override
        public String getName() {

            return this.name;
        }
        
        @Override
        public ItemStack getDrop() {
            
            if (this == FRONT) {
                
                return new ItemStack(ModItems.craftingCandlemaker);
                
            } else {
                
                return ItemStack.EMPTY;
            }
        }
        
        @Override
        public BlockPos getMaster(BlockPos pos, EnumFacing facing) {
            
            if (this == FRONT) {
                
                return pos;
                
            } else {
                
                return pos.offset(facing.getOpposite());
            }
        }
        
        @Override
        public boolean shouldBreak(World world, BlockPos pos,
                EnumFacing facing) {
            
            boolean broken = false;
            Block block = ModBlocks.craftingCandlemaker;
            
            if (this == FRONT) {

                broken = world.getBlockState(pos.offset(facing)).getBlock()
                        != block;

            } else {

                broken = world.getBlockState(pos.offset(facing.getOpposite()))
                        .getBlock() != block;
            }
            
            return broken;
        }
        
        @Override
        public AxisAlignedBB getBoundingBox(EnumFacing facing) {
            
            return this == BACK ? BlockNew.TWELVE : BlockNew.CENTRE_FOUR;
        }
        
        @Override
        public AxisAlignedBB getCollisionBox(EnumFacing facing) {
            
            return this == BACK ? BlockNew.TWELVE : Block.NULL_AABB;
        }
        
        @Override
        public boolean buildStructure(World world, BlockPos pos,
                EnumFacing facing) {

            if (this == FRONT) {
                
                BlockPos posFront = pos;
                BlockPos posBack = posFront.offset(facing);

                // Check replaceable
                IBlockState stateFront = world.getBlockState(posFront);
                Block blockFront = stateFront.getBlock();
                boolean replaceableFront = blockFront
                        .isReplaceable(world, posFront);

                IBlockState stateBack = world.getBlockState(posBack);
                Block blockBack = stateBack.getBlock();
                boolean replaceableBack = blockBack
                        .isReplaceable(world, posBack);

                if (replaceableBack && replaceableFront) {

                    // Place all
                    IBlockState placeState = ModBlocks
                            .craftingCandlemaker.getDefaultState();
                    
                    world.setBlockState(posBack, placeState);
                    world.setBlockState(posFront, placeState);
                    
                    // Set up tileentities
                    ((TECraftingCandlemaker) world.getTileEntity(posBack))
                            .setState(facing, BACK);
                    ((TECraftingCandlemaker) world.getTileEntity(posFront))
                            .setState(facing, FRONT);

                    return true;
                }
            }
            
            return false;
        }
    }
}