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
package org.jclouds.gogrid.functions;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.http.functions.ParseJson;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.SortedSet;

/**
 * Parses the single load balancer out of the response.
 *
 * This class delegates parsing to {@link ParseLoadBalancerListFromJsonResponse}.  
 *
 * @author Oleksiy Yarmula
 */
public class ParseLoadBalancerFromJsonResponse extends ParseJson<LoadBalancer> {

    @Inject
    public ParseLoadBalancerFromJsonResponse(Gson gson) {
        super(gson);
    }

    public LoadBalancer apply(InputStream stream) {
        SortedSet<LoadBalancer> allLoadBalancers =
                new ParseLoadBalancerListFromJsonResponse(gson).apply(stream);
        return Iterables.getOnlyElement(allLoadBalancers);
    }

}
