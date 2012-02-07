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
public interface VCloudDirectorMediaType {
   public final static String NS = "http://www.vmware.com/vcloud/v1.5";

   public final static String SESSION_XML = "application/vnd.vmware.vcloud.session+xml";

   public final static String ORGLIST_XML = "application/vnd.vmware.vcloud.orgList+xml";
   
   public final static String METADATA_XML = "application/vnd.vmware.vcloud.metadata+xml";
  
   public static final String METADATAENTRY_XML = "TODO"; // TODO
   
   public final static String ORG_XML = "application/vnd.vmware.vcloud.org+xml";

   public static final String ORG_NETWORK_XML = "application/vnd.vmware.vcloud.orgNetwork+xml";
   
   public final static String TASK_XML = "application/vnd.vmware.vcloud.task+xml";

   public static final String TASKSLIST_XML = "application/vnd.vmware.vcloud.tasksList+xml";

}
