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
package org.jclouds.route53.features;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.Change.Status;
import org.jclouds.route53.domain.NewZone;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.domain.ZoneAndNameServers;

/**
 * @see ZoneAsyncApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnHostedZones.html"
 *      />
 * @author Adrian Cole
 */
public interface ZoneApi {

   /**
    * This action creates a new hosted zone.
    * 
    * <h4>Note</h4>
    * 
    * You cannot create a hosted zone for a top-level domain (TLD).
    * 
    * @param name
    *           The name of the domain. ex. {@code  www.example.com.} The
    *           trailing dot is optional.
    * @param callerReference
    *           A unique string that identifies the request and allows safe
    *           retries. ex. {@code MyDNSMigration_01}
    * @return the new zone in progress, in {@link Status#PENDING}.
    */
   NewZone createWithReference(String name, String callerReference);

   /**
    * like {@link #createWithReference(String, String)}, except you can specify
    * a comment.
    */
   NewZone createWithReferenceAndComment(String name, String callerReference, String comment);

   /**
    * returns all zones in order.
    */
   PagedIterable<Zone> list();

   /**
    * retrieves up to 100 zones in order.
    */
   IterableWithMarker<Zone> listFirstPage();

   /**
    * retrieves up to 100 zones in order, starting at {@code nextMarker}
    */
   IterableWithMarker<Zone> listAt(String nextMarker);

   /**
    * Retrieves information about the specified zone, including its nameserver
    * configuration
    * 
    * @param id
    *           id of the zone to get information about. ex
    *           {@code Z1PA6795UKMFR9}
    * @return null if not found
    */
   @Nullable
   ZoneAndNameServers get(String id);

   /**
    * This action deletes a hosted zone.
    * 
    * @param id
    *           id of the zone to delete. ex {@code Z1PA6795UKMFR9}
    * @return null if not found or the change in progress
    */
   @Nullable
   Change delete(String id);
}
