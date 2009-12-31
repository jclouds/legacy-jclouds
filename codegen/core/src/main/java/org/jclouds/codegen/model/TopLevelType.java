/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.codegen.model;

import java.util.List;

public class TopLevelType extends Type {

   private String ancestor;
   private List<String> exampleCode;
   private List<String> exampleHTML;

   public void setAncestor(String ancestor) {
      this.ancestor = ancestor;
   }

   public String getAncestor() {
      return ancestor;
   }

   public void setExampleCode(List<String> exampleCode) {
      this.exampleCode = exampleCode;
   }

   public List<String> getExampleCode() {
      return exampleCode;
   }

   public void setExampleHTML(List<String> exampleHTML) {
      this.exampleHTML = exampleHTML;
   }

   public List<String> getExampleHTML() {
      return exampleHTML;
   }

}