/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.rimuhosting.miro.data;

import org.jclouds.rimuhosting.miro.domain.Image;

import com.google.gson.annotations.SerializedName;

/**
 * Some options we need to create a new server/VPS.
 *
 * @author Ivan Meredith
 */
public class CreateOptions implements PostData{
   public CreateOptions(){}
   
   public CreateOptions(String name, String password, Image image){
	   this.name = name;
	   this.password = password;
	   if(image != null){
		   this.imageId = image.getId();
	   }
   }
   public CreateOptions(String name, String password, String imageId){
	   this.name = name;
	   this.password = password;
	   this.imageId = imageId;
   }
	/**
    * The control panel to install on the VPS.  Currently only webmin is installable manually.
    * Currently null/empty string works.  For none.  Or webmin.
    * TODO: Control panels need a rest @GET interface. Or enum
    */
   @SerializedName("control_panel")
   private String controlPanel;
   /**
    * Must be a valid Image id.
    */
   @SerializedName("distro")
   private String imageId;
   /**
    * Must be FQDN.
    */
   @SerializedName("domain_name")
   private String name;
   /**
    * The password to use when setting up the server.  If not provided we will set a random one.
    */
   private String password;

   public String getControlPanel() {
      return controlPanel;
   }

   public void setControlPanel(String controlPanel) {
      this.controlPanel = controlPanel;
   }

   public String getImageId() {
      return imageId;
   }

   public void setImageId(String imageId) {
      this.imageId = imageId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

	@Override
	public void validate() {
		assert imageId != null && imageId.length() != 0;
		//TODO validation of FQDN
	}
}
