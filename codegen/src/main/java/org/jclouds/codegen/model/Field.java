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

import java.util.Map;

/**
 * 
 * @author Adrian Cole
 */
public class Field {

   private String constraints;
   private String defaultValue;
   private String desc;
   private String name;
   private String javaName;
   private boolean optional = true;
   private String type;
   private String javaType;
   private Map<String, String> valueMap;

   public void setConstraints(String constraints) {
      this.constraints = constraints;
   }

   public String getConstraints() {
      return constraints;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public String getDesc() {
      return desc;
   }

   public void setValueMap(Map<String, String> valueMap) {
      this.valueMap = valueMap;
   }

   public Map<String, String> getValueMap() {
      return valueMap;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setOptional(boolean optional) {
      this.optional = optional;
   }

   public boolean getOptional() {
      return optional;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }

   public void setJavaName(String javaName) {
      this.javaName = javaName;
   }

   public String getJavaName() {
      return javaName;
   }

   public void setJavaType(String javaType) {
      this.javaType = javaType;
   }

   public String getJavaType() {
      return javaType;
   }

}
