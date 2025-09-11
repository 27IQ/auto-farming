package com.auto_farming;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.chat.Commands;
import com.auto_farming.config.ModConfig;
import com.auto_farming.config.ModData;
import com.auto_farming.config.SaveDataLoader;
import com.auto_farming.gui.BasicHUD;
import com.auto_farming.input.InputHandler;
import com.auto_farming.input.Key;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class AutofarmingClient implements ClientModInitializer {
	public static final String MOD_ID = "auto-farming";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static volatile AutoFarm autoFarm;

	@Override
	public void onInitializeClient() {


		Actions.initActions();
		BasicHUD.registerHUD();
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		Key.register();
		InputHandler.registerKeybinds();
		Commands.registerCommandQueue();

		LOGGER.info("auto-farming loaded successfully");
	}

	public static AutoFarm getNewAutofarmInstance(){
		autoFarm=new AutoFarm(SaveDataLoader.load());
		return autoFarm;
	}
}