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
package org.jclouds.azure.management.features;

import java.util.Set;
import org.jclouds.azure.management.domain.Disk;

/**
 * The Service Management API includes operations for managing the disks in your subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
 * @see DiskAsyncApi
 * @author Gerald Pereira
 */
public interface DiskApi {

   /**
    * The List Disks operation retrieves a list of the disks in your image repository.
    */
   Set<Disk> list();

   /**
    * The Delete Disk operation deletes the specified data or operating system disk from your image
    * repository.
    * 
    * @return request id or null, if not found
    * 
    */
   String delete(String diskName);
}
