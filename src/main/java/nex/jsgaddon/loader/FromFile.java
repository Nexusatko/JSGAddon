package nex.jsgaddon.loader;

import com.google.gson.reflect.TypeToken;
import nex.jsgaddon.JSGAddon;
import nex.jsgaddon.command.stargate.AddAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FromFile {
    public static final Map<String, Map<String, JsonStargateAddress>> ADDRESS_MAP = new HashMap<>();
    public static File configFile;
    private static Map<String, Map<String, ArrayList<String>>> ADDRESS_MAP_STRING = new HashMap<>();

    public static void reload() throws IOException {
        load(configFile, true);
    }

    public static void load(File configDir) {
        try {
            if (!Files.exists(configDir.toPath().resolveSibling("config/jsgaddon"))) {
                Files.createDirectory(configDir.toPath().resolveSibling("config/jsgaddon"));
                JSGAddon.info("Config directory created!");
            }
            load(configDir, false);
        } catch (Exception e) {
            JSGAddon.logger.error(e);
        }
    }

    public static void load(File configFileOrDir, boolean pathContainsFile) {
        ADDRESS_MAP.clear();
        ADDRESS_MAP_STRING.clear();
        if (!pathContainsFile) {
            configFile = new File(configFileOrDir, "jsgaddon/addresslist.json");
        } else {
            configFile = configFileOrDir;
        }
        try {
            Type mapType = new TypeToken<Map<String, Map<String, ArrayList<String>>>>() {
            }.getType();
            ADDRESS_MAP_STRING = AddAddress.gson.fromJson(new FileReader(configFile), mapType);

            for (Map.Entry<String, Map<String, ArrayList<String>>> key : ADDRESS_MAP_STRING.entrySet()) {
                Map<String, ArrayList<String>> map = key.getValue();
                Map<String, JsonStargateAddress> newMap = new HashMap<>();
                for (Map.Entry<String, ArrayList<String>> sTypeString : map.entrySet()) {
                    newMap.put(sTypeString.getKey(), new JsonStargateAddress(sTypeString.getKey(), sTypeString.getValue()));
                }
                ADDRESS_MAP.put(key.getKey(), newMap);
            }
        } catch (FileNotFoundException e) {
            try {
                write();
            } catch (Exception ex) {
                JSGAddon.error("Error while loading addresses from file!");
                JSGAddon.error("File: " + configFile.getAbsolutePath());
                JSGAddon.error("Error:");
                ex.printStackTrace();
            } finally {
                load(configFileOrDir);
            }
        } catch (Exception e) {
            JSGAddon.error("Error while loading addresses from file!");
            JSGAddon.error("File: " + configFile.getAbsolutePath());
            JSGAddon.error("Error:");
            e.printStackTrace();
        } finally {
            JSGAddon.info("Loaded " + ADDRESS_MAP.size() + " addresses from the file!");
            JSGAddon.info(ADDRESS_MAP.keySet().toString());
        }
    }

    public static void update() throws IOException {
        if (ADDRESS_MAP.size() < 1) {
            write();
        }
    }

    private static final Random rand = new Random();

    public static void write() throws IOException {
        if (configFile == null) return;
        FileWriter writer = new FileWriter(configFile);
        ADDRESS_MAP.clear();
        ADDRESS_MAP_STRING.clear();


        Map<String, ArrayList<String>> map = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            SymbolTypeEnum type = SymbolTypeEnum.valueOf(i);
            ArrayList<String> address = new ArrayList<>();
            for (int u = 0; u < 6; u++) {
                address.add(type.getRandomSymbol(rand).getEnglishName());
            }
            address.add(type.getOrigin().getEnglishName());
            map.put(type.toString(), address);
        }

        ADDRESS_MAP_STRING.put("Example", map);

        writer.write(AddAddress.gson.toJson(ADDRESS_MAP_STRING));
        writer.close();
        ADDRESS_MAP_STRING.clear();
    }
}
