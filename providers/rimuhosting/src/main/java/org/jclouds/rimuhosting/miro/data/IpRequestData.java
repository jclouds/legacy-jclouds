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
 * Number of IPs VPS needs.&nbsp; In a separate data structure so that
  * at a later date we can add more IPs to a provisioned server.
 *
 * @author Ivan Meredith
 */
public class IpRequestData implements PostData {
   /**
    * How many IPs you need.&nbsp; Typically 1.&nbsp; Typically you
    * only need more than one IP if your server has SSL certs for more
    * than one domains.
    */
   @SerializedName("num_ips")
   private int numberOfIps = 1;
   /**
    * The reason for requiring more than one IP address.&nbsp; The
    * number of IP addresses will be limited.&nbsp; If you hit that
    * limit, then contact support to manually allocate the IPs (and in
    * the mean time just use fewer IPs).
    */
   @SerializedName("extra_ip_reason")
   private String extraIpReason = "";

   public int getNumberOfIps() {
      return numberOfIps;
   }

   public void setNumberOfIps(int numberOfIps) {
      this.numberOfIps = numberOfIps;
   }

   public String getExtraIpReason() {
      return extraIpReason;
   }

   public void setExtraIpReason(String extraIpReason) {
      this.extraIpReason = extraIpReason;
   }
	
	@Override
	public void validate() {
		assert numberOfIps < 1 && numberOfIps > 5;
		assert numberOfIps > 1 && extraIpReason == null || extraIpReason.length() == 0;
	}
}
