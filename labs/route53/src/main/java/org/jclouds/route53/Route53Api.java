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
package org.jclouds.route53;

import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.features.RecordSetApi;
import org.jclouds.route53.features.ZoneApi;

/**
 * Provides access to Amazon Route53 via the Query API
 * <p/>
 * 
 * @see Route53AsyncApi
 * @see <a href="http://docs.amazonwebservices.com/Route53/latest/APIReference"
 *      />
 * @author Adrian Cole
 */
public interface Route53Api {

   /**
    * returns the current status of a change batch request.
    * 
    * @param changeID
    *           The ID of the change batch request.
    * @return null, if not found
    */
   Change getChange(String changeID);

   /**
    * Provides synchronous access to Zone features.
    */
   @Delegate
   ZoneApi getZoneApi();
   
   /**
    * Provides synchronous access to record set features.
    */
   @Delegate
   RecordSetApi getRecordSetApiForZone(@PathParam("zoneId") String zoneId);
}
