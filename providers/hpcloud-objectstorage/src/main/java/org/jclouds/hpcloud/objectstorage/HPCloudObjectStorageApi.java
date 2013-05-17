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
package org.jclouds.hpcloud.objectstorage;

import java.util.Set;
import org.jclouds.hpcloud.objectstorage.extensions.CDNContainerApi;
import org.jclouds.location.Region;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.rest.annotations.Delegate;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides synchronous access to HP Cloud Object Storage via the REST API.
 * 
 * <p/>
 * All commands return a ListenableFuture of the result. Any exceptions incurred during processing
 * will be backend in an {@link java.util.concurrent.ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageAsyncApi
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object
 *      Storage API</a>
 * @author Jeremy Daggett
 */
public interface HPCloudObjectStorageApi extends CommonSwiftClient {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides synchronous access to CDN features.
    */
   @Delegate
   Optional<CDNContainerApi> getCDNExtension();
}
