/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.atmos;

import java.io.Closeable;
import java.net.URI;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
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
public interface AtmosClient extends Closeable {
   /**
    * Creates a default implementation of AtmosObject
    */
   @Provides
   AtmosObject newObject();

   BoundedSet<? extends DirectoryEntry> listDirectories(ListOptions... options);

   BoundedSet<? extends DirectoryEntry> listDirectory(String directoryName, ListOptions... options);

   URI createDirectory(String directoryName, PutOptions... options);

   URI createFile(String parent, AtmosObject object, PutOptions... options);

   void updateFile(String parent, AtmosObject object, PutOptions... options);

   AtmosObject readFile(String path, GetOptions... options);

   AtmosObject headFile(String path);

   SystemMetadata getSystemMetadata(String path);

   UserMetadata getUserMetadata(String path);

   void deletePath(String path);

   boolean pathExists(String path);

   boolean isPublic(String path);

}
