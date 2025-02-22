package slimeknights.tconstruct.shared;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.shared.block.OrientableBlock;
import slimeknights.tconstruct.shared.block.SlimesteelBlock;

/**
 * Contains bommon blocks and items used in crafting materials
 */
@SuppressWarnings("unused")
public final class TinkerMaterials extends TinkerModule {
  // ores
  public static final MetalItemObject copper = BLOCKS.registerMetal("copper", metalBuilder(MaterialColor.ADOBE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", metalBuilder(MaterialColor.BLUE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 3
  public static final MetalItemObject slimesteel    = BLOCKS.registerMetal("slimesteel", () -> new SlimesteelBlock(metalBuilder(MaterialColor.WARPED_WART).notSolid()), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject tinkersBronze = BLOCKS.registerMetal("tinkers_bronze", "silicon_bronze", metalBuilder(MaterialColor.WOOD), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject roseGold      = BLOCKS.registerMetal("rose_gold", metalBuilder(MaterialColor.WHITE_TERRACOTTA), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject pigIron       = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(metalBuilder(MaterialColor.PINK)), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 4
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", metalBuilder(MaterialColor.GREEN), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", metalBuilder(MaterialColor.PURPLE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", metalBuilder(MaterialColor.BLUE_TERRACOTTA), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", metalBuilder(MaterialColor.BROWN).notSolid(), HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> debrisNugget = ITEMS.register("debris_nugget", TOOLTIP_ITEM);
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", metalBuilder(MaterialColor.MAGENTA), HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);

  // non-metal
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> bloodbone = ITEMS.register("bloodbone", TOOLTIP_ITEM);
  public static final ItemObject<Item> blazingBone = ITEMS.register("blazing_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> necroniumBone = ITEMS.register("necronium_bone", TOOLTIP_ITEM);
  public static final FenceBuildingBlockObject nahuatl = BLOCKS.registerFenceBuilding("nahuatl", builder(Material.WOOD, MaterialColor.OBSIDIAN, ToolType.AXE, SoundType.WOOD).harvestLevel(HarvestLevels.DIAMOND).setRequiresTool().hardnessAndResistance(25f, 300f), GENERAL_BLOCK_ITEM);

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegistryEvent<IRecipeSerializer<?>> event) {
    CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
  }
}
