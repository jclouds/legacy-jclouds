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
package org.jclouds.hpcloud.object.storage;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.hpcloud.object.storage.domain.ContainerCDNMetadata;
import org.jclouds.hpcloud.object.storage.options.ListCDNContainerOptions;
import org.jclouds.openstack.swift.CommonSwiftClient;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to HP Cloud Object Storage via the REST API.
 * 
 * <p/>All commands return a ListenableFuture of the result. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see HPCloudObjectStorageClient
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/object-storage/api">HP Cloud Object Storage API</a>
 * @author Jeremy Daggett
 */
@Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
public interface HPCloudObjectStorageClient extends CommonSwiftClient {
	
   Set<ContainerCDNMetadata> listCDNContainers(ListCDNContainerOptions... options);

   ContainerCDNMetadata getCDNMetadata(String container);

   URI enableCDN(String container, long ttl);

   URI enableCDN(String container);

   URI updateCDN(String container, long ttl);

   boolean disableCDN(String container);
   
   /*boolean isContainerPublic(String container);
   
   boolean setContainerACL(String container);
   */
}
