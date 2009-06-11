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
package org.jclouds.codegen.ec2.queryapi;

import java.util.Set;

public class DataType {

   private String type;
   private String ancestor;
   private Set<String> see;
   private String exampleCode;
   private String exampleHTML;
   private Set<Content> contents;

   public DataType() {
      super();
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }

   public void setAncestor(String ancestor) {
      this.ancestor = ancestor;
   }

   public String getAncestor() {
      return ancestor;
   }

   public void setSee(Set<String> see) {
      this.see = see;
   }

   public Set<String> getSee() {
      return see;
   }

   public void setExampleCode(String exampleCode) {
      this.exampleCode = exampleCode;
   }

   public String getExampleCode() {
      return exampleCode;
   }

   public void setContents(Set<Content> contents) {
      this.contents = contents;
   }

   public Set<Content> getContents() {
      return contents;
   }

   public void setExampleHTML(String exampleHTML) {
      this.exampleHTML = exampleHTML;
   }

   public String getExampleHTML() {
      return exampleHTML;
   }

}