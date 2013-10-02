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
package org.jclouds.gae;
import static org.jclouds.http.HttpUtils.filterOutContentHeaders;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.common.base.Function;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConvertToJcloudsResponse implements Function<HTTPResponse, HttpResponse> {

   private final ContentMetadataCodec contentMetadataCodec;
   
   @Inject
   public ConvertToJcloudsResponse(ContentMetadataCodec contentMetadataCodec) {
      this.contentMetadataCodec = contentMetadataCodec;
   }
   
   @Override
   public HttpResponse apply(HTTPResponse gaeResponse) {
      Payload payload = gaeResponse.getContent() != null ? Payloads.newByteArrayPayload(gaeResponse.getContent())
            : null;
      Multimap<String, String> headers = LinkedHashMultimap.create();
      String message = null;
      for (HTTPHeader header : gaeResponse.getHeaders()) {
         if (header.getName() == null)
            message = header.getValue();
         else
            headers.put(header.getName(), header.getValue());
      }

      if (payload != null) {
         contentMetadataCodec.fromHeaders(payload.getContentMetadata(), headers);
      }
      return HttpResponse.builder()
                         .statusCode(gaeResponse.getResponseCode())
                         .message(message)
                         .payload(payload)
                         .headers(filterOutContentHeaders(headers)).build();
   }
}
