package com.jj.jjmod.main;

import java.util.Arrays;
import com.jj.jjmod.blocks.BlockBedAbstract;
import com.jj.jjmod.blocks.BlockBedAbstract.EnumPartBed;
import com.jj.jjmod.blocks.BlockBedBreakable;
import com.jj.jjmod.blocks.BlockCarcass;
import com.jj.jjmod.blocks.BlockRock;
import com.jj.jjmod.capabilities.CapDecay;
import com.jj.jjmod.capabilities.CapFoodstats;
import com.jj.jjmod.capabilities.CapInventory;
import com.jj.jjmod.capabilities.CapTemperature;
import com.jj.jjmod.capabilities.DefaultCapFoodstats;
import com.jj.jjmod.capabilities.DefaultCapInventory;
import com.jj.jjmod.capabilities.DefaultCapTemperature;
import com.jj.jjmod.capabilities.ProviderCapFoodstats;
import com.jj.jjmod.capabilities.ProviderCapInventory;
import com.jj.jjmod.capabilities.ProviderCapTemperature;
import com.jj.jjmod.container.ContainerInventory;
import com.jj.jjmod.init.ModBlocks;
import com.jj.jjmod.init.ModItems;
import com.jj.jjmod.items.ItemAxe;
import com.jj.jjmod.items.ItemEdibleDecayable;
import com.jj.jjmod.items.ItemHoe;
import com.jj.jjmod.items.ItemHuntingknife;
import com.jj.jjmod.items.ItemPickaxe;
import com.jj.jjmod.items.ItemShield;
import com.jj.jjmod.tileentities.TEBed;
import com.jj.jjmod.utilities.FoodStatsPartial;
import com.jj.jjmod.utilities.FoodStatsWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EventHandler {
    
    /** ------------------------ GUI & INPUT EVENTS ------------------------ */
    
    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
        
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        
        if (event.getType() == ElementType.HOTBAR) {
        
            EnumHandSide hand = player.getPrimaryHand();
            ResourceLocation icon = player.getCapability(CapTemperature
                    .CAP_TEMPERATURE, null).getIcon();
            
            int width = event.getResolution().getScaledWidth() / 2;
            width = hand == EnumHandSide.LEFT ? width - 114 : width + 96;
            int height = event.getResolution().getScaledHeight() - 21;
            
            mc.getTextureManager().bindTexture(icon);
            Gui.drawModalRectWithCustomSizedTexture(width, height,
                    0, 0, 18, 18, 18, 18);
        }
        
        if (event.getType() == ElementType.FOOD) {

            GlStateManager.enableBlend();
            int left = event.getResolution().getScaledWidth() / 2 + 91;
            int top = event.getResolution().getScaledHeight() - 39;
            Gui gui = new Gui();
            Minecraft.getMinecraft().getTextureManager()
            .bindTexture(new ResourceLocation("textures/gui/icons.png"));
            DefaultCapFoodstats food = (DefaultCapFoodstats) player
                    .getCapability(CapFoodstats.CAP_FOODSTATS, null);
            
            FoodStatsPartial carbs = food.carbs;
            int carbsHunger = carbs.getFoodLevel();
            
            for (int i = 0; i < 10; i++) {
                
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                
                gui.drawTexturedModalRect(x, y, 16, 27, 9, 9);
                
                if (idx < carbsHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                    
                } else if (idx == carbsHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                }
            }
            
            FoodStatsPartial fruitveg = food.fruitveg;
            int fruitvegHunger = fruitveg.getFoodLevel();
            top -= 10;
            
            for (int i = 0; i < 10; i++) {
                
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                
                gui.drawTexturedModalRect(x, y, 16, 27, 9, 9);
                
                if (idx < fruitvegHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                    
                } else if (idx == fruitvegHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                }
            }
            
            FoodStatsPartial protein = food.protein;
            int proteinHunger = protein.getFoodLevel();
            top -= 10;
            
            for (int i = 0; i < 10; i++) {
                
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                
                gui.drawTexturedModalRect(x, y, 16, 27, 9, 9);
                
                if (idx < proteinHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                    
                } else if (idx == proteinHunger) {
                    
                    gui.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                }
            }
            
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void guiOpen(GuiOpenEvent event) {

        EntityPlayer player = Minecraft.getMinecraft().player;

        if (event.getGui() instanceof
                net.minecraft.client.gui.inventory.GuiInventory &&
                player.inventoryContainer instanceof ContainerInventory) {

            event.setGui(new com.jj.jjmod.gui
                    .GuiInventory((ContainerInventory)
                    player.inventoryContainer));
        }
    }
    
    @SubscribeEvent
    public void keyInput(KeyInputEvent event) {

        Minecraft mc = Minecraft.getMinecraft();
        
        if (!mc.player.isSpectator() &&
                !mc.player.capabilities.isCreativeMode &&
                mc.gameSettings.keyBindSwapHands.isPressed()) {
           
            ContainerInventory inv =
                    (ContainerInventory) mc.player.inventoryContainer;
            
            if (mc.player.inventory.offHandInventory.get(0) != null &&
                    ModBlocks.OFFHAND_ONLY.contains(mc.player.inventory
                    .offHandInventory.get(0).getItem())) {
                
                return;
            }

            inv.swapHands();
            inv.sendUpdateOffhand();
        }
    }
    
    /** -------------------------- BLOCK EVENTS ---------------------------- */
    
    @SubscribeEvent
    public void notifyNeighbor(NeighborNotifyEvent event) {
        
        Block sourceBlock = event.getState().getBlock();
        World world = event.getWorld();
        BlockPos sourcePos = event.getPos();
        
        if (sourceBlock == Blocks.PORTAL) {

            world.setBlockToAir(sourcePos);
        }

        for (EnumFacing facing : EnumFacing.VALUES) {
        
            BlockPos pos = sourcePos.offset(facing);
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            
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
                    
            
            } else if (block == Blocks.DIRT || block == Blocks.GRASS) {

                shouldFall = airBelow;
            }
            
            if (shouldFall) {

                if (!BlockFalling.fallInstantly &&
                        world.isAreaLoaded(pos.add(-32, -32, -32),
                        pos.add(32, 32, 32))) {
                    
                    if (!world.isRemote) {

                        world.setBlockState(pos, fallState);
                        EntityFallingBlock falling =
                                new EntityFallingBlock(world, pos.getX() + 0.5D,
                                pos.getY(), pos.getZ() + 0.5D,
                                fallState);
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
    
    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        
        Block block = event.getState().getBlock();
        EntityPlayer player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();
        
        if ((block == Blocks.DIRT || block == Blocks.GRASS) &&
                stack != null && stack.getItem() instanceof ItemHoe) {
            
            event.getWorld().setBlockState(event.getPos(),
                    Blocks.FARMLAND.getDefaultState());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void harvestDrops(HarvestDropsEvent event) {

        World world = event.getWorld();
        Block block = event.getState().getBlock();
        ItemStack stack = event.getHarvester() == null ? ItemStack.EMPTY :
                event.getHarvester().getHeldItemMainhand();
                
        if (block instanceof BlockCarcass) {

            BlockCarcass carcass = (BlockCarcass) block;

            if (!(stack.getItem() instanceof ItemHuntingknife)) {

                // No change to drops

            } else {

                event.getDrops().clear();
                event.getDrops().addAll(Arrays.asList(carcass.drops));
            }

            return;
        }   

        // CONFIG vanilla block harvest drops

        if (block instanceof BlockLeaves) {

            event.getDrops().add(new ItemStack(ModItems.leaves));

            if (world.rand.nextInt(8) == 0) {

                event.getDrops().add(new ItemStack(Items.STICK));
            }
        }
        
        if (block == Blocks.TALLGRASS || block == Blocks.DOUBLE_PLANT) {

            event.getDrops().replaceAll((Object) -> ItemStack.EMPTY);
        }

        if (block instanceof BlockLog) {

            event.getDrops().clear();
            int rand = world.rand.nextInt(3);

            switch (rand) {

                case 0: {
                    
                    event.getDrops().add(new ItemStack(ModItems.log));
                    break;
                }
                
                case 1: {
                    
                    event.getDrops().add(new ItemStack(ModItems.pole));
                    break;
                }
                
                case 2: {
                    
                    event.getDrops().add(new ItemStack(ModItems.thicklog));
                    break;
                }
            }
        }

        if (block instanceof BlockDirt || block instanceof BlockGrass) {

            event.getDrops().clear();
            
            if (world.rand.nextInt(10) == 0) {

                event.getDrops().add(new ItemStack(Items.FLINT));
            }

            event.getDrops().add(new ItemStack(ModItems.dirt, 4));
        }
        
        if (block == Blocks.REDSTONE_ORE || block == Blocks.LIT_REDSTONE_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.REDSTONE, world.rand.nextInt(4) + 1));
        }
        
        if (block == Blocks.LAPIS_ORE) {
            
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.DYE, world.rand.nextInt(5) + 1, EnumDyeColor.BLUE.getMetadata()));
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
            event.getDrops().add(new ItemStack(Items.FLINT));
        }
    }
    
    @SubscribeEvent
    public void cropGrow(CropGrowEvent.Pre event) {
        
        if (event.getState().getBlock() == Blocks.REEDS) {
            
            if (event.getWorld().rand.nextFloat() > 0.4) {
                
                event.setResult(Result.DENY);
            }
        }
    }
    
    @SubscribeEvent
    public void playerBreakSpeed(PlayerEvent.BreakSpeed event) {

        Block block = event.getState().getBlock();
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        String toolRequired = block.getHarvestTool(event.getState());
        
        if (((toolRequired != null && toolRequired.equals("axe") &&
                (stack == null || !(stack.getItem() instanceof ItemAxe))) ||
                (toolRequired != null && toolRequired.equals("pickaxe") &&
                (stack == null || !(stack.getItem() instanceof ItemPickaxe))))) {
            
            event.setCanceled(true);
        }
    }
    
    /** --------------------------- PLAYER EVENTS -------------------------- */
    
    @SubscribeEvent
    public void playerJoin(EntityJoinWorldEvent event) {
       
        
        if (event.getEntity() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) event.getEntity();
            
            player.getCapability(CapTemperature.CAP_TEMPERATURE, null)
                    .sendMessage();
            player.getCapability(CapFoodstats.CAP_FOODSTATS, null)
                    .sendMessage();
        }
    }
    
    @SubscribeEvent
    public void playerCapabilities(AttachCapabilitiesEvent<Entity> event) {

        if (!(event.getObject() instanceof EntityPlayer)) {

            return;
        }

        EntityPlayer player = (EntityPlayer) event.getObject();
        
        if (!(player.hasCapability(CapInventory.CAP_INVENTORY, null))) {

            event.addCapability(CapInventory.ID,
                    new ProviderCapInventory(new DefaultCapInventory(player)));
        }
        
        if (!(player.hasCapability(CapTemperature.CAP_TEMPERATURE, null))) {
            
            event.addCapability(CapTemperature.ID,
                    new ProviderCapTemperature(new
                    DefaultCapTemperature(player)));
        }
        
        if (!(player.hasCapability(CapFoodstats.CAP_FOODSTATS, null))) {
            
            event.addCapability(CapFoodstats.ID, new ProviderCapFoodstats(new
                    DefaultCapFoodstats(player)));
        }
    }

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        
        if (event.phase == Phase.START) {
            
            return;
        }

        EntityPlayer player = event.player;        
        player.getCapability(CapTemperature.CAP_TEMPERATURE, null).update();
        player.getCapability(CapInventory.CAP_INVENTORY, null).update();
                
        if (player.inventoryContainer instanceof ContainerPlayer &&
                !player.capabilities.isCreativeMode) {
            
            player.inventoryContainer =
                    new ContainerInventory(player, player.world);
            player.openContainer = player.inventoryContainer;
            
        } else if (player.inventoryContainer instanceof ContainerInventory &&
                player.capabilities.isCreativeMode) {
            
            player.inventoryContainer = new ContainerPlayer(player.inventory,
                    !player.world.isRemote, player);
            player.openContainer = player.inventoryContainer;

        }
        
        if (player.inventoryContainer instanceof ContainerInventory) {
            
            for (Slot slot : player.inventoryContainer.inventorySlots) {
                
                ItemStack stack = slot.getStack();
                
                if (stack.getItem() instanceof ItemEdibleDecayable) {
                    
                    if (stack.getCapability(CapDecay.CAP_DECAY, null).updateAndRot()) {
                        
                        slot.putStack(new ItemStack(ModItems.rot));
                    }
                }
            }
        }

        if (!(player.getFoodStats() instanceof FoodStatsWrapper)) {
            
            ReflectionHelper.setPrivateValue(EntityPlayer.class, player,
                    new FoodStatsWrapper(player), "foodStats");
        }
    }
    
    @SubscribeEvent
    public void playerItemPickup(EntityItemPickupEvent event) {
        
        EntityPlayer player = event.getEntityPlayer();
        
        if (player.capabilities.isCreativeMode) {
            
            return;
        }
        
        ItemStack stack = event.getItem().getEntityItem();
        Item item = stack.getItem();
        ItemStack remaining = stack;
        
        remaining = ((ContainerInventory) player.inventoryContainer).add(remaining);

        if (remaining.isEmpty()) {

            event.getItem().setDead();

        } else {
            
            event.getItem().setEntityItemStack(remaining);
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void itemToss(ItemTossEvent event) {
        
        if (!event.getPlayer().capabilities.isCreativeMode) {

            ((ContainerInventory) event.getPlayer().inventoryContainer)
                    .sendUpdateHighlight();
        }
    }

    @SubscribeEvent
    public void playerWakeUp(PlayerWakeUpEvent event) {

        BlockPos pos = new BlockPos(event.getEntityPlayer());
        World world = event.getEntityPlayer().world;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!(block instanceof BlockBedAbstract)) {

            return;
        }

        EnumPartBed part = state.getValue(BlockBedAbstract.PART);
        EnumFacing facing = state.getValue(BlockHorizontal.FACING);

        BlockPos posFoot;
        BlockPos posHead;

        if (part == EnumPartBed.FOOT) {

            posFoot = pos;
            posHead = pos.offset(facing);

        } else {

            posHead = pos;
            posFoot = pos.offset(facing.getOpposite());
        }

        if (block == ModBlocks.bedLeaf) {

            world.setBlockToAir(posFoot);
            world.setBlockToAir(posHead);
        }

        if (block instanceof BlockBedBreakable) {

            TileEntity tileEntity = world.getTileEntity(posFoot);

            if (tileEntity instanceof TEBed) {

                TEBed tileBed = (TEBed) tileEntity;
                tileBed.addUse();

                if (tileBed.isBroken()) {

                    world.setBlockToAir(posFoot);
                    world.setBlockToAir(posHead);
                    world.removeTileEntity(posFoot);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void playerAttacked(LivingAttackEvent event) {
        
        if (!(event.getEntity() instanceof EntityPlayer)) {
            
            return;
        }
        
        EntityPlayer player = (EntityPlayer) event.getEntity();
        DamageSource source = event.getSource();
        
        if (!source.isUnblockable() && player.isActiveItemStackBlocking()) {
            
            System.out.println("can block");
            Vec3d sourceVec = source.getDamageLocation();

            if (sourceVec != null) {
                
                Vec3d playerVec = player.getLook(1.0F);
                Vec3d attackVec = sourceVec.subtractReverse(new
                        Vec3d(player.posX, player.posY,
                        player.posZ)).normalize();
                attackVec = new Vec3d(attackVec.xCoord,
                        0.0D, attackVec.zCoord);

                if (attackVec.dotProduct(playerVec) < 0.0D &&
                        event.getAmount() >= 3 &&
                        player.getActiveItemStack().getItem()
                        instanceof ItemShield) {
                    
                    EnumHand hand = player.getActiveHand();
                    player.getActiveItemStack().damageItem(1 +
                            MathHelper.floor(event.getAmount()), player);
                    
                    if (player.getActiveItemStack().isEmpty()) {
                        
                        if (hand == EnumHand.MAIN_HAND) {
                            
                            player.setItemStackToSlot(
                                    EntityEquipmentSlot.MAINHAND,
                                    ItemStack.EMPTY);
                            
                        } else {
                            
                            player.setItemStackToSlot(
                                    EntityEquipmentSlot.OFFHAND,
                                    ItemStack.EMPTY);
                        }
                    }
                    
                    if (hand == EnumHand.MAIN_HAND) {
                        
                        ((ContainerInventory) player.inventoryContainer)
                                .sendUpdateHighlight();
                    
                    } else {
                        
                        ((ContainerInventory) player.inventoryContainer)
                            .sendUpdateOffhand();
                    }
                }
            }
        }
    }
    
    /** ----------------------- GENERAL ENTITY EVENTS ---------------------- */

    @SubscribeEvent
    public void livingDrops(LivingDropsEvent event) {

        Entity entity = event.getEntity();

        if (entity.world.isRemote) {

            return;
        }

        if (entity instanceof EntityPig) {

            event.getDrops().clear();
            entity.entityDropItem(new ItemStack(ModBlocks.carcassPig), 0);

        } else if (entity instanceof EntityCow) {

            event.getDrops().clear();
            entity.entityDropItem(new ItemStack(ModBlocks.carcassCowpart), 0);
            
        } else if (entity instanceof EntitySheep) {

            event.getDrops().clear();
            entity.entityDropItem(new ItemStack(ModBlocks.carcassSheep), 0);

        } else if (entity instanceof EntityChicken) {

            event.getDrops().clear();
            entity.entityDropItem(new ItemStack(ModBlocks.carcassChicken), 0);

        } else if (entity instanceof EntityRabbit) {

            event.getDrops().clear();
            entity.entityDropItem(new ItemStack(ModBlocks.carcassRabbit), 0);
        }
    }
}