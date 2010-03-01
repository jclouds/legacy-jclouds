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
package org.jclouds.gogrid.services;

import org.jclouds.concurrent.Timeout;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.options.GetIpListOptions;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Oleksiy Yarmula
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface GridIpClient {

    /**
     * Returns all IPs in the system
     * that match the options
     * @param options options to narrow the search down
     * @return IPs found by the search
     */
    Set<Ip> getIpList(GetIpListOptions... options);

    /**
     * Returns the list of unassigned IPs
     * @return unassigned IPs
     */
    Set<Ip> getUnassignedIpList();

    /**
     * Returns the list of assigned IPs
     * @return assigned IPs
     */
    Set<Ip> getAssignedIpList();
}
