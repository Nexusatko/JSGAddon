package nex.jsgaddon.command.stargate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;

public class AddAddress extends AbstractJSGACommand {
    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }

    @Nonnull
    @Override
    public String getName() {
        return "addaddress";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Adds address under defined name into playerlist.json";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "addaddress <name of address>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, ICommandSender sender, @Nonnull String[] args) {
        File configFileOrDir = null;
        File configFile2 = new File(configFileOrDir, "config/jsgaddon/addaddress.json");

        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        if (tileEntity == null) {
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        if (args.length == 0) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;

        String name = args[0];
        JsonStargateAddress foundAddress = findAddress(name.replace("-", " "), casted.getSymbolType());
        if (foundAddress != null) {
            baseCommand.sendErrorMess(sender, "This name is already used!");
            return;
        }
        try {
            write(name, casted, new File(configFile2.getAbsolutePath()));
            baseCommand.sendSuccessMess(sender, "Stargate's address types logged under name '" + name + "'!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
    public void write(String name, StargateClassicBaseTile casted, File configFile2) throws IOException {

        Map<String, Map<String, ArrayList<String>>> ADDRESS_MAP_STRING = new HashMap<>();
        FileWriter writer = new FileWriter(configFile2, false);

        Map<String, ArrayList<String>> map = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            SymbolTypeEnum type = SymbolTypeEnum.valueOf(i);
            ArrayList<String> address = new ArrayList<>();
            for (int u = 0; u < 8; u++) {
                address.add(Objects.requireNonNull(casted.getStargateAddress(type)).get(u).getEnglishName());
            }
            address.add(type.getOrigin().getEnglishName());
            map.put(type.toString(), address);
        }

        ADDRESS_MAP_STRING.put(name, map);

        writer.write(gson.toJson(ADDRESS_MAP_STRING));
        writer.close();
        ADDRESS_MAP_STRING.clear();
    }
}

