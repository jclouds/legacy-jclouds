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

package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.data.PostData;

/**
 * Attributes about a running VPS.&nbsp; Implicit with any changes to
 * these attributes on a VPS is that we'd need to restart the VPS for
 * the changes to happen.&nbsp; At least at this point in time.
 *
 * @author Ivan Meredith
 */
public class ServerParameters implements PostData {
   /**
    * File system image size.
    */
   @SerializedName("disk_space_mb")
   private Integer primaryDisk;
   /**
    * Some VPSs have a secondary partition.&nbsp; One that is not part
    * of the regular backup setups.&nbsp; Mostly not used.
    */
   @SerializedName("disk_space_2_mb")
   private Integer secondaryDisk;
   /**
    * Memory size.
    */
   @SerializedName("memory_mb")
   private Integer ram;

   public Integer getPrimaryDisk() {
      return primaryDisk;
   }

   public void setPrimaryDisk(Integer primaryDisk) {
      this.primaryDisk = primaryDisk;
   }

   public Integer getSecondaryDisk() {
      return secondaryDisk;
   }

   public void setSecondaryDisk(Integer secondaryDisk) {
      this.secondaryDisk = secondaryDisk;
   }

   public Integer getRam() {
      return ram;
   }

   public void setRam(Integer ram) {
      this.ram = ram;
   }

	@Override
	public void validate() {
		//XXX: do we need to do anything here?
	}
}
