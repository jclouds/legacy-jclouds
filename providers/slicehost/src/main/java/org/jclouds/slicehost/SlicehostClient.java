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
package org.jclouds.slicehost;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.slicehost.domain.Backup;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Image;
import org.jclouds.slicehost.domain.Slice;

/**
 * Provides access to Slicehost via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Slicehost. Any exceptions incurred
 * during processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see SlicehostAsyncClient
 * @see <a href="http://www.slicehost.com/docs/Slicehost_API.pdf" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface SlicehostClient {
   Set<Slice> listSlices();

   Slice getSlice(int id);

   Void destroySlice(int id);

   Void rebootSlice(int id);

   Void hardRebootSlice(int id);

   Slice createSlice(String name, int imageId, int flavorId);

   Void rebuildSliceFromImage(int id, int imageId);

   Void rebuildSliceFromBackup(int id, int imageId);

   Set<Flavor> listFlavors();

   Flavor getFlavor(int id);

   Set<Image> listImages();

   Image getImage(int id);

   Set<Backup> listBackups();

   Backup getBackup(int id);

}
