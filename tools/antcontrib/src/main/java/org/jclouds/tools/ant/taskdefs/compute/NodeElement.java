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

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class NodeElement {
   private String name;
   private String profile;
   private String image;
   private String passwordproperty;
   private String keyfi1le;
   private String hostproperty;
   private String idproperty;
   private String usernameproperty;
   private String location = "default";

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

   String getProfile() {
      return profile;
   }

   public void setProfile(String profile) {
      this.profile = profile;
   }

   String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
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
      this.keyfi1le = keyfile;
   }

   String getKeyfile() {
      return keyfi1le;
   }

}
