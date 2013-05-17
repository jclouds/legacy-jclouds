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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Map;
import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volume Type features
 *
 * @author Adam Lowe
 * @see VolumeApi
 * @see VolumeTypeAsyncApi
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.volumetypes.html"/>
 * @see <a href="https://blueprints.launchpad.net/nova/+spec/volume-type"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUME_TYPES)
public interface VolumeTypeApi {

   /**
    * @return set of all volume types
    */
   FluentIterable<? extends VolumeType> list();

   /**
    * @param id the id of the volume type to retrieve
    * @return the requested volume type
    */
   VolumeType get(String id);

   /**
    * Creates a new volume type
    *
    * @param name    the name of the new volume type
    * @param options optional settings for the new volume type
    * @return the new volume type
    */
   VolumeType create(String name, CreateVolumeTypeOptions... options);

   /**
    * Deletes a volume type
    */
   boolean delete(String id);

   /**
    * @param id the id of the volume type
    * @return the set of extra metadata for the flavor
    */
   Map<String, String> getExtraSpecs(String id);

   /**
    * Creates or updates the extra metadata for a given flavor
    */
   boolean updateExtraSpecs(String id, Map<String, String> specs);

   /**
    * Retrieve a single extra spec value
    *
    * @param id  the id of the volume type
    * @param key the key of the extra spec item to retrieve
    */
   String getExtraSpec(String id, String key);

   /**
    * Creates or updates a single extra spec value
    *
    * @param id    the id of the volume type
    * @param key   the extra spec key (when creating ensure this does not include whitespace or other difficult characters)
    * @param value the new value to store associate with the key
    */
   boolean updateExtraSpec(String id, String key, String value);

   /**
    * Deletes an existing extra spec
    *
    * @param id  the id of the volume type
    * @param key the key of the extra spec to delete
    */
   boolean deleteExtraSpec(String id, String key);
}
