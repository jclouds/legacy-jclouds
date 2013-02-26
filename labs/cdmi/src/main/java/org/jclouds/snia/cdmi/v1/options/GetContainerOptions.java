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

/**
 * Optional get CDMI container operations
 * 
 * @author Kenneth Nagin
 */
public class GetContainerOptions extends GetCDMIObjectOptions {

   public GetContainerOptions() {
      super();
   }

   /**
    * Get CDMI container's field
    * 
    * @param fieldname
    * @return this
    */
   public GetContainerOptions field(String fieldname) {
      super.field(fieldname);
      return this;
   }

   /**
    * Get CDMI container's metadata
    * 
    * @return this
    */
   public GetContainerOptions metadata() {
      super.metadata();
      return this;
   }

   /**
    * Get CDMI container's metadata
    * 
    * @param prefix
    * @return this
    */
   public GetContainerOptions metadata(String prefix) {
      super.metadata(prefix);
      return this;
   }

   /**
    * Get CDMI container's children
    * 
    * @return this
    */
   public GetContainerOptions children() {
      this.pathSuffix = this.pathSuffix + "children;";
      return this;
   }

   /**
    * Get CDMI container's children in range
    * 
    * @param from
    * @param to
    * @return this
    */
   public GetContainerOptions children(int from, int to) {
      this.pathSuffix = this.pathSuffix + "children:" + from + "-" + to + ";";
      return this;
   }

   public static class Builder {
      public static GetContainerOptions field(String fieldname) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.field(fieldname);
      }

      public static GetContainerOptions metadata() {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.metadata();
      }

      public static GetContainerOptions metadata(String prefix) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.metadata(prefix);
      }

      public static GetContainerOptions children() {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.children();
      }

      public static GetContainerOptions children(int from, int to) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.children(from, to);
      }

   }
}
