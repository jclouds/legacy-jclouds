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
package org.jclouds.oauth.v2.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jclouds.oauth.v2.domain.Header;

import java.io.IOException;

/**
 * JSON TypeAdapter for the Header type. Simply transforms the field names.
 */
public class HeaderTypeAdapter extends TypeAdapter<Header> {

   @Override
   public void write(JsonWriter out, Header value) throws IOException {
      out.beginObject();
      out.name("alg");
      out.value(value.getSignerAlgorithm());
      out.name("typ");
      out.value(value.getType());
      out.endObject();
   }

   @Override
   public Header read(JsonReader in) throws IOException {
      Header.Builder builder = new Header.Builder();
      in.beginObject();
      in.nextName();
      builder.signerAlgorithm(in.nextString());
      in.nextName();
      builder.type(in.nextString());
      in.endObject();
      return builder.build();
   }
}
