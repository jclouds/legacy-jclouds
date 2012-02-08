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
package org.jclouds.vcloud.director.v1_5;

/**
 * Resource Types used in VCloud.
 * 
 * The object type, specified as a MIME content type, of the object that the link references. This
 * attribute is present only for links to objects. It is not present for links to actions.
 * 
 * @see javax.ws.rs.core.MediaType;
 */
public enum VCloudDirectorMediaType {
   ANY("*/*"),
   SESSION("application/vnd.vmware.vcloud.session+xml"),
   ERROR("application/vnd.vmware.vcloud.error+xml"),
   ORG_LIST("application/vnd.vmware.vcloud.orgList+xml"),
   METADATA("application/vnd.vmware.vcloud.metadata+xml"),
   METADATA_ENTRY("*/*"), // TODO
   ORG("application/vnd.vmware.vcloud.org+xml"),
   TASKS_LIST("application/vnd.vmware.vcloud.tasksList+xml"),
   TASK("application/vnd.vmware.vcloud.task+xml");

   private final String mediaType;

   private VCloudDirectorMediaType(String mediaType) {
      this.mediaType = mediaType;
   }

   public String getMediaType() {
      return mediaType;
   }
}
