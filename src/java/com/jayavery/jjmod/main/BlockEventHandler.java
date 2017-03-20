package com.jayavery.jjmod.main;

import com.jayavery.jjmod.blocks.BlockRock;
import com.jayavery.jjmod.entities.FallingTreeBlock;
import com.jayavery.jjmod.init.ModBlocks;
import com.jayavery.jjmod.init.ModItems;
import com.jayavery.jjmod.items.ItemAxe;
import com.jayavery.jjmod.items.ItemPickaxe;
import com.jayavery.jjmod.utilities.TreeFallUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Handler for block related events. */
public class BlockEventHandler {

    /** Alters equivalent to Block#neighborChanged behaviour
     * for vanilla blocks. */
    @SubscribeEvent
    public void notifyNeighbor(NeighborNotifyEvent event) {
        
        Block sourceBlock = event.getState().getBlock();
        World world = event.getWorld();
        BlockPos sourcePos = event.getPos();
        
        // Prevent Nether Portals from generating
        if (sourceBlock == Blocks.PORTAL) {

            world.setBlockToAir(sourcePos);
        }

        // Apply gravity
        for (EnumFacing facing : EnumFacing.VALUES) {
        
            BlockPos pos = sourcePos.offset(facing);
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            
            // Check for falling trees
            if (block instanceof BlockLog) {
                
                TreeFallUtils.checkTreeFall(world, pos);
                
            } else if (block instanceof BlockLeaves) {
                
                TreeFallUtils.checkLeavesFall(world, pos);
            }
            
            // Check for vertical-falling single blocks
            
            boolean shouldFall = false;
            IBlockState fallState = state;
            boolean airBelow = world.isAirBlock(pos.down());
            
            if (block instanceof BlockStone || block instanceof BlockRock ||
                    block instanceof BlockOre) {

                boolean airAround = false;
                
                for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                    
                    if (world.isAirBlock(pos.offset(direction))) {
                        
                        airAround = true;
                        break;
                    }
                }
                
                if (block instanceof BlockStone) {

                    EnumType type = state.getValue(BlockStone.VARIANT);
                    
                    if (type == EnumType.GRANITE) {

                        shouldFall = false;
                        
                    } else if (type == EnumType.ANDESITE) {

                        shouldFall = airAround && airBelow ?
                                world.rand.nextFloat() < 0.33 : airBelow ?
                                world.rand.nextFloat() < 0.04F : false;
                        
                    } else if (type == EnumType.DIORITE) {

                        shouldFall = airAround && airBelow ?
                                world.rand.nextFloat() < 0.25 : airBelow ?
                                world.rand.nextFloat() < 0.03F : false;
                        
                    } else {

                        shouldFall = airAround && airBelow ?
                                world.rand.nextFloat() < 0.17F : airBelow ?
                                world.rand.nextFloat() < 0.02F : false;
                    }
                    
                    fallState = ModBlocks.rubble.getDefaultState();
                    
                } else {

                    shouldFall = airAround && airBelow ?
                            world.rand.nextFloat()< 0.17F : airBelow ?
                            world.rand.nextFloat() < 0.02F : false;
                }
                    
            
            } else if (block == Blocks.DIRT || block == Blocks.GRASS ||
                    block == Blocks.CLAY) {

                shouldFall = airBelow;
                
            }
            
            if (shouldFall) {

                if (!BlockFalling.fallInstantly &&
                        world.isAreaLoaded(pos.add(-32, -32, -32),
                        pos.add(32, 32, 32))) {
                    
                    if (!world.isRemote) {

                        world.setBlockState(pos, fallState);
                        EntityFallingBlock falling =
                                new EntityFallingBlock(world,
                                pos.getX() + 0.5D, pos.getY(),
                                pos.getZ() + 0.5D, fallState);
                        world.spawnEntity(falling);
                    }
                    
                } else {

                    world.setBlockToAir(pos);
                    BlockPos checkPos;

                    for (checkPos = pos.down(); world.isAirBlock(checkPos) &&
                            checkPos.getY() > 0; checkPos = checkPos.down()) {
                        ;
                    }

                    if (checkPos.getY() > 0) {

                        world.setBlockState(checkPos.up(), fallState, 0);
                    }
                }
            }
        }
    }
    
    /** Adjusts block breaking speed. */
    @SubscribeEvent
    public void playerBreakSpeed(PlayerEvent.BreakSpeed event) {

        Block block = event.getState().getBlock();
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        String toolRequired = block.getHarvestTool(event.getState());
        
        // Make axe and pickaxe blocks unbreakable without tool
        if (("axe".equals(toolRequired) &&
                !(stack.getItem() instanceof ItemAxe)) ||
                ("pickaxe".equals(toolRequired) &&
                !(stack.getItem() instanceof ItemPickaxe))) {
            
            event.setCanceled(true);
        }
    }
    
    /** Reduces sugarcane growth rate to 0.4 of vanilla. */
    @SubscribeEvent
    public void cropGrow(CropGrowEvent.Pre event) {
        
        if (event.getState().getBlock() == Blocks.REEDS) {
            
            if (event.getWorld().rand.nextInt(5) < 3) {
                
                event.setResult(Result.DENY);
            }
        }
    }

    /** Alters equivalent to Block#getDrops for vanilla blocks. */
    @SubscribeEvent
    public void harvestDrops(HarvestDropsEvent event) {

        World world = event.getWorld();
        Block block = event.getState().getBlock();

        if (block instanceof BlockLeaves) {

            for (int i = 0; i < event.getDrops().size(); i++) {
                
                if (event.getDrops().get(i).getItem() == Items.APPLE) {
                    
                    event.getDrops().remove(i);
                }
            }
            
            if (world.rand.nextInt(4) == 0) {
            
                event.getDrops().add(new ItemStack(ModItems.leaves));
            }

            if (world.rand.nextInt(4) == 0) {

                event.getDrops().add(new ItemStack(Items.STICK));
            }
        }
        
        if (block == Blocks.TALLGRASS || block == Blocks.DOUBLE_PLANT) {

            event.getDrops().replaceAll((stack) -> ItemStack.EMPTY);
        }

        if (block instanceof BlockLog) {

            event.getDrops().clear();
            int rand = world.rand.nextInt(6);
            event.getDrops().add(new ItemStack(rand == 0 ?
                    ModItems.thicklog : rand == 1 ?
                    ModItems.log : ModItems.pole));
        }

        if (block instanceof BlockDirt || block instanceof BlockGrass) {

            event.getDrops().clear();
            
            if (world.rand.nextInt(8) == 0) {

                event.getDrops().add(new ItemStack(Items.FLINT));
            }

            if (world.rand.nextInt(8) == 0) {

                event.getDrops().add(new ItemStack(ModItems.dirt));
            }
        }
        
        if (block == Blocks.REDSTONE_ORE || block == Blocks.LIT_REDSTONE_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.REDSTONE,
                    world.rand.nextInt(4) + 1));
        }
        
        if (block == Blocks.LAPIS_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.DYE,
                    world.rand.nextInt(5) + 1,
                    EnumDyeColor.BLUE.getMetadata()));
        }
        
        if (block == Blocks.DIAMOND_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.DIAMOND));
        }
        
        if (block == Blocks.GOLD_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(ModItems.oreGold));
        }
        
        if (block == Blocks.EMERALD_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.EMERALD));
        }
        
        if (block == Blocks.IRON_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(ModItems.oreIron));
        }

        if (block instanceof BlockStone) {

            event.getDrops().clear();
            event.getDrops().add(new ItemStack(ModItems.stoneRough, 4));
        }
        
        if (block == Blocks.SAND) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(ModItems.sand, 4));
        }
        
        if (block == Blocks.GRAVEL) {
            
            event.getDrops().clear();
            
            if (world.rand.nextInt(4) == 0) {
            
                event.getDrops().add(new ItemStack(Items.FLINT));
            }
        }
        
        if (block == Blocks.CLAY) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.CLAY_BALL));
        }
    }
    
    /** Fells trees according to player facing. */
    @SubscribeEvent
    public void breakBlock(BreakEvent event) {
        
        IBlockState state = event.getState();
        Block block = state.getBlock();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        
        if (block instanceof BlockLog) {

            TreeFallUtils.fellTree(world, pos, event.getPlayer()
                    .getHorizontalFacing().rotateY());
        }
    }    
}