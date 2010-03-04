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
import org.jclouds.gogrid.services.*;

/**
 * @author Oleksiy Yarmula
 */
@ImplementedBy(GoGridClientImpl.class)

public interface GoGridClient {

    /**
     * Services with methods, related to managing servers
     */
    GridServerClient getServerServices();

    /**
     * Services with methods, related to retrieving jobs
     */
    GridJobClient getJobServices();

    /**
     * Services with methods, related to retrieving IP addresses
     */
    GridIpClient getIpServices();

    /**
     * Services with methods, related to managing load balancers.
     */
    GridLoadBalancerClient getLoadBalancerServices();

    /**
    * Services with methods, related to managing images.
    */
    GridImageClient getImageServices();

}
