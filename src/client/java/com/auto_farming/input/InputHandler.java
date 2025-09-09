package com.auto_farming.input;

import com.auto_farming.AutoFarm;
import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Directions;

import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.RIGHT;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.input.Bindings.START_LEFT;
import static com.auto_farming.input.Bindings.START_RIGHT;
import static com.auto_farming.input.Bindings.AUTO_SET_UP;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class InputHandler {

    public static Thread farmThread=null;

    public static void registerKeybinds(){

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (START_LEFT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed "+START_LEFT.toString());
				task_helper(LEFT);
            }
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (START_RIGHT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed "+START_RIGHT.toString());
				task_helper(RIGHT);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (PAUSE_TOGGLE.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed "+PAUSE_TOGGLE.toString());
				AutoFarm.pause_toggle();
			}
		});   

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AUTO_SET_UP.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed "+AUTO_SET_UP.toString());
				Runnable set_up_task=()->{
					AutoFarm.auto_set_up();
				};
				
				Thread set_up_thread=new Thread(set_up_task);
				set_up_thread.setDaemon(false);
				set_up_thread.start();
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
