==========================================================================
Raspberry Pi - Sunrise / Sunset Power Controller
==========================================================================

## PROJECT INFORMATION

 Project website: 
 http://www.savagehomeautomation.com/projects/raspberry-pi-sunrise-sunset-timer-for-christmas-lights.html 
 
 Release builds are available on GitHub: 
 https://github.com/savagehomeautomation/raspi-sspc/downloads

 Copyright (C) 2012 Robert Savage (www.savagehomeautomation.com)

## LICENSE
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

## IN DEVELOPMENT 

2014-11-27 :: 1.0.1-SNAPSHOT
 
    *  Updated to Pi4J current version (1.0.0-SNAPSHOT)
    *  Updated to use Pi4J PowerController interface instead of raw GPIO pin.
    *  Updated dependency versions
    *  Removed Github upload plugin; no longer supported
    *  Updated POM to work with Maven v3.x. 
    *  Added .DEB installer package
    *  Added Debian init.d service scripts
    *  Fixed #1: end of month issue


## RELEASES

2012-12-04 :: 1.0.0
 
    *  Initial release


## INSTALLATION PREREQUISITES

    *  A Java Virtual Machine must be installed.
    *  A JAVA_HOME environment variable must be defined.
    *  Apache Commons Daemon must be installed. \
       (http://commons.apache.org/proper/commons-daemon/jsvc.html)
       "sudo apt-get install jsvc"
    
 
 
## INSTALLATION INSTRUCTIONS

Download the sspc-{version}.deb file to your Raspberry Pi.
Use the following command to install the application:

    *  sudo dpkg -i sspc-{version}.deb
 
 
## UNINSTALL INSTALLATION INSTRUCTIONS

Use the following command to uninstall the application:

    *  sudo dpkg -r sspc

    
## CONFIGURATION

Use the following command to edit the application's configuration

    *  nano /opt/sspc/config/config.properties
    
Enter the correct longitude and latitude coordinates in the defined properties.
After saving the updated properties file, then use the following command to restart the application.

    *  service sspc restart

## RUNTIME

The following commands can be used to start, stop, debug, and restart the application service.

    *  service sspc start
    *  service sspc stop
    *  service sspc restart
    *  service sspc debug
