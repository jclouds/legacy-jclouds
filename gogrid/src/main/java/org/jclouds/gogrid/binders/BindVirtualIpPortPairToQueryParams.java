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
package org.jclouds.gogrid.binders;

import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import static org.jclouds.gogrid.reference.GoGridQueryParams.VIRTUAL_IP_KEY;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Binds a virtual IP to the request.
 *
 * The {@link IpPortPair} must have a {@link IpPortPair#ip} set with a valid
 * IP address.
 *
 * @author Oleksiy Yarmula
 */
public class BindVirtualIpPortPairToQueryParams implements Binder {

    @Override
    public void bindToRequest(HttpRequest request, Object input) {
        checkArgument(checkNotNull(request, "request is null") instanceof GeneratedHttpRequest,
                "this binder is only valid for GeneratedHttpRequests!");
        checkArgument(checkNotNull(input, "input is null") instanceof IpPortPair,
                "this binder is only valid for a IpPortPair argument");

        IpPortPair ipPortPair = (IpPortPair) input;
        GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;

        checkNotNull(ipPortPair.getIp(), "There must be an IP address defined");
        checkNotNull(ipPortPair.getIp().getIp(), "There must be an IP address defined in Ip object");
        checkState(ipPortPair.getPort() > 0, "The port number must be a positive integer");

        generatedRequest.addQueryParam(VIRTUAL_IP_KEY + "ip", ipPortPair.getIp().getIp());
        generatedRequest.addQueryParam(VIRTUAL_IP_KEY + "port", String.valueOf(ipPortPair.getPort()));
    }
}
