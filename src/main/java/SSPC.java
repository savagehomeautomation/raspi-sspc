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
public class SSPC
{
    public static void main(String[] args)
    {
        // create controller instance and start it up
        SunriseSunsetPowerController sspc = new SunriseSunsetPowerController();
        sspc.start(args);
    }
}
