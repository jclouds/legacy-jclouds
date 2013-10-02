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
package org.jclouds.rackspace.cloudloadbalancers.v1.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;

import com.google.inject.TypeLiteral;

/**
 * @author Everett Toews
 */
public class ParseMetadata extends ParseJson<Metadata> {

   @Inject
   public ParseMetadata(Json json, TypeLiteral<Metadata> type) {
      super(json, type);
   }

   @Override
   public Metadata apply(HttpResponse response) {
      Map<String, List<CLBMetadata>> clbMetadata;
      
      try {
         Type clbMetadataType = new TypeLiteral<Map<String, List<CLBMetadata>>>() {}.getType();
         clbMetadata = apply(response.getPayload().getInput(), clbMetadataType);
      }
      catch (IOException e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing response");
         logger.error(e, message.toString());
         throw new HttpResponseException(message.toString() + "\n" + response, null, response, e);
      } 
      finally {
         releasePayload(response);
      }
      
      return transformCLBMetadataToMetadata(clbMetadata.get("metadata"));
   }
   
   public static Metadata transformCLBMetadataToMetadata(List<CLBMetadata> clbMetadatum) {
      Metadata metadata = new Metadata();      
      
      for (CLBMetadata clbMetadata: clbMetadatum) {
         metadata.put(clbMetadata.key, clbMetadata.value);
         metadata.putId(clbMetadata.key, clbMetadata.id);
      }
      
      return metadata;
   }
   
   /**
    * This class is here only to deal with the metadata format in CLB.
    */
   public static class CLBMetadata {
      private int id;
      private String key;
      private String value;
   }
}
