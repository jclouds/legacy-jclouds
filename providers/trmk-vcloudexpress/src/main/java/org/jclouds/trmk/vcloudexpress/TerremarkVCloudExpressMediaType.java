/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloudexpress;

import javax.ws.rs.core.MediaType;

import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;

/**
 * Resource Types used in Terremark VCloud express
 * 
 * @see MediaType
 */
public interface TerremarkVCloudExpressMediaType extends TerremarkVCloudMediaType {
   /**
    * "application/vnd.tmrk.vcloudExpress.keysList+xml"
    */
   public static final String KEYSLIST_XML = "application/vnd.tmrk.vcloudExpress.keysList+xml";

   /**
    * "application/vnd.tmrk.vCloudExpress.keysList+xml"
    */
   public static final MediaType KEYSLIST_XML_TYPE = new MediaType("application",
            "vnd.tmrk.vcloudExpress.keysList+xml");

}
