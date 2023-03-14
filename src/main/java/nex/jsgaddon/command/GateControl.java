package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.energy.CapabilityEnergy;
import nex.jsgaddon.JSGAddon;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.power.stargate.StargateClassicEnergyStorage;
import tauri.dev.jsg.power.stargate.StargateItemEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

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
        StargateClassicEnergyStorage energyStorage = (StargateClassicEnergyStorage) casted.getCapability(CapabilityEnergy.ENERGY, null);

        switch (args[0]) {
            case "energy":
                if (energyStorage == null) return;
                for (int i = 4; i <= 6; i++) {
                    ItemStack stack = casted.getItemHandler().getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    StargateItemEnergyStorage energy = (StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energy == null) continue;
                    switch (args[1]) {
                        case "max":
                            energy.setEnergyStored(energy.getMaxEnergyStored());
                            break;
                        case "half":
                            energy.setEnergyStored(energy.getMaxEnergyStored() / 2);
                            break;
                        case "none":
                            energy.setEnergyStored(0);
                            break;
                    }
                }
                switch (args[1]) {
                    case "max":
                        energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
                        sender.sendMessage(new TextComponentString("Stargate's energy was set to full of capacity."));
                        break;
                    case "half":
                        energyStorage.setEnergyStored(energyStorage.getMaxEnergyStoredInternally() / 2);
                        sender.sendMessage(new TextComponentString("Stargate's energy was set to half of capacity."));
                        break;
                    case "none":
                        energyStorage.setEnergyStored(0);
                        break;
                }
                JSGAddon.info("Energy subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            case "iris":
                switch (args[1]) {
                    case "code":
                        if (!casted.hasIris()) return;
                        sender.sendMessage(new TextComponentString(String.valueOf(casted.getIrisCode())));
                        break;
                    case "type":
                        if (!casted.hasIris()) return;
                        sender.sendMessage(new TextComponentString(casted.getIrisType().toString()));
                        break;
                    case "status":
                    case "state":
                        if (!casted.hasIris()) return;
                        sender.sendMessage(new TextComponentString(casted.getIrisState().toString()));
                        break;
                    case "toggle":
                        if (casted.hasIris() && casted.isIrisClosed()) {
                            casted.toggleIris();
                            sender.sendMessage(new TextComponentString("Iris was opened."));
                        } else if (casted.hasIris() && casted.isIrisOpened()) {
                            casted.toggleIris();
                            sender.sendMessage(new TextComponentString("Iris was closed."));
                        }
                        else if (casted.hasIris()) {
                            sender.sendMessage(new TextComponentString("Iris is busy."));
                        } else {
                            sender.sendMessage(new TextComponentString("No Iris present!"));
                        }
                        break;
                }
                JSGAddon.info("Iris subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            case "gen":
                int amount = Integer.parseInt(args[1]);
                casted.generateIncoming(amount, 9);
                JSGAddon.info("Gen subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            default:
                sender.sendMessage(new TextComponentString("Please, choose a sub-function ( energy / iris / gen )"));
                break;
        }

    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "energy", "iris", "gen");
            case 2:
                switch (args[0]) {
                    case "energy":
                        return getListOfStringsMatchingLastWord(args, "max", "half", "none");
                    case "iris":
                        return getListOfStringsMatchingLastWord(args, "status", "state", "toggle", "type", "code");
                }
                break;

        }
        return new ArrayList<>();
    }
}
