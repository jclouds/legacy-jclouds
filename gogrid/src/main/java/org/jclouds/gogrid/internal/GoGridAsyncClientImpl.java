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
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.services.*;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class GoGridAsyncClientImpl implements GoGridAsyncClient {

    private GridServerAsyncClient gridServerAsyncClient;
    private GridJobAsyncClient gridJobAsyncClient;
    private GridIpAsyncClient gridIpAsyncClient;
    private GridLoadBalancerAsyncClient gridLoadBalancerAsyncClient;
    private GridImageAsyncClient gridImageAsyncClient;

    @Inject
    public GoGridAsyncClientImpl(GridServerAsyncClient gridServerClient,
                                 GridJobAsyncClient gridJobAsyncClient,
                                 GridIpAsyncClient gridIpAsyncClient,
                                 GridLoadBalancerAsyncClient gridLoadBalancerAsyncClient,
                                 GridImageAsyncClient gridImageAsyncClient) {
        this.gridServerAsyncClient = gridServerClient;
        this.gridJobAsyncClient = gridJobAsyncClient;
        this.gridIpAsyncClient = gridIpAsyncClient;
        this.gridLoadBalancerAsyncClient = gridLoadBalancerAsyncClient;
        this.gridImageAsyncClient = gridImageAsyncClient;
    }

    @Override
    public GridServerAsyncClient getServerServices() {
        return gridServerAsyncClient;
    }

    @Override
    public GridJobAsyncClient getJobServices() {
        return gridJobAsyncClient;
    }

    @Override
    public GridIpAsyncClient getIpServices() {
        return gridIpAsyncClient;
    }

    @Override
    public GridLoadBalancerAsyncClient getLoadBalancerServices() {
        return gridLoadBalancerAsyncClient;
    }

    @Override
    public GridImageAsyncClient getImageServices() {
        return gridImageAsyncClient;
    }
}
