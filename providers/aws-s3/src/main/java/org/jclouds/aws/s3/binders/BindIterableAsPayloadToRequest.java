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
package org.jclouds.aws.s3.binders;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.Binder;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrei Savu
 */
public class BindIterableAsPayloadToRequest implements Binder {

   /**
    * The Multi-Object Delete operation supports two modes for the response; verbose and quiet.
    * By default, the operation uses verbose mode in which the response includes the result of
    * deletion of each key in your request.
    * <p/>
    * In quiet mode the response includes only keys where the delete operation encountered an
    * error. For a successful deletion, the operation does not return any information about
    * the delete in the response body.
    */
   private final boolean QUIET = true;

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input is null") instanceof Iterable,
         "this binder is only valid for an Iterable");
      checkNotNull(request, "request is null");

      Iterable<String> keys = (Iterable<String>) input;

      StringBuilder builder = new StringBuilder();
      for (String key : keys) {
         builder.append(String.format("<Object><Key>%s</Key></Object>", key));
      }

      final String objects = builder.toString();
      checkArgument(!objects.isEmpty(), "The list of keys should not be empty.");

      final String content = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<Delete><Quiet>%s</Quiet>%s</Delete>", QUIET, objects);

      Payload payload = Payloads.newStringPayload(content);
      payload.getContentMetadata().setContentType(MediaType.TEXT_XML);
      payload.getContentMetadata().setContentMD5(CryptoStreams.md5(content.getBytes()));

      request.setPayload(payload);
      return request;
   }
}
