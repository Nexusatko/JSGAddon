package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.energy.CapabilityEnergy;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

public class GateControl extends CommandBase {

    @Override
    public String getName() {
        return "gate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/gate";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        if (tileEntity == null) {
            sender.sendMessage(new TextComponentString("Can't find Stargate in your radius."));
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        StargateAbstractEnergyStorage getEnergyStorage = (StargateAbstractEnergyStorage) casted.getCapability(CapabilityEnergy.ENERGY, null);
        ItemStack capacitor1 = casted.getItemHandler().getStackInSlot(4);
        StargateItemEnergyStorage energyStorage = (StargateItemEnergyStorage) capacitor1.getCapability(CapabilityEnergy.ENERGY, null);
        energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
        ItemStack capacitor2 = casted.getItemHandler().getStackInSlot(5);
        StargateItemEnergyStorage energyStorage2 = (StargateItemEnergyStorage) capacitor2.getCapability(CapabilityEnergy.ENERGY, null);
        energyStorage2.setEnergyStored(energyStorage.getMaxEnergyStored());
        ItemStack capacitor3 = casted.getItemHandler().getStackInSlot(6);
        StargateItemEnergyStorage energyStorage3 = (StargateItemEnergyStorage) capacitor3.getCapability(CapabilityEnergy.ENERGY, null);
        energyStorage3.setEnergyStored(energyStorage.getMaxEnergyStored());
        getEnergyStorage.setEnergyStored(getEnergyStorage.getMaxEnergyStored());

        sender.sendMessage(new TextComponentString("Stargate's energy set to max"));

        switch (args[0]) {
            case "energy":
            case "iris":
            case "set":
            case "default":
                sender.sendMessage(new TextComponentString("Please, choose a sub-function ( energy / iris / set )"));
                break;
        }

    }

}
