package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.energy.CapabilityEnergy;
import nex.jsgaddon.JSGAddon;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.command.JSGACommand;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.power.general.ItemEnergyStorage;
import tauri.dev.jsg.power.general.LargeEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class GateControl extends AbstractJSGACommand {

    @Override
    @Nonnull
    public String getName() {
        return "gate";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Controls the nearest gate within your radius";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "gate <energy/iris/gen> (Just tab it)";
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
                baseCommand.sendErrorMess(sender, "Player is either invalid or not online.");
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        LargeEnergyStorage energyStorage = (LargeEnergyStorage) casted.getCapability(CapabilityEnergy.ENERGY, null);

        switch (args[0]) {
            case "energy":
                if (energyStorage == null) return;
                for (int i = 4; i <= 6; i++) {
                    ItemStack stack = casted.getItemHandler().getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    ItemEnergyStorage energy = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
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
                        case "default":
                            return;
                    }
                }
                switch (args[1]) {
                    case "max":
                        energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
                        baseCommand.sendSuccessMess(sender, "Stargate's energy was set to full of capacity.");
                        break;
                    case "half":
                        energyStorage.setEnergyStored(energyStorage.getMaxEnergyStoredInternally() / 2);
                        ((JSGACommand) baseCommand).sendSuccessMess(sender, new TextComponentString("Stargate's energy was set to half of capacity."));
                        break;
                    case "none":
                        energyStorage.setEnergyStored(0);
                        break;
                    case "default":
                        return;
                }
                JSGAddon.info("Energy subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            case "iris":
                switch (args[1]) {
                    case "code":
                        if (!casted.hasIris()) return;
                        ((JSGACommand) baseCommand).sendInfoMess(sender, new TextComponentString(String.valueOf(casted.getIrisCode())));
                        break;
                    case "type":
                        if (!casted.hasIris()) return;
                        ((JSGACommand) baseCommand).sendInfoMess(sender, new TextComponentString(casted.getIrisType().toString()));

                        break;
                    case "status":
                    case "state":
                        if (!casted.hasIris()) return;
                        ((JSGACommand) baseCommand).sendInfoMess(sender, new TextComponentString(casted.getIrisState().toString()));
                        break;
                    case "toggle":
                        if (casted.hasIris() && casted.isIrisClosed()) {
                            casted.toggleIris();
                            ((JSGACommand) baseCommand).sendSuccessMess(sender, new TextComponentString("Iris was opened."));
                        } else if (casted.hasIris() && casted.isIrisOpened()) {
                            casted.toggleIris();
                            ((JSGACommand) baseCommand).sendSuccessMess(sender, new TextComponentString("Iris was closed."));
                        } else if (casted.hasIris()) {
                            ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Iris is busy."));
                        } else {
                            ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("No Iris present!"));
                        }
                        break;
                    case "default":
                        return;
                }
                JSGAddon.info("Iris subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            case "gen":
                int amount = Integer.parseInt(args[1]);
                casted.generateIncoming(amount, 9);
                JSGAddon.info("Gen subsystem of GateControl was used by " + sender.getName() + "!");
                break;
            default:
                baseCommand.sendUsageMess(sender, this);
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
