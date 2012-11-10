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

import com.google.gson.annotations.SerializedName;

/**
 * Options for a clone.
 * TODO: test and add constructors.
 * @author Ivan Meredith
 */
public class CloneOptions implements PostData {
   /**
    * Select this if you want the newly setup VPS to be a clone of
    * another VPS you have with us.&nbsp; We will need to pause (but
    * not restart) the clone source VPS for a few seconds to a few
    * minutes to take the snapshot.
    */
   @SerializedName("vps_order_oid_to_clone")
   private Long instanceId;
   /**
    * The label you want to give the server.&nbsp; It will need to be a
    * fully qualified domain name (FQDN).&nbsp; e.g. example.com. Will
    * default to the domain name used on the order id provided.
    */
   @SerializedName("domain_name")
   private String name;

   public long getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(long instanceId) {
      this.instanceId = instanceId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   
   public void validate(){
	   assert instanceId == null || instanceId < 0;
   }
}
