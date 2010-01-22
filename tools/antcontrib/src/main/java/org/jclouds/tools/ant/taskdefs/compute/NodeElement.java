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
package org.jclouds.tools.ant.taskdefs.compute;

import java.io.File;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class NodeElement {
   private String name;
   private String size;
   private String os;
   private String openports = "22";
   private String passwordproperty;
   private String keyfile;
   private String hostproperty;
   private String idproperty;
   private String usernameproperty;
   private String location;
   private File runscript;

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   String getUsernameproperty() {
      return usernameproperty;
   }

   /**
    * The name of a property in which the username of the login user should be stored.
    * 
    */
   public void setUsernameproperty(String usernameproperty) {
      this.usernameproperty = usernameproperty;
   }

   String getPasswordproperty() {
      return passwordproperty;
   }

   /**
    * The name of a property in which the password of the login user should be stored.
    * 
    */
   public void setPasswordproperty(String passwordproperty) {
      this.passwordproperty = passwordproperty;
   }

   /**
    * The name of a property in which the hostname of the machine should be stored
    * 
    */
   public void setHostproperty(String hostproperty) {
      this.hostproperty = hostproperty;
   }

   String getHostproperty() {
      return hostproperty;
   }

   /**
    * The name of a property in which the id of the machine should be stored
    * 
    */
   public void setIdproperty(String idproperty) {
      this.idproperty = idproperty;
   }

   String getIdproperty() {
      return idproperty;
   }

   /**
    * The name of a file under which to store the DSA key of the user (if supported)
    */
   public void setKeyfile(String keyfile) {
      this.keyfile = keyfile;
   }

   String getKeyfile() {
      return keyfile;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public String getSize() {
      return size;
   }

   public void setOs(String os) {
      this.os = os;
   }

   public String getOs() {
      return os;
   }

   public void setRunscript(File runscript) {
      this.runscript = runscript;
   }

   public File getRunscript() {
      return runscript;
   }

   public void setOpenports(String openports) {
      this.openports = openports;
   }

   public String getOpenports() {
      return openports;
   }

}
