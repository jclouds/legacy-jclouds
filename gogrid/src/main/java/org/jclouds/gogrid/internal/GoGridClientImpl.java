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
package org.jclouds.gogrid.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.services.*;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class GoGridClientImpl implements GoGridClient {

    private GridServerClient gridServerClient;
    private GridJobClient gridJobClient;
    private GridIpClient gridIpClient;
    private GridLoadBalancerClient gridLoadBalancerClient;

    @Inject
    public GoGridClientImpl(GridServerClient gridServerClient,
                            GridJobClient gridJobClient,
                            GridIpClient gridIpClient,
                            GridLoadBalancerClient gridLoadBalancerClient) {
        this.gridServerClient = gridServerClient;
        this.gridJobClient = gridJobClient;
        this.gridIpClient = gridIpClient;
        this.gridLoadBalancerClient = gridLoadBalancerClient;
    }

    @Override
    public GridServerClient getServerServices() {
        return gridServerClient;
    }

    @Override
    public GridJobClient getJobServices() {
        return gridJobClient;
    }

    @Override
    public GridIpClient getIpServices() {
        return gridIpClient;
    }

    @Override
    public GridLoadBalancerClient getLoadBalancerServices() {
        return gridLoadBalancerClient;
    }
}
