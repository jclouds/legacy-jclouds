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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jclouds.io.Payload;
import com.google.gson.JsonObject;

/**
 * Construct parts for MultiPart MIME create dataObject
 * 
 * @author Kenneth Nagin
 */
public class MultipartMimeParts {
   final static String BOUNDARY = "gc0p4Jq0M2Yt08j34c0p";
   public static final String MULTIPARTMIXED = " multipart/mixed; boundary=" + BOUNDARY;
   final static String SEPARATER1 = "--" + BOUNDARY + "\n";
   final static String SEPARATER = "\n\n--" + BOUNDARY + "\n";
   final static String END = "\n\n--" + BOUNDARY + "--";
   final Payload payload;
   final JsonObject jsonObjectBody = new JsonObject();
   final String beginning;
   final String middle;
   final long contentLength;
   

   public MultipartMimeParts(Map<String, String> metadata, Payload payload) {
      this.payload = payload;
      JsonObject jsonObjectMetadata = new JsonObject();
      if (metadata != null) {
         for (Entry<String, String> entry : metadata.entrySet()) {
            jsonObjectMetadata.addProperty(entry.getKey(), entry.getValue());
         }
      }
      jsonObjectBody.add("metadata", jsonObjectMetadata);
      jsonObjectBody.addProperty("mimetype", "application/octet-stream");
      beginning = "Content-Type: application/cdmi-object\n" + "Content-Length: " + jsonObjectBody.toString().length()
               + "\n\n";
      long payloadLength = payload.getContentMetadata().getContentLength();
      middle = "Content-Type: application/octet-stream\n" + "Content-Transfer-Encoding: binary\n" + "Content-Length: " + payloadLength  + "\n\n";
      contentLength = SEPARATER1.length() + beginning.length() + jsonObjectBody.toString().length()
               + SEPARATER.length() + middle.length() + payload.getContentMetadata().getContentLength() + END.length();

   }

   protected InputStream getInput() {
      List<InputStream> streams = Arrays.asList(new ByteArrayInputStream(SEPARATER1.getBytes()),
               new ByteArrayInputStream(beginning.getBytes()), new ByteArrayInputStream(jsonObjectBody.toString()
                        .getBytes()), new ByteArrayInputStream(SEPARATER.getBytes()),
               new ByteArrayInputStream(middle.getBytes()), payload.getInput(),
               new ByteArrayInputStream(END.getBytes()));
      return new SequenceInputStream(Collections.enumeration(streams));

   }

   protected long getContentLength() {
      return contentLength;
   }

}
