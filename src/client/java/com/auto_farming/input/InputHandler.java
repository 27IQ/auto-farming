package com.auto_farming.input;

import com.auto_farming.AutoFarm;
import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Directions;

import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.RIGHT;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.input.Bindings.START_LEFT;
import static com.auto_farming.input.Bindings.START_RIGHT;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class InputHandler {

    public static Thread farmThread=null;

    public static void registerKeybinds(){

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (START_LEFT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed F6");
				task_helper(LEFT);
            }
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (START_RIGHT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed F7");
				task_helper(RIGHT);
			}
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (PAUSE_TOGGLE.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed F9");
				AutoFarm.pause_toggle();
			}
		});   
	}

	private static void task_helper(Directions direction){
		
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
