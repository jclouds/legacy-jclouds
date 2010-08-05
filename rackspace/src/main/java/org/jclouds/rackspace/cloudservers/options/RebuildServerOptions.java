/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class RebuildServerOptions extends BindToJsonPayload {
   Integer imageId;

   @Override
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, Integer> image = Maps.newHashMap();
      if (imageId != null)
         image.put("imageId", imageId);
      super.bindToRequest(request, ImmutableMap.of("rebuild", image));
   }

   @Override
   public void bindToRequest(HttpRequest request, Object toBind) {
      throw new IllegalStateException("RebuildServer is a POST operation");
   }

   /**
    * 
    * @param id
    *           of the image to rebuild the server with.
    */
   public RebuildServerOptions withImage(int id) {
      checkArgument(id > 0, "server id must be a positive number");
      this.imageId = id;
      return this;
   }

   public static class Builder {

      /**
       * @see RebuildServerOptions#withImage(int)
       */
      public static RebuildServerOptions withImage(int id) {
         RebuildServerOptions options = new RebuildServerOptions();
         return options.withImage(id);
      }
   }
}
