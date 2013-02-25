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
 * Generate CDMI container query parameters Example: container = containerApi.get
 * (containerName,ContainerQueryParams.Builder.field("parentURI")); container =
 * containerApi.get(containerName,ContainerQueryParams.Builder.children(0,3));
 * 
 * @author Kenneth Nagin
 */
public class ContainerQueryParams extends CDMIObjectQueryParams {

   public ContainerQueryParams() {
      super();
   }

   /**
    * Get CDMI container's field value
    * 
    * @param fieldname
    * @return this
    */
   public ContainerQueryParams field(String fieldname) {
      super.field(fieldname);
      return this;
   }

   /**
    * Get CDMI container's metadata
    * 
    * @return this
    */
   public ContainerQueryParams metadata() {
      super.metadata();
      return this;
   }

   /**
    * Get CDMI container's metadata associated with prefix
    * 
    * @param prefix
    * @return this
    */
   public ContainerQueryParams metadata(String prefix) {
      super.metadata(prefix);
      return this;
   }

   /**
    * Get CDMI data object's with any query parameter string
    * 
    * @param anyQueryParam
    * @return this
    */
   public ContainerQueryParams any(String anyQueryParam) {
      super.any(anyQueryParam);
      return this;
   }

   /**
    * Get CDMI container's children
    * 
    * @return this
    */
   public ContainerQueryParams children() {
      queryParams = queryParams + "children;";
      return this;
   }

   /**
    * Get CDMI container's children within range
    * 
    * @param from
    * @param to
    * @return this
    */
   public ContainerQueryParams children(int from, int to) {
      queryParams = queryParams + "children:" + from + "-" + to + ";";
      return this;
   }

   public static class Builder {
      public static ContainerQueryParams field(String fieldname) {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.field(fieldname);
      }

      public static ContainerQueryParams metadata() {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.metadata();
      }

      public static ContainerQueryParams metadata(String prefix) {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.metadata(prefix);
      }

      public static ContainerQueryParams any(String anyQueryParam) {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.any(anyQueryParam);
      }

      public static ContainerQueryParams children() {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.children();
      }

      public static ContainerQueryParams children(int from, int to) {
         ContainerQueryParams options = new ContainerQueryParams();
         return (ContainerQueryParams) options.children(from, to);
      }

   }

}
