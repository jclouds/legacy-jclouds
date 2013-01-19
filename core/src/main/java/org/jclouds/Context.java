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
package org.jclouds;

import java.io.Closeable;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.domain.Location;
import org.jclouds.internal.ContextImpl;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.Utils;

import com.google.inject.ImplementedBy;

/**
 * Represents an authenticated context to the cloud.
 * 
 * <h2>Note</h2> Please issue {@link #close()} when you are finished with this context in order to
 * release resources.
 * 
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(ContextImpl.class)
public interface Context extends Location, Closeable {

  /**
   * Identifies the Context. This is a unique name optionally specified by the user and safe to index on.
   * The purpose of this property is to provide means to distinct between multiple contexts, without having to check
   * multiple properties or have explicit knowledge of how the context was created.
   * @return
   */
   String getName();

   /**
    * @return the providerMetadata used to create this context
    * @see ContextBuilder#newBuilder(org.jclouds.providers.ProviderMetadata)
    */
   ProviderMetadata getProviderMetadata();

   /**
    * @return the current login user, access key, email, or whatever the 'identity' field was building the context.
    * @see ApiMetadata#getDefaultIdentity
    */
   String getIdentity();

   Utils getUtils();

   /**
    * @see #getUtils
    */
   Utils utils();

   /**
    * Closes all connections, including executor service
    */
   @Override
   void close();

}
