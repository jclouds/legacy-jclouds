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
package org.jclouds.hpcloud.objectstorage.blobstore.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApi;
import org.jclouds.hpcloud.objectstorage.extensions.CDNContainerApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EnableCDNAndCache implements Function<String, URI> {
   private final LoadingCache<String, URI> cdnContainer;
   private final HPCloudObjectStorageApi sync;

   @Inject
   public EnableCDNAndCache(HPCloudObjectStorageApi sync, LoadingCache<String, URI> cdnContainer) {
      this.sync = sync;
      this.cdnContainer = cdnContainer;
   }

   @Override
   public URI apply(String input) {
      Optional<CDNContainerApi> cdnExtension = sync.getCDNExtension();
      checkArgument(cdnExtension.isPresent(), "CDN is required, but the extension is not available!");
      URI uri = cdnExtension.get().enable(input);
      cdnContainer.put(input, uri);
      return uri;
   }

}
