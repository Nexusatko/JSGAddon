package nex.jsgaddon.utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.util.LinkingHelper;

import java.util.ArrayList;

public class FindNearestTile {
    public static TileEntity runByCLass(World world, BlockPos startPos, Class<? extends TileEntity> findByClass, int radiusXZ, int radiusY) {
        BlockPos found = null;
        TileEntity tileEntity = null;

        ArrayList<BlockPos> blacklist = new ArrayList<>();
        while (found == null) {
            found = LinkingHelper.findClosestPos(world, startPos, new BlockPos(radiusXZ, radiusY, radiusXZ), JSGBlocks.STARGATE_BASE_BLOCKS, blacklist);
            if (found == null) break;
            tileEntity = world.getTileEntity(found);
            if (!findByClass.isInstance(tileEntity)) {
                blacklist.add(found);
                tileEntity = null;
            }
        }
        return tileEntity;
    }
}
