package com.ru.tgra.graphics;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;


/**
 * Schedule a task that executes once every second.
 */

public class BlinkingLights {
    Toolkit toolkit;
    Timer timer;
    boolean on;

    public BlinkingLights() {
	toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        on = false;
        timer.schedule(new RemindTask(),
	               0,        //initial delay
	               1*90);  //subsequent rate
    }

    class RemindTask extends TimerTask {

        public void run() {
        	if (on == true) {
        		//Lights
        		//System.out.format("Red");
        		on = false;
        	} else {
        		//Lights 
                //System.out.format("Blue");
                on = true;
        	}
        }
    }

	public boolean getBlueOrRed() {
		if (on == true){
			return true;
		}
		else {
			return false;
		}
	}
	
}