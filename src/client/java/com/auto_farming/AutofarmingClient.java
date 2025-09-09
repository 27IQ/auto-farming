package com.auto_farming;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto_farming.gui.BasicHUD;

public class AutofarmingClient implements ClientModInitializer {
	public static final String MOD_ID = "auto-farm";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
	
		BasicHUD.registerHUD();

		@SuppressWarnings("unused")
		AutoFarm autoFarm=new AutoFarm();

		LOGGER.info("auto-farm loaded successfully");
	}
}