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
package org.jclouds.hpcloud.objectstorage.lvs;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.hpcloud.objectstorage.lvs.domain.ContainerCDNMetadata;
import org.jclouds.hpcloud.objectstorage.lvs.options.CreateContainerOptions;
import org.jclouds.hpcloud.objectstorage.lvs.options.ListCDNContainerOptions;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to HP Cloud Object StorageLasVegas via the REST API.
 * 
 * <p/>
 * All commands return a ListenableFuture of the result. Any exceptions incurred during processing
 * will be wrapped in an {@link ExecutionException} as documented in {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageLasVegasClient
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object
 *      StorageLasVegas API</a>
 * @author Jeremy Daggett
 */
@Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
public interface HPCloudObjectStorageLasVegasClient extends CommonSwiftClient {

   boolean createContainer(String container, CreateContainerOptions... options);

   ContainerMetadata getContainerMetadata(String container);

   @Beta
   Set<ContainerCDNMetadata> listCDNContainers(ListCDNContainerOptions... options);

   @Beta
   ContainerCDNMetadata getCDNMetadata(String container);

   @Beta
   URI enableCDN(String container, long ttl);

   @Beta
   URI enableCDN(String container);

   @Beta
   URI updateCDN(String container, long ttl);
   
   @Beta
   boolean disableCDN(String container);

}
