package com.auto_farming.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import com.auto_farming.AutofarmingClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.fabricmc.loader.api.FabricLoader;

public class SaveDataLoader {

    private static Path configDir = FabricLoader.getInstance().getConfigDir();
    private static Path autoFarmingConfigPath = configDir.resolve(AutofarmingClient.MOD_ID + ".json");

    public static ModData load() {
        AutofarmingClient.LOGGER.info("loading from: " + autoFarmingConfigPath.toString());
        String configString;

        try {
            configString = readFromInputStream(new FileInputStream(autoFarmingConfigPath.toFile()));
        } catch (IOException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            ModData modData = new ModData();
            modData.init();
            save(modData);
            return modData;
        }

        AutofarmingClient.LOGGER.info("loading: " + configString);

        ObjectMapper objectMapper = new ObjectMapper();
        ModData data;

        try {
            data = objectMapper.readValue(configString, ModData.class);
        } catch (JsonProcessingException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            ModData modData = new ModData();
            modData.init();
            save(modData);
            return modData;
        }

        data.init();
        ModDataHolder.DATA.set(data);
        return data;
    }

    public static void save(ModData data) {
        AutofarmingClient.LOGGER.info("saving to: " + autoFarmingConfigPath.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            return;
        }

        AutofarmingClient.LOGGER.info("saving: " + json);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoFarmingConfigPath.toFile()))) {
            writer.write(json);
        } catch (IOException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            return;
        }

        ModDataHolder.DATA.set(data);
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
