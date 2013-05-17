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
package org.jclouds.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;

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
   Integer imageId;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Integer> image = Maps.newHashMap();
      if (imageId != null)
         image.put("imageId", imageId);
      return jsonBinder.bindToRequest(request, ImmutableMap.of("rebuild", image));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
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
