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
package org.jclouds.snia.cdmi.v1.options;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.BaseEncoding;
import org.jclouds.io.Payload;

/**
 * CreateDataObjectOptions options supported in the REST API for the CREATE CDMI Data Object operation.
 * 
 * @author Kenneth Nagin
 */
public class CreateDataObjectOptions extends CreateCDMIObjectOptions {
   public static final String BASE64 = "base64";
   public static final String UTF8 = "utf-8";

   public CreateDataObjectOptions() {
   }

   /**
    * Create CDMI data object with metadata
    * 
    * @param metadata
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions metadata(Map<String, String> metadata) {
      super.metadata(metadata);
      return this;
   }

   /**
    * Create CDMI data object with mimetype
    * 
    * @param mimetype
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions mimetype(String mimetype) {
      jsonObjectBody.addProperty("mimetype", mimetype);
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with value equal to empty string
    * 
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value() {
      jsonObjectBody.addProperty("value", new String());
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with String value
    * 
    * @param value
    *           String value
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(String value) {
      jsonObjectBody.addProperty("value", (value == null) ? "" : value);
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with InputStream value
    * 
    * @param value 
    *           Payload is converted to a String value with UTF-8 (default) or  base64.
    *           User sets value's contentEncoding field to base64.
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(Payload value) throws IOException {
      if(value==null) {
         throw new IllegalArgumentException("CreateDataObjectOptions Payload can not be null");
         
      }
      if(value.getContentMetadata().getContentEncoding()!=null && BASE64.matches(value.getContentMetadata().getContentEncoding())) {
         jsonObjectBody.addProperty("value",
                  (value == null) ? new String() : BaseEncoding.base64().encode(ByteStreams.toByteArray(value.getInput())));
         jsonObjectBody.addProperty("valuetransferencoding", BASE64);
      } else {
         jsonObjectBody.addProperty("value", (value == null) ? new String() : CharStreams.toString(new InputStreamReader(value.getInput(), Charsets.UTF_8)));
         jsonObjectBody.addProperty("valuetransferencoding", UTF8);         
      }
      this.payload = jsonObjectBody.toString();
      return this;
   }

   public static class Builder {
      public static CreateDataObjectOptions metadata(Map<String, String> metadata) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.metadata(metadata);
      }

      public static CreateDataObjectOptions mimetype(String mimetype) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.mimetype(mimetype);
      }

      public static CreateDataObjectOptions value() {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value();
      }

      public static CreateDataObjectOptions value(String value) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }
      
      public static CreateDataObjectOptions value(Payload value) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }

   }
}
