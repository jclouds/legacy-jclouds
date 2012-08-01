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
package org.jclouds.fujitsu.fgcp.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.rest.Binder;

/**
 * Adds system id to resource id and binds them to query parameters
 * 
 * @author Dies Koper
 */
@Singleton
public class BindAlsoToSystemId implements Binder {

    /**
     * 
     * @param request
     *            request where the query params will be set
     * @param input
     *            array of String params
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object input) {

        checkNotNull(input);
        checkArgument(
                input instanceof String,
                "this binder only applies to String arguments: "
                        + input.getClass());

        Pattern pattern = Pattern.compile("^(\\w+-\\w+)\\b.*");
        Matcher matcher = pattern.matcher((String) input);

        checkArgument(matcher.find(),
                "no valid resource id found to construct vsys id from: "
                        + input.toString());

        Builder<?> builder = request.toBuilder();
        builder.replaceQueryParam("vsysId", matcher.group(1));

        return (R) builder.build();
    }
}
