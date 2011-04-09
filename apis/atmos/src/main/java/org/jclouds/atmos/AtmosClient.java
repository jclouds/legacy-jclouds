/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmos;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.options.GetOptions;

import com.google.inject.Provides;

/**
 * Provides access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see AtmosAsyncClient
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface AtmosClient {
   /**
    * Creates a default implementation of AtmosObject
    */
   @Provides
   AtmosObject newObject();

   BoundedSet<? extends DirectoryEntry> listDirectories(ListOptions... options);

   BoundedSet<? extends DirectoryEntry> listDirectory(String directoryName, ListOptions... options);

   URI createDirectory(String directoryName);

   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   URI createFile(String parent, AtmosObject object);

   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void updateFile(String parent, AtmosObject object);

   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   AtmosObject readFile(String path, GetOptions... options);

   AtmosObject headFile(String path);

   SystemMetadata getSystemMetadata(String path);

   UserMetadata getUserMetadata(String path);

   void deletePath(String path);

   boolean pathExists(String path);

}
