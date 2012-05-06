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
package org.jclouds.demo.paas;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.demo.paas.config.PlatformServicesInitializer.PLATFORM_SERVICES_ATTRIBUTE_NAME;

import java.util.Map;

import javax.servlet.ServletContext;

import org.jclouds.demo.paas.service.taskqueue.TaskQueue;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

/**
 * @author Andrew Phillips
 */
public class PlatformServices {
    protected final String baseUrl;
    private ImmutableMap<String, TaskQueue> taskQueues;

    public PlatformServices(String baseUrl, Map<String, TaskQueue> taskQueues) {
        this.baseUrl = baseUrl;
        this.taskQueues = ImmutableMap.copyOf(taskQueues);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public @Nullable TaskQueue getTaskQueue(String name) {
        return taskQueues.get(name);
    }

    public static PlatformServices get(ServletContext context) {
        return (PlatformServices) checkNotNull(context.getAttribute(
                PLATFORM_SERVICES_ATTRIBUTE_NAME), PLATFORM_SERVICES_ATTRIBUTE_NAME);
    }
}
