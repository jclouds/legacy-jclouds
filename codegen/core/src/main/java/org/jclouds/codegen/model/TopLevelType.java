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


public class TopLevelType extends Type {

   private String ancestor;
   private String exampleCode;
   private String exampleHTML;

   public void setAncestor(String ancestor) {
      this.ancestor = ancestor;
   }

   public String getAncestor() {
      return ancestor;
   }

   public void setExampleCode(String exampleCode) {
      this.exampleCode = exampleCode;
   }

   public String getExampleCode() {
      return exampleCode;
   }

   public void setExampleHTML(String exampleHTML) {
      this.exampleHTML = exampleHTML;
   }

   public String getExampleHTML() {
      return exampleHTML;
   }

}