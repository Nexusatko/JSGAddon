package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.energy.CapabilityEnergy;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class GateControl extends CommandBase {

    @Override
    @Nonnull
    public String getName() {
        return "gate";
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/gate";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        TileEntity tileEntity = null;
        if (args.length > 2) {
            EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[2]);
            if (player != null) {
                tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
            } else {
                sender.sendMessage(new TextComponentString("Player is either invalid or not online."));
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        StargateAbstractEnergyStorage getEnergyStorage = (StargateAbstractEnergyStorage) casted.getCapability(CapabilityEnergy.ENERGY, null);


        switch (args[0]) {
            case "energy":
                if (getEnergyStorage == null) return;
                ItemStack cap1 = casted.getItemHandler().getStackInSlot(4);
                ItemStack cap2 = casted.getItemHandler().getStackInSlot(5);
                ItemStack cap3 = casted.getItemHandler().getStackInSlot(6);
                StargateItemEnergyStorage cen1 = (StargateItemEnergyStorage) cap1.getCapability(CapabilityEnergy.ENERGY, null);
                StargateItemEnergyStorage cen2 = (StargateItemEnergyStorage) cap2.getCapability(CapabilityEnergy.ENERGY, null);
                StargateItemEnergyStorage cen3 = (StargateItemEnergyStorage) cap3.getCapability(CapabilityEnergy.ENERGY, null);
                if (args[1].equalsIgnoreCase("max")) {
                    if (cen1 != null) {
                        cen1.setEnergyStored(cen1.getMaxEnergyStored());
                    }
                    if (cen2 != null) {
                        cen2.setEnergyStored(cen2.getMaxEnergyStored());
                    }
                    if (cen3 != null) {
                        cen3.setEnergyStored(cen3.getMaxEnergyStored());
                    }
                    getEnergyStorage.setEnergyStored(getEnergyStorage.getMaxEnergyStored());
                    sender.sendMessage(new TextComponentString("Stargate's energy set to max"));
                } else if (args[1].equalsIgnoreCase("half")) {
                    if (cen1 != null) {
                        cen1.setEnergyStored(cen1.getMaxEnergyStored() / 2);
                    }
                    if (cen2 != null) {
                        cen2.setEnergyStored(cen2.getMaxEnergyStored() / 2);
                    }
                    if (cen3 != null) {
                        cen3.setEnergyStored(cen3.getMaxEnergyStored() / 2);
                    }
                    getEnergyStorage.setEnergyStored((getEnergyStorage.getMaxEnergyStored() / 2));
                    sender.sendMessage(new TextComponentString("Stargate's energy set to half"));
                }
                return;
            case "iris":
                return;
            case "set":
            case "default":
                sender.sendMessage(new TextComponentString("Please, choose a sub-function ( energy / iris / set )"));
                break;
        }

    }

}
