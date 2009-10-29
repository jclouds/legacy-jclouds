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
package org.jclouds.codegen.model;

import java.util.Set;

public class Type {

   private String packageName;
   private String name;
   private String javaName;
   private Set<String> see;
   private Set<Field> fields;

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setJavaName(String javaName) {
      this.javaName = javaName;
   }

   public String getJavaName() {
      return javaName;
   }

   public void setSee(Set<String> see) {
      this.see = see;
   }

   public Set<String> getSee() {
      return see;
   }

   public void setFields(Set<Field> fields) {
      this.fields = fields;
   }

   public Set<Field> getFields() {
      return fields;
   }

   public void setPackageName(String packageName) {
      this.packageName = packageName;
   }

   public String getPackageName() {
      return packageName;
   }

}