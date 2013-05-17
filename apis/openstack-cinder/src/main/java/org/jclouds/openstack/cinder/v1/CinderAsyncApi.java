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
package org.jclouds.openstack.cinder.v1;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.cinder.v1.features.SnapshotAsyncApi;
import org.jclouds.openstack.cinder.v1.features.VolumeAsyncApi;
import org.jclouds.openstack.cinder.v1.features.VolumeTypeAsyncApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to Cinder via its REST API.
 * 
 * @see CinderApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CinderApi.class)} as
 *             {@link CinderAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CinderAsyncApi extends Closeable {
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides asynchronous access to Extension features.
    */
   @Delegate
   ExtensionAsyncApi getExtensionApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Volume features.
    */
   @Delegate
   VolumeAsyncApi getVolumeApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to VolumeType features.
    */
   @Delegate
   VolumeTypeAsyncApi getVolumeTypeApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Snapshot features.
    */
   @Delegate
   SnapshotAsyncApi getSnapshotApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
