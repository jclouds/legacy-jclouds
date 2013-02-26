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
package org.jclouds.snia.cdmi.v1.queryparams;

/**
 * Generate CDMI object query parameters Note: The preferred implementation would use jax-rs queryParam. However, the
 * CDMI query parameters specification does not conform to jax-rs queryParam of key=value separated by &. Rather it
 * follows the form: ?<fieldname>;<fieldname>;.... ?metadata:<prefix>;... ?children:<from>-<to>;...
 * ?value:<from>-<to>;...
 * 
 * @author Kenneth Nagin
 */
public class CDMIObjectQueryParams {

   protected String queryParams = "";

   public CDMIObjectQueryParams() {
      super();
   }

   /**
    * Get CDMI data object's field value
    * 
    * @param fieldname
    * @return this
    */
   public CDMIObjectQueryParams field(String fieldname) {
      queryParams = queryParams + fieldname + ";";
      return this;
   }

   /**
    * Get CDMI data object's metadata
    * 
    * @return this
    */
   public CDMIObjectQueryParams metadata() {
      queryParams = queryParams + "metadata;";
      return this;
   }

   /**
    * Get CDMI data object's metadata associated with prefix
    * 
    * @param prefix
    * @return this
    */
   public CDMIObjectQueryParams metadata(String prefix) {
      queryParams = queryParams + "metadata:" + prefix + ";";
      return this;
   }

   /**
    * Get CDMI data object's with any query parameter string
    * 
    * @param anyQueryParam
    * @return this
    */
   public CDMIObjectQueryParams any(String anyQueryParam) {
      queryParams = queryParams + anyQueryParam + ";";
      return this;
   }

   public static class Builder {
      public static CDMIObjectQueryParams field(String fieldname) {
         CDMIObjectQueryParams options = new CDMIObjectQueryParams();
         return (CDMIObjectQueryParams) options.field(fieldname);
      }

      public static CDMIObjectQueryParams metadata() {
         CDMIObjectQueryParams options = new CDMIObjectQueryParams();
         return (CDMIObjectQueryParams) options.metadata();
      }

      public static CDMIObjectQueryParams metadata(String prefix) {
         CDMIObjectQueryParams options = new CDMIObjectQueryParams();
         return (CDMIObjectQueryParams) options.metadata(prefix);
      }

      public static CDMIObjectQueryParams any(String anyQueryParam) {
         CDMIObjectQueryParams options = new CDMIObjectQueryParams();
         return (CDMIObjectQueryParams) options.any(anyQueryParam);
      }

   }

   public String toString() {
      return queryParams;
   }

}
