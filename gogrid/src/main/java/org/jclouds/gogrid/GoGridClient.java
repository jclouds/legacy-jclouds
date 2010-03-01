/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.gogrid;

import com.google.inject.ImplementedBy;
import org.jclouds.gogrid.internal.GoGridClientImpl;
import org.jclouds.gogrid.services.GridIpClient;
import org.jclouds.gogrid.services.GridJobClient;
import org.jclouds.gogrid.services.GridLoadBalancerClient;
import org.jclouds.gogrid.services.GridServerClient;

/**
 * @author Oleksiy Yarmula
 */
@ImplementedBy(GoGridClientImpl.class)

public interface GoGridClient {

    /**
     * Returns methods, related to managing servers
     * @return serverServices
     */
    GridServerClient getServerServices();

    /**
     * Returns methods, related to retrieving jobs
     * @return jobServices
     */
    GridJobClient getJobServices();

    /**
     * Returns methods, related to retrieving IP addresses
     * @return ipServices
     */
    GridIpClient getIpServices();

    /**
     * Returns method, related to managing load balancers.
     * @return loadBalancerServices
     */
    GridLoadBalancerClient getLoadBalancerServices();

}
