/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.cloudsigma.domain.Drive;
import org.jclouds.cloudsigma.domain.DriveData;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;

/**
 * Provides synchronous access to CloudSigma.
 * <p/>
 * 
 * @see CloudSigmaAsyncClient
 * @see <a href="TODO: insert URL of cloudsigma documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface CloudSigmaClient  {
   /**
    * list of server uuids in your account
    * 
    * @return or empty set if no servers are found
    */
   Set<String> listServers();

   /**
    * Get all servers info
    * 
    * @return or empty set if no servers are found
    */
   Set<? extends ServerInfo> listServerInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   ServerInfo getServerInfo(String uuid);

   /**
    * create a new server
    * 
    * @param server
    * @return newly created server
    */
   ServerInfo createServer(Server server);

   /**
    * set server configuration
    * 
    * @param uuid
    *           what server to change
    * @param serverData
    *           what values to change
    * @return new data
    */
   ServerInfo setServerConfiguration(String uuid, Server server);

   /**
    * Destroy a server
    * 
    * @param uuid
    *           what to destroy
    */
   void destroyServer(String uuid);

   /**
    * Start a server
    * 
    * @param uuid
    *           what to start
    */
   void startServer(String uuid);

   /**
    * Stop a server
    * <p/>
    * Kills the server immediately, equivalent to a power failure. Server reverts to a stopped
    * status if it is persistent and is automatically destroyed otherwise.
    * 
    * @param uuid
    *           what to stop
    */
   void stopServer(String uuid);

   /**
    * Shutdown a server
    * <p/>
    * Sends the server an ACPI power-down event. Server reverts to a stopped status if it is
    * persistent and is automatically destroyed otherwise.
    * <h4>note</h4> behaviour on shutdown depends on how your server OS is set up to respond to an
    * ACPI power button signal.
    * 
    * @param uuid
    *           what to shutdown
    */
   void shutdownServer(String uuid);

   /**
    * Reset a server
    * 
    * @param uuid
    *           what to reset
    */
   void resetServer(String uuid);

   /**
    * list of drive uuids in your account
    * 
    * @return or empty set if no drives are found
    */
   Set<String> listDrives();

   /**
    * Get all drives info
    * 
    * @return or empty set if no drives are found
    */
   Set<? extends DriveInfo> listDriveInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   DriveInfo getDriveInfo(String uuid);

   /**
    * create a new drive
    * 
    * @param createDrive
    *           required parameters: name, size
    * @return newly created drive
    */
   DriveInfo createDrive(Drive createDrive);

   /**
    * set extra drive data
    * 
    * @param uuid
    *           what drive to change
    * @param driveData
    *           what values to change
    * @return new data
    */
   DriveInfo setDriveData(String uuid, DriveData driveData);

   /**
    * Destroy a drive
    * 
    * @param uuid
    *           what to delete
    */
   void destroyDrive(String uuid);

   /**
    * list of drive uuids that are in the library
    * 
    * @return or empty set if no drives are found
    */
   Set<String> listStandardDrives();

   /**
    * list of cd uuids that are in the library
    * 
    * @return or empty set if no cds are found
    */
   Set<String> listStandardCds();

   /**
    * list of image uuids that are in the library
    * 
    * @return or empty set if no images are found
    */
   Set<String> listStandardImages();

   /**
    * Clone an existing drive. By default, the size is the same as the source
    * 
    * @param sourceUuid
    *           source to clone
    * @param newName
    *           name of the resulting drive
    * @param options
    *           options to control size
    * @return new drive
    */
   DriveInfo cloneDrive(String sourceUuid, String newName, CloneDriveOptions... options);


}
