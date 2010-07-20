/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.chef.domain;


/**
 * Client object.
 * 
 * @author Adrian Cole
 */
public class Client {
   private String certificate;
   private String orgname;
   private String clientname;
   private String name;
   private boolean validator;

   // only for deserialization
   Client() {

   }

   public String getCertificate() {
      return certificate;
   }

   public String getOrgname() {
      return orgname;
   }

   public String getClientname() {
      return clientname;
   }

   public String getName() {
      return name;
   }

   public boolean isValidator() {
      return validator;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", clientname=" + clientname + ", orgname=" + orgname + ", isValidator=" + validator
            + ", certificate=" + certificate + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
      result = prime * result + ((clientname == null) ? 0 : clientname.hashCode());
      result = prime * result + (validator ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((orgname == null) ? 0 : orgname.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Client other = (Client) obj;
      if (certificate == null) {
         if (other.certificate != null)
            return false;
      } else if (!certificate.equals(other.certificate))
         return false;
      if (clientname == null) {
         if (other.clientname != null)
            return false;
      } else if (!clientname.equals(other.clientname))
         return false;
      if (validator != other.validator)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (orgname == null) {
         if (other.orgname != null)
            return false;
      } else if (!orgname.equals(other.orgname))
         return false;
      return true;
   }

   public Client(String certificate, String orgname, String clientname, String name, boolean isValidator) {
      this.certificate = certificate;
      this.orgname = orgname;
      this.clientname = clientname;
      this.name = name;
      this.validator = isValidator;
   }
}