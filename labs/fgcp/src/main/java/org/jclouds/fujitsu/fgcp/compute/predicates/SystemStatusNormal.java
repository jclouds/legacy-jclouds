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
package org.jclouds.fujitsu.fgcp.compute.predicates;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VSystemStatus;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * @author Dies Koper
 * 
 */
@Singleton
public class SystemStatusNormal implements Predicate<String> {

    private final FGCPApi api;

    @Resource
    protected Logger logger = Logger.NULL;

    @Inject
    public SystemStatusNormal(FGCPApi api) {
        this.api = api;
    }

    public boolean apply(String systemId) {
        logger.trace("looking for status on system %s", systemId);

        VSystemStatus status = api.getVirtualSystemApi().getStatus(systemId);
        logger.trace("looking for status on system %s: currently: %s",
                systemId, status);

        if (status == VSystemStatus.ERROR)
            throw new IllegalStateException("system in error: " + status);
        return status == VSystemStatus.NORMAL;
    }

}
