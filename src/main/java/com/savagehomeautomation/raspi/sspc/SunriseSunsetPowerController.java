package com.savagehomeautomation.raspi.sspc;
/*
 * **********************************************************************
 * ORGANIZATION  :  savagehomeautomation.com
 * PROJECT       :  Sunrise/Sunset Power Controller 
 * FILENAME      :  SunriseSunsetPowerController.java  
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
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.pi4j.device.power.PowerController;
import com.savagehomeautomation.utility.SunriseSunset;

/**
 * This class is the implementation of the sunrise/sunset  
 * power controller for use to automate power states on
 * a PowerSwitch Tail device connected to a Raspberry Pi.
 *  
 * @author Robert Savage
 * @see http://www.savagehomeautomation.com/projects/raspberry-pi-sunrise-sunset-timer-for-christmas-lights.html
 */
public class SunriseSunsetPowerController
{
    // internal class members
    private Timer timer;
    private SunriseSunset ss; 
    private Double latitude;
    private Double longitude;
    private EventType nextEvent;
    private Date nextSunriseDate; 
    private Date nextSunsetDate;
    private PowerController powerController;
    
    public SunriseSunsetPowerController(PowerController powerController)
    {
        this.powerController = powerController;
    }
    
    /**
     * Start the controller.
     * Run the main program loop until user exits.
     * 
     * @param args command line arguments
     */
    public void start(String[] args)
    {
        // display welcome screen
        displayWelcome();
        
        // attempt to read command line arguments
        for(String arg : args)
        {
            if(arg.startsWith("-longitude="))
            {
                try
                {
                    longitude = Double.parseDouble(arg.substring(11));
                    System.out.println("LONGITUDE = " + longitude);
                }
                catch(Exception ex){}
            }
            else if(arg.startsWith("-latitude="))
            {
                try
                {
                    latitude = Double.parseDouble(arg.substring(10));
                    System.out.println("LATITUDE  = " + latitude);
                }
                catch(Exception ex){}
            }
        }

        // prompt user for latitude if needed
        if(latitude == null)
            promptForLatitude();
        
        // prompt user for longitude if needed
        if(longitude == null)
            promptForLongitude();

        // display welcome user options menu
        displayMenuOptions();

        // create timer, GPIO controller, and sunrise/sunset calculator
        timer = new Timer();        
        ss = new SunriseSunset();
        
        // schedule starting event; apply initial power controller state
        switch(scheduleNextEvent())
        {
            case SunriseToday:
            {
                // if the next event is sunrise, then turn power ON
                powerController.on();
                break;
            }
            case SunsetToday:
            {
                // if the next event is sunset, then turn power OFF
                powerController.off();
                break;
            }
            case SunriseTomorrow:
            {
                // if the next event is sunrise, then turn power ON
                powerController.on();
                break;
            }
        }

        // main program loop; 
        // process user input or wait for user to abort with CTRL-C
        for(;;)
        {
            String command = System.console().readLine();
            if(command.equalsIgnoreCase("on"))
            {
                // turn ON power
                powerController.on();
                
                System.out.println("---------------------------------");
                System.out.println("[OVERRIDE] POWER STATE ON");
                System.out.println("---------------------------------");

            }
            else if(command.equalsIgnoreCase("off"))
            {
                // turn OFF power
                powerController.off();
                
                System.out.println("---------------------------------");
                System.out.println("[OVERRIDE] POWER STATE OFF");
                System.out.println("---------------------------------");
            }
            else if(command.equalsIgnoreCase("status"))
            {
                // determine and display current power controller state
                if(powerController.isOn())
                {
                    System.out.println("---------------------------------");
                    System.out.println("[STATUS] POWER STATE IS : ON");
                    System.out.println("---------------------------------");
                }
                else
                {
                    System.out.println("---------------------------------");
                    System.out.println("[STATUS] POWER STATE IS : OFF");
                    System.out.println("---------------------------------");
                }
            }
            else if(command.equalsIgnoreCase("time"))
            {
                // display current date/time
                System.out.println("---------------------------------");
                System.out.println("[CURRENT TIME] ");
                System.out.println(new Date());
                System.out.println("---------------------------------");
            }
            else if(command.equalsIgnoreCase("sunrise"))
            {
                // display sunrise date/time
                System.out.println("---------------------------------");
                System.out.println("[NEXT SUNRISE] ");
                System.out.println(" @ " + nextSunriseDate);
                System.out.println("---------------------------------");
            }
            else if(command.equalsIgnoreCase("sunset"))
            {
                // display sunset date/time
                System.out.println("---------------------------------");
                System.out.println("[NEXT SUNSET] ");
                System.out.println(" @ " + nextSunsetDate);
                System.out.println("---------------------------------");
            }
            else if(command.equalsIgnoreCase("coord"))
            {
                System.out.println("---------------------------------");
                System.out.println("[LONGITUDE] = " + longitude);
                System.out.println("[LATITUDE]  = " + latitude);
                System.out.println("---------------------------------");
            }
            else if(command.equalsIgnoreCase("next"))
            {
                // display next scheduled event
                switch(nextEvent)
                {
                    case SunriseToday:
                    {
                        System.out.println("-----------------------------------");
                        System.out.println("[NEXT EVENT] SUNRISE TODAY ");
                        System.out.println("  @ " + nextSunriseDate);
                        System.out.println("-----------------------------------");
                        break;
                    }
                    case SunsetToday:
                    {
                        System.out.println("-----------------------------------");
                        System.out.println("[NEXT EVENT] SUNSET TODAY");
                        System.out.println("  @ " + nextSunsetDate);
                        System.out.println("-----------------------------------");
                        break;
                    }
                    case SunriseTomorrow:
                    {
                        System.out.println("-----------------------------------");
                        System.out.println("[NEXT EVENT] SUNRISE TOMORROW");
                        System.out.println("  @ " + nextSunriseDate);
                        System.out.println("-----------------------------------");
                        break;
                    }
                }                
            }
            else if(command.equalsIgnoreCase("help"))
            {
                // display user options menu
                displayMenuOptions();                
            }            
            else 
            {
                // un-handled command
                System.out.println("---------------------------------");
                System.out.println("[INVALID COMMAND ENTRY]");
                System.out.println("---------------------------------");
            }
        }
    }

