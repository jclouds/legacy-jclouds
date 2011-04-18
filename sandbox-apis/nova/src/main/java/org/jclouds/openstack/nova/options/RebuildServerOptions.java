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

package org.jclouds.openstack.nova.options;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 */
public class RebuildServerOptions extends BindToJsonPayload {
   String imageRef;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      Map<String, String> image = Maps.newHashMap();
      if (imageRef != null)
         image.put("imageRef", imageRef);
      return super.bindToRequest(request, ImmutableMap.of("rebuild", image));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("RebuildServer is a POST operation");
   }

   /**
    * @param ref - reference of the image to rebuild the server with.
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
