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
import org.jclouds.oauth.v2.domain.ClaimSet;

import java.io.IOException;
import java.util.Map;

/**
 * JSON TypeAdapter for the ClaimSet type. Pull the claims maps to the root level and adds two properties for the
 * expiration time and issuing time.
 *
 * @author David Alves
 */
public class ClaimSetTypeAdapter extends TypeAdapter<ClaimSet> {

   @Override
   public void write(JsonWriter out, ClaimSet value) throws IOException {
      out.beginObject();
      for (Map.Entry<String, String> entry : value.entrySet()) {
         out.name(entry.getKey());
         out.value(entry.getValue());
      }
      out.name("exp");
      out.value(value.getExpirationTime());
      out.name("iat");
      out.value(value.getEmissionTime());
      out.endObject();
   }

   @Override
   public ClaimSet read(JsonReader in) throws IOException {
      ClaimSet.Builder builder = new ClaimSet.Builder();
      in.beginObject();
      while (in.hasNext()) {
         String claimName = in.nextName();
         String claimValue = in.nextString();
         builder.addClaim(claimName, claimValue);
      }
      in.endObject();
      return builder.build();
   }
}