    /**
     * This method will print the program's welcome message 
     * on the Raspberry Pi's console screen.
     */
    private void displayWelcome()
    {
        // display user options menu
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("----------------------------------------------------");
        System.out.println("     Welcome to Sunrise/Sunset Power Controller     ");
        System.out.println("         http://www.savagehomeautomation.com       ");
        System.out.println("----------------------------------------------------");
        System.out.println("");
    }    

    /**
     * This method will prompt the user for the location's longitude 
     * value via the Raspberry Pi's console screen.
     */
    private void promptForLongitude()
    {
        boolean success = false;
        
        while(!success)
        try
        {                    
            // display user prompt
            System.out.println("");
            System.out.println("Please enter the longitude (in degrees) for your location:");
            String longitudeString = System.console().readLine();
            longitude = Double.parseDouble(longitudeString);
            success = true;
        }
        catch(Exception ex)
        {
            System.err.println("***************************************************");
            System.err.println("[ERROR] Invalid longitude entry.  Please try again.");
            System.err.println("***************************************************");
            success = false;
        }
    }    

    /**
     * This method will prompt the user for the location's latitude 
     * value via the Raspberry Pi's console screen.
     */
    private void promptForLatitude()
    {
        boolean success = false;
        
        while(!success)
        try
        {        
            // display user prompt
            System.out.println("");
            System.out.println("Please enter the latitude (in degrees) for your location:");
            String latitudeString = System.console().readLine();
            latitude = Double.parseDouble(latitudeString);
            success = true;
        }
        catch(Exception ex)
        {
            System.err.println("***************************************************");
            System.err.println("[ERROR] Invalid latitude entry.  Please try again.");
            System.err.println("***************************************************");
            success = false;
        }
    }    
    
    /**
     * This method will print the program menu option 
     * on the Raspberry Pi's console screen.
     */
    private void displayMenuOptions()
    {
        // display user options menu
        System.out.println("");
        System.out.println("----------------------------------------------------");
        System.out.println("");
        System.out.println("COMMAND OPTIONS:");
        System.out.println("");
        System.out.println("  'on'      to force power controller to ON state");
        System.out.println("  'off'     to force power controller to OFF state");
        System.out.println("  'status'  to see current power controller state");
        System.out.println("  'sunrise' to display sunrise time.");
        System.out.println("  'sunset'  to display sunset time.");
        System.out.println("  'next'    to display next scheduled event.");
        System.out.println("  'time'    to display current time.");
        System.out.println("  'coord'   to display longitude and latitude.");
        System.out.println("  'help'    to display this menu.");
        System.out.println("");
        System.out.println("PRESS 'CTRL-C' TO TERMINATE");
        System.out.println("");
        System.out.println("----------------------------------------------------");
        System.out.println("");
    }
    

