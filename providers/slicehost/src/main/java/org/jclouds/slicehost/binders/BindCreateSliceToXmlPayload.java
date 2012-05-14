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
package org.jclouds.slicehost.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCreateSliceToXmlPayload implements MapBinder {
   private final BindToStringPayload binder;

   @Inject
   BindCreateSliceToXmlPayload(BindToStringPayload binder) {
      this.binder = binder;
   }
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String flavorId = checkNotNull(postParams.get("flavor_id"), "flavor_id").toString();
      String imageId = checkNotNull(postParams.get("image_id"), "image_id").toString();
      String name = checkNotNull(postParams.get("name"), "name").toString();
      StringBuilder builder = new StringBuilder();
      builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><slice>");
      builder.append("<flavor-id type=\"integer\">").append(flavorId).append("</flavor-id>");
      builder.append("<image-id type=\"integer\">").append(imageId).append("</image-id>");
      builder.append("<name>").append(name).append("</name>");
      builder.append("</slice>");
      request = binder.bindToRequest(request, builder.toString());
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_XML);
      return request;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("should use map params");
   }
}
