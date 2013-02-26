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

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the CREATE container operation. <h2>
 * 
 * @author Kenneth Nagin
 */
public class CreateDataObjectNonCDMIOptions extends BaseHttpRequestOptions {
   /**
    * A name-value pair to associate with the container as metadata.
    */
   public CreateDataObjectNonCDMIOptions withStringPayload(String value) {
      this.payload = value;
      return this;
   }

   public static class Builder {
      public static CreateDataObjectNonCDMIOptions withStringPayload(String value) {
         CreateDataObjectNonCDMIOptions options = new CreateDataObjectNonCDMIOptions();
         return (CreateDataObjectNonCDMIOptions) options.withStringPayload(value);
      }

   }
}
