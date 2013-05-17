/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.options;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.DESCRIPTION_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOAD_BALANCER_PERSISTENCE_TYPE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOAD_BALANCER_TYPE_KEY;

import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Optional parameters for adding a load balancer.
 *
 * @see org.jclouds.gogrid.services.GridLoadBalancerClient#addLoadBalancer
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:grid.loadbalancer.add"/>
 *
 * @author Oleksiy Yarmula
 */
public class AddLoadBalancerOptions extends BaseHttpRequestOptions {

    public AddLoadBalancerOptions setDescription(String description) {
        checkState(!queryParameters.containsKey(DESCRIPTION_KEY), "Can't have duplicate " +
                "load balancer description");
        queryParameters.put(DESCRIPTION_KEY, description);
        return this;
    }

    public AddLoadBalancerOptions setType(LoadBalancerType loadBalancerType) {
        checkState(!queryParameters.containsKey(LOAD_BALANCER_TYPE_KEY), "Can't have duplicate " +
                "load balancer type limitation");
        queryParameters.put(LOAD_BALANCER_TYPE_KEY, loadBalancerType.toString());
        return this;
    }

    public AddLoadBalancerOptions setPersistenceType(LoadBalancerPersistenceType loadBalancerPersistenceType) {
        checkState(!queryParameters.containsKey(LOAD_BALANCER_PERSISTENCE_TYPE_KEY), "Can't have duplicate " +
                "load balancer type limitation");
        queryParameters.put(LOAD_BALANCER_PERSISTENCE_TYPE_KEY, loadBalancerPersistenceType.toString());
        return this;
    }

    public static class Builder {
        public AddLoadBalancerOptions create(LoadBalancerType type,
                                             LoadBalancerPersistenceType persistenceType) {
            return new AddLoadBalancerOptions().setType(type).setPersistenceType(persistenceType);
        }
    }
}
