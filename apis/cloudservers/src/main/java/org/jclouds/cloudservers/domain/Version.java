/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudservers.domain;

public class Version {

   private String docURL;
   private String id = "v1.0";
   private VersionStatus status;
   private String wadl;

   public void setDocURL(String docURL) {
      this.docURL = docURL;
   }

   public String getDocURL() {
      return docURL;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public void setStatus(VersionStatus status) {
      this.status = status;
   }

   public VersionStatus getStatus() {
      return status;
   }

   public void setWadl(String wadl) {
      this.wadl = wadl;
   }

   public String getWadl() {
      return wadl;
   }
}
