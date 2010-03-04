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
package org.jclouds.gogrid.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.*;

/**
 * @author Oleksiy Yarmula
 */
public class AddServerOptions extends BaseHttpRequestOptions {

    public AddServerOptions setDescription(String description) {
        checkState(!queryParameters.containsKey(DESCRIPTION_KEY), "Can't have duplicate server description");
        queryParameters.put(DESCRIPTION_KEY, description);
        return this;
    }

    /**
     * Make server a sandbox instance.
     * By default, it's not.
     *
     * @return itself for convenience
     */
    public AddServerOptions makeSandboxType() {
        checkState(!queryParameters.containsKey(IS_SANDBOX_KEY), "Can only have one sandbox option per server");
        queryParameters.put(IS_SANDBOX_KEY, "true");
        return this;
    }

}
