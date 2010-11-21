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

package org.jclouds.elastichosts.binders;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.elastichosts.options.ReadDriveOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindReadDriveOptionsToPath implements Binder {
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public BindReadDriveOptionsToPath(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = uriBuilderProvider;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      checkArgument(payload instanceof ReadDriveOptions, "this binder is only valid for ReadDriveOptions!");
       ReadDriveOptions options = ReadDriveOptions.class.cast(payload);
       if (options.getOffset() != null || options.getSize() != null){
          UriBuilder builder = uriBuilderProvider.get().uri(request.getEndpoint());
          if (options.getOffset() != null)
             builder.path("/"+options.getOffset());
          if (options.getSize() != null)
             builder.path("/"+options.getSize());
          request.setEndpoint(builder.build());
       }
          
   }
}
