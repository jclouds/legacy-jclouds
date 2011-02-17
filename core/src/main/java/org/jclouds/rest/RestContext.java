/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.rest;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.Beta;
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
@ImplementedBy(RestContextImpl.class)
public interface RestContext<S, A> extends Location {

   /**
    * low-level api to the cloud. Threadsafe implementations will return a singleton.
    * 
    * @return a connection to the cloud where all methods return {@link Future}s
    */
   A getAsyncApi();

   /**
    * reflects a tuned connection to the cloud which calls {@link #getAsyncApi()} with predetermined
    * timeouts.
    * 
    * @return a connection to the cloud where all methods block
    */
   S getApi();

   URI getEndpoint();

   String getApiVersion();

   String getIdentity();

   /**
    * retrieves a list of credentials for resources created within this context, keyed on {@code id}
    * of the resource. We are testing this approach for resources such as compute nodes, where you
    * could access this externally.
    * 
    */
   @Beta
   Map<String, Credentials> getCredentialStore();

   @Beta
   Map<String, Credentials> credentialStore();

   Utils getUtils();

   /**
    * @see #getUtils
    */
   Utils utils();

   /**
    * Closes all connections to Cloud Files.
    */
   void close();

}