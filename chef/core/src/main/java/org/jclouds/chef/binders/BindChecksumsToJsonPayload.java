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

package org.jclouds.chef.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.primitives.Bytes;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindChecksumsToJsonPayload extends BindToStringPayload {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Set, "this binder is only valid for Set!");

      Set<List<Byte>> md5s = (Set<List<Byte>>) input;

      StringBuilder builder = new StringBuilder();
      builder.append("{\"checksums\":{");

      for (List<Byte> md5 : md5s)
         builder.append(String.format("\"%s\":null,", CryptoStreams.hex(Bytes.toArray(md5))));
      builder.deleteCharAt(builder.length() - 1);
      builder.append("}}");
      super.bindToRequest(request, builder.toString());
      request.getPayload().setContentType(MediaType.APPLICATION_JSON);
   }

}