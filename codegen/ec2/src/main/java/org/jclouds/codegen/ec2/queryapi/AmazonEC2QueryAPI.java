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
import java.util.Set;

/**
 * 
 * @author Adrian Cole
 */
public class AmazonEC2QueryAPI {
   private Map<String, Category> categories;
   private Map<String, DataType> dataTypes;
   private Set<String> see;

   public void setCategories(Map<String, Category> categories) {
      this.categories = categories;
   }

   public Map<String, Category> getCategories() {
      return categories;
   }

   public void setDataTypes(Map<String, DataType> dataTypes) {
      this.dataTypes = dataTypes;
   }

   public Map<String, DataType> getDataTypes() {
      return dataTypes;
   }

   public void setSee(Set<String> see) {
      this.see = see;
   }

   public Set<String> getSee() {
      return see;
   }

}
