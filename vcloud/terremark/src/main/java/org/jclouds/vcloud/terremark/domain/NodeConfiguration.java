/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;


/**
 * 
 * @author Adrian Cole
 * 
 */
public class NodeConfiguration {
   private String name = null;
   private String description = null;
   private String enabled = null;

   public NodeConfiguration enableTraffic() {
      this.enabled = "true";
      return this;
   }

   public NodeConfiguration disableTraffic() {
      this.enabled = "false";
      return this;
   }

   public NodeConfiguration changeNameTo(String name) {
      this.name = name;
      return this;
   }

   public NodeConfiguration changeDescriptionTo(String description) {
      this.description = description;
      return this;
   }

   public static class Builder {
      /**
       * @see NodeConfiguration#changeNameTo(String)
       */
      public static NodeConfiguration changeNameTo(String name) {
         NodeConfiguration options = new NodeConfiguration();
         return options.changeNameTo(name);
      }

      /**
       * @see NodeConfiguration#changeDescriptionTo(String)
       */
      public static NodeConfiguration changeDescriptionTo(String description) {
         NodeConfiguration options = new NodeConfiguration();
         return options.changeDescriptionTo(description);
      }

      /**
       * @see NodeConfiguration#enableTraffic()
       */
      public static NodeConfiguration enableTraffic() {
         NodeConfiguration options = new NodeConfiguration();
         return options.enableTraffic();
      }

      /**
       * @see NodeConfiguration#disableTraffic()
       */
      public static NodeConfiguration disableTraffic() {
         NodeConfiguration options = new NodeConfiguration();
         return options.disableTraffic();
      }
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public String getEnabled() {
      return enabled;
   }
}
