package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DecorativeBlocks {

  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);
  private static final Item.Properties GENERAL_PROPS = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GENERAL_PROPS);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(modEventBus);
  }

  /* Decorative Blocks */
  public static final BlockItemObject<ClearGlassBlock> clear_glass = BLOCKS.register("clear_glass", () -> new ClearGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK), DEFAULT_BLOCK_ITEM);
  public static final EnumObject<ClearStainedGlassBlock.GlassColor,ClearStainedGlassBlock> clear_stained_glass = BLOCKS.registerEnum(ClearStainedGlassBlock.GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK, color), DEFAULT_BLOCK_ITEM);

  public static final BuildingBlockObject mud_bricks = BLOCKS.registerBuilding("mud_bricks", BlockProperties.MUD_BRICKS, DEFAULT_BLOCK_ITEM);
  public static final BuildingBlockObject dried_clay = BLOCKS.registerBuilding("dried_clay", BlockProperties.DRIED_CLAY, DEFAULT_BLOCK_ITEM);
  public static final BuildingBlockObject dried_clay_bricks = BLOCKS.registerBuilding("dried_clay_bricks", BlockProperties.DRIED_CLAY_BRICKS, DEFAULT_BLOCK_ITEM);

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(clear_glass.get(), RenderType.getCutout());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      RenderTypeLookup.setRenderLayer(clear_stained_glass.get(color), RenderType.getTranslucent());
    }
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();

    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      Block block = clear_stained_glass.get(color);
      blockColors.register((state, reader, pos, index) -> color.getColor(), block);
      itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null,  null, index);
      }, block);
    }
  }
}