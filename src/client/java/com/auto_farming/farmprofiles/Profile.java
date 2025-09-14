package com.auto_farming.farmprofiles;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.config.ModConfig;
import com.auto_farming.config.ModData;
import com.auto_farming.config.clothconfigextensions.ButtonEntry;
import com.auto_farming.config.clothconfigextensions.WideStringListEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Profile {
    public Profile() {
    }

    public final static String EMPTY_JSON_PROFILE_STRING = "{\"name\":\"\",\"leftRowClearTime\":0,\"rightRowClearTime\":0,\"voidDropTime\":0,\"layerSwapTime\":0,\"layerCount\":0,\"actionsLeft\":[],\"actionsRight\":[],\"actionsLayerSwap\":[]}";

    public Profile(String name, long leftRowClearTime, long rightRowClearTime, long voidDropTime, long layerSwapTime,
            int layerCount, Actions[] actionsLeft, Actions[] actionsRight, Actions[] actionsLayerSwap) {
        this.name = name;
        this.leftRowClearTime = leftRowClearTime;
        this.rightRowClearTime = rightRowClearTime;
        this.voidDropTime = voidDropTime;
        this.layerSwapTime = layerSwapTime;
        this.layerCount = layerCount;
        this.actionsLeft = actionsLeft;
        this.actionsRight = actionsRight;
        this.actionsLayerSwap = actionsLayerSwap;
    }

    public Profile(String jsonString) {
        setJsonString(jsonString);
    }

    public String name;
    public long leftRowClearTime;
    public long rightRowClearTime;
    public long voidDropTime;
    public long layerSwapTime;
    public int layerCount;
    public Actions[] actionsLeft;
    public Actions[] actionsRight;
    public Actions[] actionsLayerSwap;

    public TextListEntry getNameLabel(ConfigEntryBuilder builder) {
        return builder
                .startTextDescription(Text.of(name))
                .build();
    }

    @JsonIgnore
    public WideStringListEntry getSettingTextField(ConfigEntryBuilder builder) {

        String json = getJsonString();

        return new WideStringListEntry(
                json,
                () -> json,
                jsonString -> {
                    setJsonString(jsonString);
                });
    }

    public ButtonEntry getDeleteButton(ConfigEntryBuilder builder, ModData data, Screen parent) {
        return new ButtonEntry(Text.literal("Delete Profile"), (() -> {
            List<Profile> profiles = data.getProfiles();
            profiles.remove(this);
            data.setProfiles(profiles);
            ModConfig.reload(parent);
        }));
    }

    @JsonIgnore
    public String getJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return "";
        }

        return json;
    }

    @JsonIgnore
    public void setJsonString(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Profile data;

        try {
            data = objectMapper.readValue(json, Profile.class);
        } catch (JsonProcessingException e) {
            AutofarmingClient.LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return;
        }

        this.name = data.name;
        this.leftRowClearTime = data.leftRowClearTime;
        this.rightRowClearTime = data.rightRowClearTime;
        this.voidDropTime = data.voidDropTime;
        this.layerSwapTime = data.layerSwapTime;
        this.layerCount = data.layerCount;
        this.actionsLeft = data.actionsLeft;
        this.actionsRight = data.actionsRight;
        this.actionsLayerSwap = data.actionsLayerSwap;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Profile))
            return false;

        Profile otherProfile = (Profile) other;

        if (!checkActionsEqual(actionsLeft, otherProfile.actionsLeft) &&
                !checkActionsEqual(actionsRight, otherProfile.actionsRight) &&
                !checkActionsEqual(actionsLayerSwap, otherProfile.actionsLayerSwap))
            return false;

        return this.name.equals(otherProfile.name) &&
                this.leftRowClearTime == otherProfile.leftRowClearTime &&
                this.rightRowClearTime == otherProfile.rightRowClearTime &&
                this.voidDropTime == otherProfile.voidDropTime &&
                this.layerSwapTime == otherProfile.layerSwapTime &&
                this.layerCount == otherProfile.layerCount;

    }

    private boolean checkActionsEqual(Actions[] actions, Actions[] otherActions) {
        if (actions.length != otherActions.length)
            return false;

        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != otherActions[i])
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, leftRowClearTime, rightRowClearTime,
                voidDropTime, layerSwapTime, layerCount);
        result = 31 * result + Arrays.hashCode(actionsLeft);
        result = 31 * result + Arrays.hashCode(actionsRight);
        result = 31 * result + Arrays.hashCode(actionsLayerSwap);
        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }
}