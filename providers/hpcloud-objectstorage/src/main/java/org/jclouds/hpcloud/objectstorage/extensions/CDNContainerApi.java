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
package org.jclouds.hpcloud.objectstorage.extensions;

import java.net.URI;
import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to HP Cloud Object Storage via the REST API.
 * 
 * <p/>
 * All commands return a ListenableFuture of the result. Any exceptions incurred during processing
 * will be backend in an {@link java.util.concurrent.ExecutionException} as documented in {@link ListenableFuture#get()}.
 * 
 * @see org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApi
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api">HP Cloud Object
 *      Storage API</a>
 * @see CDNContainerAsyncApi
 * @author Jeremy Daggett
 */
@Beta
public interface CDNContainerApi  {
   
   FluentIterable<CDNContainer> list();
   
   FluentIterable<CDNContainer> list(ListCDNContainerOptions options);

   CDNContainer get(String container);

   URI enable(String container, long ttl);

   URI enable(String container);

   URI update(String container, long ttl);
   
   boolean disable(String container);

}
