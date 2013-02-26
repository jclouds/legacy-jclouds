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
package org.jclouds.snia.cdmi.v1.functions;

import static org.jclouds.http.HttpUtils.releasePayload;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * Parses dataObject from MultipartMimeGetResponse.
 * 
 * @author Kenneth Nagin
 */
@Singleton
public class ParseMultipartMimeGetResponse implements Function<HttpResponse, DataObject> {

   public DataObject apply(HttpResponse from) {
      DataObject dataObject = getDataObject(from.getPayload());
      releasePayload(from);
      return dataObject;
   }

   @SuppressWarnings({ "deprecation", "unchecked" })
   private DataObject getDataObject(Payload payloadIn) {
      DataObject dataObjectOut = null;
      String contentType = payloadIn.getContentMetadata().getContentType();
      String boundary = contentType.substring(contentType.lastIndexOf("=") + 1);
      StringBuilder jsonWithoutMetadata = new StringBuilder();
      StringBuilder jsonWithMetadata = new StringBuilder();
      DataInputStream dis = new DataInputStream(new BufferedInputStream(payloadIn.getInput()));
      boolean isJson = false;
      boolean isMetadata = false;
      String line;
      try {
         line = dis.readLine();
         int boundaryCnt = 0;
         int contentLength = 0;
         byte[] data = null;
         int metadataCnt = 0;
         while (line != null && !line.matches("--" + boundary + "--")) {
            if (line.matches("--" + boundary)) {
               isJson = false;
               boundaryCnt++;
               line = dis.readLine();
               continue;
            }
            if (boundaryCnt == 1 & line.startsWith("{")) {
               isJson = true;
            }
            if (isJson & !isMetadata) {
               if (line.contains("metadata")) {
                  isMetadata = true;
               } else {
                  if(line.endsWith("}")) {
                  	// searching for dangling comma
                  	// this occurs when there are no extra fields after the metadata
                     if(jsonWithoutMetadata.substring(jsonWithoutMetadata.lastIndexOf(",")+2).isEmpty()) {
                        jsonWithoutMetadata.setCharAt(jsonWithoutMetadata.lastIndexOf(","), ' ');
                     }
                  }
                  jsonWithoutMetadata.append(line);
                  jsonWithMetadata.append(line);
               }
            }
            if (isMetadata) {
            	// insulating the metatada from the rest of the json is because
            	// gson.fromJson breaks when encountering json with in json
               metadataCnt = metadataCnt + countCharOccurrences(line, '{') - countCharOccurrences(line, '}');
               if (metadataCnt <= 0) {
                  isMetadata = false;
               }
               jsonWithMetadata.append(line);
            }
            if (boundaryCnt == 2) {
               int contentLengthIndex = line.indexOf("Content-Length: ");
               if (contentLengthIndex > -1) {
                  contentLength = Integer.valueOf(line.substring(contentLengthIndex + "Content-Length: ".length()));
               }
               if (line.isEmpty()) {
                  data = new byte[contentLength];
                  dis.read(data);
                  break;
               }

            }
            line = dis.readLine();
         }
         Payload payload = (data == null) ? null : Payloads.newByteArrayPayload(data);
         Gson gson = new Gson();
         dataObjectOut = gson.fromJson(jsonWithoutMetadata.toString(), DataObject.class);
         if (dataObjectOut != null) {
            Map<String, Map<String, Object>> map = gson.fromJson(jsonWithMetadata.toString(), Map.class);
            if (map != null) {
               Map<String, Object> metadata;
               Map<String, String> userMetadata = Maps.newHashMap();
               Map<String, String> systemMetadata = Maps.newHashMap();
               List<Map<String, String>> aclMetadata = Lists.newArrayList();
               if (map.containsKey("metadata")) {
                  metadata = map.get("metadata");
                  for (String key : metadata.keySet()) {
                     if (key.startsWith("cdmi")) {
                        if (key.matches("cdmi_acl")) {
                           aclMetadata = (List<Map<String, String>>) metadata.get(key);
                        } else {
                           systemMetadata.put(key, metadata.get(key).toString());
                        }
                     } else {
                        userMetadata.put(key, metadata.get(key).toString());
                     }
                  }
               }
               Map<String, JsonBall> metadataJsonBall = ((Map<String, Map<String, JsonBall>>) gson.fromJson(
                        jsonWithMetadata.toString(), Map.class)).get("metadata");
               dataObjectOut = DataObject.builder().fromDataObject(dataObjectOut).metadata(metadataJsonBall)
                        .userMetadata(userMetadata).systemMetadata(systemMetadata).aclMetadata(aclMetadata)
                        .payload(payload).build();
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return dataObjectOut;
   }

   private static int countCharOccurrences(String s, char c) {
      int count = 0;
      for (int i = 0; i < s.length(); i++) {
         if (s.charAt(i) == c) {
            count++;
         }
      }
      return count;
   }

}
