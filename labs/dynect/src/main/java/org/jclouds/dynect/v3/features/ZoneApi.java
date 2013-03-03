/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.DynECTExceptions.TargetExistsException;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.domain.Zone.SerialStyle;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.collect.FluentIterable;

/**
 * @see ZoneAsyncApi
 * @author Adrian Cole
 */
public interface ZoneApi {
   /**
    * Lists all zone ids.
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   FluentIterable<String> list() throws JobStillRunningException;

   /**
    * Schedules addition of a new primary zone into the current session. Calling {@link ZoneApi#publish(String)} will
    * publish the zone, creating the zone.
    * 
    * @param zone
    *           required parameters to create the zone.
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws TargetExistsException
    *            if the same fqdn exists
    */
   Job scheduleCreate(CreatePrimaryZone zone) throws JobStillRunningException, TargetExistsException;

   /**
    * Schedules addition of a new primary zone with one hour default TTL and {@link SerialStyle#INCREMENT} into the
    * current session. Calling {@link ZoneApi#publish(String)} will publish the zone, creating the zone.
    * 
    * @param fqdn
    *           fqdn of the zone to create {@ex. jclouds.org}
    * @param contact
    *           email address of the contact
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws TargetExistsException
    *            if the same fqdn exists
    */
   Job scheduleCreateWithContact(String fqdn, String contact) throws JobStillRunningException, TargetExistsException;

   /**
    * Retrieves information about the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to get information about. ex {@code jclouds.org}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Nullable
   Zone get(String fqdn) throws JobStillRunningException;

   /**
    * Deletes the zone.  No need to call @link ZoneApi#publish(String)}.
    * 
    * @param fqdn
    *           zone to delete
    * @return job relating to the scheduled deletion or null, if the zone never existed.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Nullable
   Job delete(String fqdn) throws JobStillRunningException;

   /**
    * Deletes changes to the specified zone that have been created during the current session but not yet published to
    * the zone.
    * 
    * @param fqdn
    *           fqdn of the zone to delete changes from ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Job deleteChanges(String fqdn) throws JobStillRunningException;

   /**
    * Publishes the current zone
    * 
    * @param fqdn
    *           fqdn of the zone to publish. ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   Zone publish(String fqdn) throws JobStillRunningException, ResourceNotFoundException;

   /**
    * freezes the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to freeze ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Job freeze(String fqdn) throws JobStillRunningException;

   /**
    * thaws the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to thaw ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Job thaw(String fqdn) throws JobStillRunningException;
}