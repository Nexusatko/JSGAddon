package nex.jsgaddon.command;

import com.google.gson.GsonBuilder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;

public class AddAddress extends CommandBase {
    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }

    @Override
    public String getName() {
        return "addaddress";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/addaddress";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        File configFile;

        boolean pathContainsFile = false;
        File configFileOrDir = null;
        File configFile2 = new File(configFileOrDir, "config/jsgaddon/playerlist.json");

        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        if (tileEntity == null) {
            sender.sendMessage(new TextComponentString("Can't find Stargate in your radius."));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Enter an new address name!"));
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;

        String name = args[0];
        JsonStargateAddress foundAddress = findAddress(name.replace("-", " "), casted.getSymbolType());
        if (foundAddress != null) {
            sender.sendMessage(new TextComponentString("This Stargate is already logged!"));
            return;
        }
        try {
            write(name, casted, true, new File(configFile2.getAbsolutePath()));
            sender.sendMessage(new TextComponentString("Stargate's multi address logged under name '" + name + "'!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(String name, StargateClassicBaseTile casted, boolean pathContainsFile, File configFile2) throws IOException {
        Map<String, Map<String, ArrayList<String>>> ADDRESS_MAP_STRING = new HashMap<>();
        FileWriter writer = new FileWriter(configFile2, false);

        Map<String, ArrayList<String>> map = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            SymbolTypeEnum type = SymbolTypeEnum.valueOf(i);
            ArrayList<String> address = new ArrayList<>();
            for (int u = 0; u < 8; u++) {
                address.add(casted.getStargateAddress(type).get(u).getEnglishName());
            }
            address.add(type.getOrigin().getEnglishName());
            map.put(type.toString(), address);
        }

        ADDRESS_MAP_STRING.put(name, map);

        writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(ADDRESS_MAP_STRING));
        writer.close();
        ADDRESS_MAP_STRING.clear();
    }
}

