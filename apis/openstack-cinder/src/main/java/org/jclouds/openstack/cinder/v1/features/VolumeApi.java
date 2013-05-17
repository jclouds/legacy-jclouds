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
package org.jclouds.openstack.cinder.v1.features;

import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volumes.
 * 
 * This API strictly handles creating and managing Volumes. To attach a Volume to a Server you need to use the
 * @see VolumeAttachmentApi
 * 
 * @see VolumeAsyncApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
public interface VolumeApi {
   /**
    * Returns a summary list of Volumes.
    *
    * @return The list of Volumes
    */
   FluentIterable<? extends Volume> list();

   /**
    * Returns a detailed list of Volumes.
    *
    * @return The list of Volumes
    */
   FluentIterable<? extends Volume> listInDetail();

   /**
    * Return data about the given Volume.
    *
    * @param volumeId Id of the Volume
    * @return Details of a specific Volume
    */
   Volume get(String volumeId);

   /**
    * Creates a new Volume
    * 
    * @param volumeId Id of the Volume
    * @param options See CreateVolumeOptions
    * @return The new Volume
    */
   Volume create(int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a Volume. The Volume status must be Available or Error.
    *
    * @param volumeId Id of the Volume
    * @return true if successful, false otherwise
    */
   boolean delete(String volumeId);
}
