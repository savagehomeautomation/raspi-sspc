package com.savagehomeautomation.raspi.sspc;
/*
 * **********************************************************************
 * ORGANIZATION  :  savagehomeautomation.com
 * PROJECT       :  Sunrise/Sunset Power Controller 
 * FILENAME      :  EventType.java  
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
 * This enumeration defined the type of scheduled events being managed. 
 * 
 * @author Robert Savage
 *
 */
public enum EventType
{
    SunriseToday,
    SunsetToday,
    SunriseTomorrow
}
