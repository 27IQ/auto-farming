package com.auto_farming;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.chat.Commands;
import com.auto_farming.config.ModConfig;
import com.auto_farming.event.EventManager;
import com.auto_farming.farminglogic.BlockBreakDetection;
import com.auto_farming.gui.HudHelper;
import com.auto_farming.input.InputHandler;
import com.auto_farming.scoreboard.Scoreboard;
import com.auto_farming.sounds.SoundAlert;

public class AutofarmingClient implements ClientModInitializer {
	public static final String MOD_ID = "auto-farming";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {

		EventManager.scanAndRegister("com.auto_farming");
		Actions.register();
		HudHelper.registerAllHuds();
		ModConfig.register();
		InputHandler.register();
		Commands.register();
		Scoreboard.register();
		SoundAlert.registerAll();
		BlockBreakDetection.register();

		LOGGER.info("auto-farming loaded successfully");
	}
}