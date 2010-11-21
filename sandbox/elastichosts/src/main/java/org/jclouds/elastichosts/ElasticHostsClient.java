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

package org.jclouds.elastichosts;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveInfo;

/**
 * Provides synchronous access to ElasticHosts.
 * <p/>
 * 
 * @see ElasticHostsAsyncClient
 * @see <a href="TODO: insert URL of ElasticHosts documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ElasticHostsClient {
   /**
    * list of drive uuids in your account
    * 
    * @return or empty set if no drives are found
    */
   Set<String> listDrives();

   /**
    * list of drive uuids that are in the library
    * 
    * @return or empty set if no drives are found
    */
   Set<String> listStandardDrives();

   /**
    * Get all drives info
    * 
    * @return or empty set if no drives are found
    */
   Set<DriveInfo> listDriveInfo();

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
   DriveInfo createDrive(CreateDriveRequest createDrive);

   /**
    * Destroy a drive
    * 
    * @param uuid
    *           what to delete
    */
   void destroyDrive(String uuid);

}