    private synchronized EventType scheduleNextEvent()
    {
        // get sunrise and sunset time for today
        Date today = new Date();
        Date today_sunrise = ss.getSunrise(latitude, longitude);
        Date today_sunset = ss.getSunset(latitude, longitude);  

        // get sunrise and sunset time for tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.roll(Calendar.DATE, true);
        Date tomorrow_sunrise = ss.getSunrise(latitude, longitude, tomorrow.getTime());
        Date tomorrow_sunset = ss.getSunset(latitude, longitude, tomorrow.getTime());

        // determine if sunrise or sunset is the next event
        if(today.after(today_sunset))
        {
            // get tomorrow's date time
            System.out.println("-----------------------------------");
            System.out.println("[SCHEDULED] NEXT EVENT: SUNRISE    ");
            System.out.println("  @ " + tomorrow_sunrise);
            System.out.println("-----------------------------------");
            
            // schedule tomorrow's sunrise as next event
            timer.schedule(new SunriseTask(), tomorrow_sunrise);
            
            // set cache next sunrise and sunset variables
            nextSunriseDate = tomorrow_sunrise;
            nextSunsetDate = tomorrow_sunset;

            // return next event
            nextEvent = EventType.SunriseTomorrow;
            return nextEvent;
        }
        else if(today.after(today_sunrise))
        {
            System.out.println("-----------------------------------");
            System.out.println("[SCHEDULED] NEXT EVENT: SUNSET     ");
            System.out.println("  @ " + today_sunset);
            System.out.println("-----------------------------------");
            
            // schedule sunset as next event
            timer.schedule(new SunsetTask(), today_sunset);

            // set cache next sunrise and sunset variables
            nextSunriseDate = tomorrow_sunrise;
            nextSunsetDate = today_sunset;
            
            // return next event
            nextEvent = EventType.SunsetToday;
            return nextEvent;            
        }
        else
        {
            System.out.println("-----------------------------------");
            System.out.println("[SCHEDULED] NEXT EVENT: SUNRISE    ");
            System.out.println("  @ " + today_sunrise);
            System.out.println("-----------------------------------");
            
            // schedule sunrise as next event
            timer.schedule(new SunriseTask(), today_sunrise);

            // set cache next sunrise and sunset variables
            nextSunriseDate = today_sunrise;
            nextSunsetDate = today_sunset;
            
            // return next event
            nextEvent = EventType.SunriseToday;
            return nextEvent;            
        }
    }
    
    /**
     * This class is invoked as a callback at sunrise time and it  
     * turns on the attached power controller to the Raspberry Pi 
     * using the Pi4J API.
     * 
     * @author Robert Savage
     */
    private class SunriseTask extends TimerTask
    {
        @Override
        public void run()
        {
            // turn OFF power
            powerController.off();
            
            System.out.println("-----------------------------------");
            System.out.println("[SUNRISE] POWER HAS BEEN TURNED OFF");
            System.out.println("-----------------------------------");
            
            // schedule next event
            scheduleNextEvent();
        }
    }
    
    /**
     * This class is invoked as a callback at sunset time and it  
     * turns off the attached power controller to the Raspberry Pi 
     * using the Pi4J API.
     * 
     * @author Robert Savage
     */
    private class SunsetTask extends TimerTask
    {
        @Override
        public void run()
        {
            // turn ON power
            powerController.on();
            
            System.out.println("-----------------------------------");
            System.out.println("[SUNSET]  POWER HAS BEEN TURNED ON");
            System.out.println("-----------------------------------");
            
            // schedule next event
            scheduleNextEvent();            
        }
    }
    
    /**
     * This listener class is invoked as a callback when a state change
     * is detected on the override input switch (if implemented; optional)
     * 
     * @author Robert Savage
     */
//    private class OverrideSwitchListener implements GpioPinListenerDigital
//    {
//        @Override
//        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
//        {
//            if(event.getState().isHigh())
//            {
//                System.out.println("---------------------------------");
//                System.out.println("[OVERRIDE] POWER STATE TOGGLED");
//                System.out.println("---------------------------------");
//            }
//        }
//    }    
}

