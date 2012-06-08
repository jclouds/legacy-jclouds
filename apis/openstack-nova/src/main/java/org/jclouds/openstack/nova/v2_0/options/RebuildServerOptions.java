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
package org.jclouds.openstack.nova.v2_0.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class RebuildServerOptions implements MapBinder {
   @Inject
   private BindToJsonPayload jsonBinder;
   String imageRef;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, String> image = Maps.newHashMap();
      if (imageRef != null)
         image.put("imageRef", imageRef);
      return jsonBinder.bindToRequest(request, ImmutableMap.of("rebuild", image));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("RebuildServer is a POST operation");
   }

   /**
    * @param ref
    *           - reference of the image to rebuild the server with.
    */
   public RebuildServerOptions withImage(String ref) {
      checkNotNull(ref, "image reference should not be null");
      checkArgument(!ref.isEmpty(), "image reference should not be empty");
      this.imageRef = ref;
      return this;
   }

   public static class Builder {

      /**
       * @see RebuildServerOptions#withImage(String)
       */
      public static RebuildServerOptions withImage(String ref) {
         RebuildServerOptions options = new RebuildServerOptions();
         return options.withImage(ref);
      }
   }
}
