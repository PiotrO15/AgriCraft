package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.StatsConfig;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.journal.GeneticsPage;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GeneticsPageDrawer implements JournalPageDrawer<GeneticsPage> {

	private final ResourceLocation DNA_SCHEMATIC = new ResourceLocation(AgriApi.MOD_ID, "textures/gui/journal/dna_schematic.png");

	private final Component CROP_BREEDING = Component.translatable("agricraft.journal.crop_breeding");
	private final Component PARAGRAPH_L_1 = Component.translatable("agricraft.journal.crop_breeding.paragraph_1");
	private final Component PARAGRAPH_L_2 = Component.translatable("agricraft.journal.crop_breeding.paragraph_2");
	private final Component PARAGRAPH_L_3 = Component.translatable("agricraft.journal.crop_breeding.paragraph_3");

	private final Component STATS = Component.translatable("agricraft.journal.stats");
	private final Component PARAGRAPH_R_1 = Component.translatable("agricraft.journal.stats.paragraph_1");
	private final Component PARAGRAPH_GROWTH = Component.translatable("agricraft.journal.stats.growth");
	private final Component PARAGRAPH_GAIN = Component.translatable("agricraft.journal.stats.gain");
	private final Component PARAGRAPH_STRENGTH = Component.translatable("agricraft.journal.stats.strength");
	private final Component PARAGRAPH_RESISTANCE = Component.translatable("agricraft.journal.stats.resistance");
	private final Component PARAGRAPH_FERTILITY = Component.translatable("agricraft.journal.stats.fertility");
	private final Component PARAGRAPH_MUTATIVITY = Component.translatable("agricraft.journal.stats.mutativity");

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, GeneticsPage page, int pageX, int pageY, JournalData journalData) {
		Font font = Minecraft.getInstance().font;
		float scale = 0.7F;
		float dx = pageX + 6;
		float dy = pageY + 7 + 10;
		int spacing = 4;
		// Title
		guiGraphics.drawString(font, CROP_BREEDING, (int) dx, (int) dy, 0, false);
		dy += font.lineHeight;
		dy += spacing;
		// First paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_L_1, dx, dy, scale);
		dy += spacing;
		// Second paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_L_2, dx, dy, scale);
		dy += spacing;
		// Illustration
		guiGraphics.blit(DNA_SCHEMATIC, (int) dx, (int) dy, 0, 0, 96, 32, 96, 32);
		dy += spacing + 32;
		// Third paragraph
		this.drawScaledText(guiGraphics, PARAGRAPH_L_3, dx, dy, scale);
	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, GeneticsPage page, int pageX, int pageY, JournalData journalData) {
		Font font = Minecraft.getInstance().font;

		float dx = pageX + 6;
		float dy = pageY + 10;
		float spacing = 4;
		// Title
		guiGraphics.drawString(font, STATS, (int) dx, (int) dy, 0, false);
		dy += font.lineHeight;
		dy += spacing;

		// First paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_R_1, dx, dy, 0.7F);
		dy += spacing;

		// Growth
		if (!StatsConfig.growthHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().growthStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_GROWTH, dx, dy, 0.6F);
			dy += spacing / 2;
		}
		// Gain
		if (!StatsConfig.gainHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().gainStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_GAIN, dx, dy, 0.6F);
			dy += spacing / 2;
		}
		// Strength
		if (!StatsConfig.strengthHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().strengthStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_STRENGTH, dx, dy, 0.6F);
			dy += spacing / 2;
		}
		// Resistance
		if (!StatsConfig.resistanceHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().resistanceStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_RESISTANCE, dx, dy, 0.6F);
			dy += spacing / 2;
		}
		// Fertility
		if (!StatsConfig.fertilityHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().fertilityStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_FERTILITY, dx, dy, 0.6F);
			dy += spacing / 2;
		}
		// Mutativity
		if (!StatsConfig.mutativityHidden) {
			dy += this.drawScaledText(guiGraphics, LangUtils.statName(AgriStatRegistry.getInstance().mutativityStat()).withStyle(ChatFormatting.UNDERLINE), dx, dy, 0.7F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_MUTATIVITY, dx, dy, 0.6F);
		}
	}

}
