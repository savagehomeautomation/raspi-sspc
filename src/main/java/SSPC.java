import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import com.pi4j.component.power.impl.GpioPowerComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.savagehomeautomation.raspi.sspc.SunriseSunsetPowerController;
/*
 * **********************************************************************
 * ORGANIZATION  :  savagehomeautomation.com
 * PROJECT       :  Sunrise/Sunset Power Controller 
 * FILENAME      :  SSPC.java  
 * 
 * More information about this project can be found here:  
 * http://www.savagehomeautomation.com/projects/raspberry-pi-sunrise-sunset-timer-for-christmas-lights.html
 * **********************************************************************
 * %%
 * Copyright (C) 2012 Robert Savage (www.savagehomeautoamtion.com)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * This class acts as a lightweight shim to 
 * launch the power controller implementation.
 *  
 * @author Robert Savage
 * @see http://www.savagehomeautomation.com/projects/raspberry-pi-sunrise-sunset-timer-for-christmas-lights.html
 */
public class SSPC implements Daemon
{
    // create controller instance and start it up
    private static SunriseSunsetPowerController sspc;
    private static GpioPowerComponent powerController;
    
    public static void main(String[] args) throws InterruptedException
    {
        initializeApplicationConfiguration();
        sspc.start(false);
    }
    
    private static void initializeApplicationConfiguration()
    {
        // create GPIO controller
        GpioController gpio  = GpioFactory.getInstance();

        // provision GPIO pins : 
        //   GPIO PIN #0 == OVERRIDE SWITCH
        //   GPIO PIN #1 == POWER CONTROLLER
        //GpioPinDigitalInput overrideSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "OverrideSwitch", PinPullResistance.PULL_DOWN);
        GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "PowerController");
        
        // force power controller to OFF if the program is shutdown
        outputPin.setShutdownOptions(true,PinState.LOW);
        
        // create a gpio toggle trigger on the override switch input pin; 
        // when the input is detected, toggle the power controller state
        //overrideSwitch.addTrigger(new GpioToggleStateTrigger(PinState.HIGH, powerController));
        
        // create a listener for the override switch
        //overrideSwitch.addListener(new OverrideSwitchListener());

        // create power controller device component
        powerController = new GpioPowerComponent(outputPin, PinState.HIGH, PinState.LOW);
        
        // create controller instance and start it up
        sspc = new SunriseSunsetPowerController(powerController);
    }

    @Override
    public void destroy()
    {
        sspc = null;
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception
    {
    }

    @Override
    public void start() throws Exception
    {
        initializeApplicationConfiguration();
        sspc.start(true);
    }

    @Override
    public void stop() throws Exception
    {
        sspc.stop();
        powerController.off();
    }
}
