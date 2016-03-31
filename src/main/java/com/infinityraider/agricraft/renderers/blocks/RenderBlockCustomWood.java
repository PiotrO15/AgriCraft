package com.infinityraider.agricraft.renderers.blocks;


import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<T extends TileEntityCustomWood> extends RenderBlockBase<T> {
    protected RenderBlockCustomWood(Block block, T te, boolean inventory, boolean tesr, boolean isbrh) {
		super(block, te, inventory, tesr, isbrh);
    }

	@Override
	public final void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block, @Nullable T tile, boolean dynamicRender, float partialTick, int destroyStage) {
		if(tile != null) {
			this.renderWorldBlock(tessellator, world, pos, x, y, z, state, block, tile, dynamicRender, partialTick, destroyStage, tile.getIcon());
		}
	}

	public abstract void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block, T tile, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite matIcon);

	@Override
	public TextureAtlasSprite getIcon() {
		return getTileEntity().getIcon();
	}
}
