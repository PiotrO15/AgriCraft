package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.tileentity.TileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public abstract class BlockBaseNoTile extends BlockBase<TileEntityBase> {

    protected BlockBaseNoTile(Material mat, String internalName) {
        super(mat, internalName);
    }
}
