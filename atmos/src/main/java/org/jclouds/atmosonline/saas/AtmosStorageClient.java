/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.options.GetOptions;

/**
 * Provides access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see AtmosStorageAsyncClient
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
@Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
public interface AtmosStorageClient {

   AtmosObject newObject();

   BoundedSet<? extends DirectoryEntry> listDirectories(ListOptions... options);

   BoundedSet<? extends DirectoryEntry> listDirectory(String directoryName,
            ListOptions... options);

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

   /**
    * 
    * @param path
    * @return
    * @throws AtmosStorageResponseException
    *            , if the path is a directory and not empty
    */
   void deletePath(String path);

   boolean pathExists(String path);

   // signature currently doesn't work
   // @POST
   // @QueryParams(keys = "acl")
   // @Headers(keys = { "x-emc-useracl", "x-emc-groupacl" }, values = { "root=FULL_CONTROL",
   // "other=READ" })
   // @Consumes(MediaType.WILDCARD)
   // void makePublic(@Endpoint URI url);

}
