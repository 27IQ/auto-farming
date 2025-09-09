package com.auto_farming;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.RIGHT;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.actionwrapper.Directions;
import com.auto_farming.gui.BasicHUD;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class AutofarmingClient implements ClientModInitializer {
	public static final String MOD_ID = "auto-farm";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Thread farmThread=null;

	@Override
	public void onInitializeClient() {
	
		Actions.initActions();
		BasicHUD.registerHUD();
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		registerKeybinds();

		LOGGER.info("auto-farm loaded successfully");
	}

	private void registerKeybinds(){
        KeyBinding start_left = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auto-farming.start_left", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "category.auto-farming"
        ));

		KeyBinding start_right = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auto-farming.start_right", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.auto-farming"
        ));

		KeyBinding pause_toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auto-farming.pause_toggle", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.auto-farming"
        ));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (start_left.wasPressed()) {
				task_helper(LEFT);
            }
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (start_right.wasPressed()) {
				task_helper(RIGHT);
			}
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (pause_toggle.wasPressed()) {
				AutoFarm.pause_toggle();
			}
		});   
	}

	public void task_helper(Directions direction){
		
		if(farmThread!=null){
			AutoFarm.is_active=false;
			farmThread=null;
			return;
		}

		Runnable start_task=()->{
			AutoFarm.run_farm(direction);
		};

		farmThread=new Thread(start_task);
		farmThread.setDaemon(false);
		farmThread.start();
	}
}