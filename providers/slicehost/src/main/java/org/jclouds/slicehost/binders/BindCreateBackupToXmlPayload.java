/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
public class BindCreateBackupToXmlPayload implements MapBinder {
   private final BindToStringPayload binder;

   @Inject
   BindCreateBackupToXmlPayload(BindToStringPayload binder) {
      this.binder = binder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      String sliceId = checkNotNull(postParams.get("slice_id"), "slice_id");
      String name = checkNotNull(postParams.get("name"), "name");
      StringBuilder builder = new StringBuilder();
      builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><backup>");
      builder.append("<slice-id type=\"integer\">").append(sliceId).append("</slice-id>");
      builder.append("<name>").append(name).append("</name>");
      builder.append("</backup>");
      request = binder.bindToRequest(request, builder.toString());
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_XML);
      return request;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("should use map params");
   }
}
