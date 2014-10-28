package tconstruct.tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.DualHarvestTool;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.HarvestLevels;
import tconstruct.library.weaponry.IAmmo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ToolStationGuiHelper {
    // non-instantiable
    private ToolStationGuiHelper() {}

    private static final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
    private static int xPos, yPos;

    private static final DecimalFormat df =  new DecimalFormat("##.#");

    private static void newline()
    {
        yPos += 10;
    }


    private static void write(String s)
    {
        fontRendererObj.drawString(s, xPos, yPos, 0xffffffff);
        newline();
    }

    public static void drawToolStats (ItemStack stack, int x, int y)
    {
        String name = stack.getItem() instanceof ToolCore ? ((ToolCore) stack.getItem()).getLocalizedToolName() : stack.getDisplayName();
        Item item = stack.getItem();
        NBTTagCompound tags = stack.getTagCompound();
        Collection<String> categories = new LinkedList<String>();

        // initialize drawing variables
        xPos = x;
        yPos = y + 8;

        // get the correct tags
        if(item instanceof IModifyable) {
            IModifyable modifyable = (IModifyable) item;
            tags = tags.getCompoundTag(modifyable.getBaseTagName());
            categories = Arrays.asList(modifyable.getTraits());
        }

        drawCenteredString(fontRendererObj, "\u00A7n" + name, xPos + 55, yPos, 0xffffffff);
        newline();
        newline();

        // does it have ammo instead of durability?
        if(item instanceof IAmmo)
            drawAmmo((IAmmo) item, stack);
        // regular durability?
        else if(item instanceof ToolCore || item instanceof ArmorCore)
            drawDurability(tags);

        // tools
        if(item instanceof ToolCore) {
            ToolCore tool = (ToolCore) item;
            // DualHarvest tool?
            if (categories.contains("dualharvest"))
                drawDualHarvestStats(tool, tags);
            // or regular Harvest tool?
            else if (categories.contains("harvest"))
                drawHarvestStats(tool, tags);
            // weapon?
            if (categories.contains("weapon"))
                drawWeaponStats(tool, tags);
            // projectile weapon?
            if (categories.contains("projectileweapon"))
                drawProjectileWeaponStats(tags);
            // projectile?
            if (categories.contains("projectile"))
                drawProjectileStats(tags);
        }
        // armor
        if(item instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) item;
            drawArmorStats(armor, tags, stack);
        }
        // Accessory
        if(item instanceof AccessoryCore)
        {
            AccessoryCore accessory = (AccessoryCore) item;
            drawAccessoryStats(accessory, tags);
        }

        newline();

        // Modifiers
        drawModifiers(tags);
    }

    private static void drawDurability(NBTTagCompound tags) {
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        final int availableDurability = maxDur - durability;

        // big durabilities have to split to 2 lines
        if (maxDur >= 10000)
        {
            write(StatCollector.translateToLocal("gui.toolstation1"));
            write("- " + availableDurability + "/" + maxDur);
        }
        else
        {
            write(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur);
        }
    }

    private static void drawAmmo(IAmmo ammoItem, ItemStack stack)
    {
        final int max = ammoItem.getAmmoCount(stack);
        final int current = ammoItem.getAmmoCount(stack);

        write(String.format("%s: %d/%d", StatCollector.translateToLocal("gui.toolstation21"), current, max));
    }

    private static void drawModifiers(NBTTagCompound tags)
    {
        int modifiers = tags.getInteger("Modifiers");
        // remaining modifiers
        if (modifiers != 0)
            write(StatCollector.translateToLocal("gui.toolstation18") + modifiers);

        // Modifier-header (if we have modifiers)
        if (tags.hasKey("ModifierTip1")) {
            write(StatCollector.translateToLocal("gui.toolstation17"));

            String tooltip = "ModifierTip";
            int tipNum = 1;
            while (tags.hasKey(tooltip + tipNum)) {
                write("- " + tags.getString(tooltip + tipNum));
                tipNum++;
            }
        }
    }

    private static void drawHarvestStats(ToolCore tool, NBTTagCompound tags)
    {
        float mineSpeed = AbilityHelper.calcToolSpeed(tool, tags);
        float stoneboundSpeed = AbilityHelper.calcStoneboundBonus(tool, tags);

        write(StatCollector.translateToLocal("gui.toolstation14") + df.format(mineSpeed));
        if(stoneboundSpeed != 0)
        {
            String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
            write(bloss + df.format(stoneboundSpeed));
        }
        write(StatCollector.translateToLocal("gui.toolstation15") + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")));
    }

    private static void drawDualHarvestStats(ToolCore tool, NBTTagCompound tags)
    {
        float mineSpeed = AbilityHelper.calcDualToolSpeed(tool, tags, false);
        float mineSpeed2 = AbilityHelper.calcDualToolSpeed(tool, tags, true);
        float stoneboundSpeed = AbilityHelper.calcStoneboundBonus(tool, tags);

        write(StatCollector.translateToLocal("gui.toolstation12"));
        write("- " + df.format(mineSpeed) + ", " + df.format(mineSpeed2));
        if (stoneboundSpeed != 0)
        {
            String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
            write(bloss + df.format(stoneboundSpeed));
        }

        write(StatCollector.translateToLocal("gui.toolstation13"));
        write("- " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel2")));
    }

    private static void drawWeaponStats(ToolCore tool, NBTTagCompound tags)
    {
        // DAMAGE
        int attack = (tags.getInteger("Attack")) + 1;

        // factor in Stonebound
        float stoneboundDamage = -AbilityHelper.calcStoneboundBonus(tool, tags);
        attack += stoneboundDamage;
        attack *= tool.getDamageModifier();

        if (attack < 1)
            attack = 1;

        String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
        if (attack % 2 == 0)
            write(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart);
        else
            write(StatCollector.translateToLocal("gui.toolstation3") + df.format(attack / 2f) + heart);

        if (stoneboundDamage != 0)
        {
            heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
            write(bloss + df.format(stoneboundDamage / 2f) + heart);
        }
    }

    private static void drawProjectileWeaponStats(NBTTagCompound tags)
    {
        /*
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        int drawSpeed = tags.getInteger("DrawSpeed");
        float flightSpeed = tags.getFloat("FlightSpeed");
        float trueDraw = drawSpeed / 20f;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", x, base + offset * 10, 0xffffff);
        offset++;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", x, base + offset * 10, 0xffffff);
        offset++;
        offset++;*/
    }

    private static void drawProjectileStats(NBTTagCompound tags)
    {/*
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        int attack = (int) (tags.getInteger("Attack"));
        float mass = tags.getFloat("Mass");
        float shatter = tags.getFloat("BreakChance");
        float accuracy = tags.getFloat("Accuracy");

        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation10"), x, base + offset * 10, 0xffffff);
        offset++;
        String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
        if (attack % 2 == 0)
            fontRendererObj.drawString("- " + attack / 2 + heart, x, base + offset * 10, 0xffffff);
        else
            fontRendererObj.drawString("- " + attack / 2f + heart, x, base + offset * 10, 0xffffff);
        offset++;
        int minAttack = attack;
        int maxAttack = attack * 2;
        heart = StatCollector.translateToLocal("gui.partcrafter9");
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation11"), x, base + offset * 10, 0xffffff);
        offset++;
        fontRendererObj.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, x, base + offset * 10, 0xffffff);
        offset++;
        offset++;

        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), x, base + offset * 10, 0xffffff);
        offset++;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", x, base + offset * 10, 0xffffff);
        offset++;
            /*
             * this.fontRendererObj.drawString("Chance to break: " +
             * df.format(shatter)+"%", xSize + 8, base + offset * 10, 0xffffff);
             * offset++;

        offset++;*/
    }

    private static void drawArmorStats(ArmorCore armor, NBTTagCompound tags, ItemStack stack)
    {
// Damage reduction
        double damageReduction = tags.getDouble("DamageReduction");
        if(damageReduction > 0)
            write(StatCollector.translateToLocal("gui.toolstation19") + df.format(damageReduction));

        // Protection
        double protection = armor.getProtection(stack);
        double maxProtection = tags.getDouble("MaxDefense");

        write(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection) + "/" + df.format(maxProtection));
    }

    private static void drawAccessoryStats(AccessoryCore core, NBTTagCompound tags)
    {
        if(tags.hasKey("MiningSpeed")) {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            write(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed);
        }
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     * Copied out of GUI
     */
    public static void drawCenteredString(FontRenderer p_73732_1_, String p_73732_2_, int p_73732_3_, int p_73732_4_, int p_73732_5_)
    {
        p_73732_1_.drawStringWithShadow(p_73732_2_, p_73732_3_ - p_73732_1_.getStringWidth(p_73732_2_) / 2, p_73732_4_, p_73732_5_);
    }
}
