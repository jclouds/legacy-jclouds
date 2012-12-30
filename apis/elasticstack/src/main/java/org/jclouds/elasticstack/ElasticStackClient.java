/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.elasticstack;

import java.util.Set;
import org.jclouds.elasticstack.domain.Drive;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.io.Payload;

/**
 * Provides synchronous access to elasticstack.
 * <p/>
 * 
 * @see ElasticStackAsyncClient
 * @see <a href="TODO: insert URL of elasticstack documentation" />
 * @author Adrian Cole
 */
public interface ElasticStackClient {
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
    * create and start a new server
    * 
    * @param server
    * @return newly created server
    */
   ServerInfo createAndStartServer(Server server);

   /**
    * Image a drive from another drive. The actual imaging process is asynchronous, with progress
    * reported via drive info.
    * 
    * @param source
    *           drive to copy from
    * @param destination
    *           drive to copy to
    */
   void imageDrive(String source, String destination);

   /**
    * @see #imageDrive(String, String)
    * @param conversionType
    *           Supports 'gzip' or 'gunzip' conversions.
    */
   void imageDrive(String source, String destination, ImageConversionType conversionType);

   /**
    * Read binary data from a drive
    * 
    * @param uuid
    *           drive to read
    * @param offset
    *           start at the specified offset in bytes
    * @param size
    *           the specified size in bytes; must be <=4096k
    * @return binary content of the drive.
    */
   Payload readDrive(String uuid, long offset, long size);

   /**
    * Write binary data to a drive
    * 
    * @param uuid
    *           drive to write
    * @param content
    *           what to write.
    *           <ul>
    *           <li>Binary data (Content-Type: application/octet-stream)</li>
    *           <li>Supports raw data or Content-Encoding: gzip</li>
    *           <li>Does not support Transfer-Encoding: chunked</li>
    *           </ul>
    */
   void writeDrive(String uuid, Payload content);

   /**
    * @see ElasticStackClient#writeDrive(String, Payload)
    * @param offset
    *           the byte offset in the target drive at which to start writing, not an offset in the
    *           input stream.
    */
   void writeDrive(String uuid, Payload content, long offset);

}
