/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.codegen.model;

/**
 * 
 * @author James Murty
 */
public class Parameter {
   private String javaName;
   private String name;
   private String type;
   private String javaType;
   private String param;
   private String desc;

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public String getJavaType() {
      return javaType;
   }

   public String getParam() {
      return param;
   }

   public String getDesc() {
      return desc;
   }

   public void setJavaName(String javaName) {
      this.javaName = javaName;
   }

   public String getJavaName() {
      return javaName;
   }

}
