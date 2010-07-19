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
package org.jclouds.gae;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.common.base.Function;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConvertToJcloudsResponse implements Function<HTTPResponse, HttpResponse> {
   private final HttpUtils utils;

   @Inject
   ConvertToJcloudsResponse(HttpUtils utils) {
      this.utils = utils;
   }

   @Override
   public HttpResponse apply(HTTPResponse gaeResponse) {
      Payload payload = gaeResponse.getContent() != null ? Payloads.newByteArrayPayload(gaeResponse
               .getContent()) : null;
      Multimap<String, String> headers = LinkedHashMultimap.create();
      String message = null;
      for (HTTPHeader header : gaeResponse.getHeaders()) {
         if (header.getName() == null)
            message = header.getValue();
         else
            headers.put(header.getName(), header.getValue());
      }
      HttpResponse response = new HttpResponse(gaeResponse.getResponseCode(), message, payload);
      utils.setPayloadPropertiesFromHeaders(headers, response);
      return response;
   }
}