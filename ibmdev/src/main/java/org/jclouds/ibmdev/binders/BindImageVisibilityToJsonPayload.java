/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.binders;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BindImageVisibilityToJsonPayload extends BindToStringPayload {

   @Override
   public void bindToRequest(HttpRequest request, Object payload) {
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               ImmutableSet.of(MediaType.APPLICATION_JSON));
      super.bindToRequest(request, String.format("{\"visibility\":\"%s\"}", payload));
   }

}