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
package org.jclouds.codegen.ec2.queryapi;

import java.util.Map;

/**
 * 
 * @author Adrian Cole
 */
public class Content {

   private String constraints;
   private String defaultValue;
   private String desc;
   private Map<String, String> valueMap;
   private String name;
   private String optional;
   private String type;

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

   public void setOptional(String optional) {
      this.optional = optional;
   }

   public String getOptional() {
      return optional;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }

}
