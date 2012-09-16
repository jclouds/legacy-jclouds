/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import javax.inject.Inject;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.enterprise.UserDto;
import com.google.common.base.Supplier;

/**
 * Gets the current user.
 * 
 * @author Ignasi Barrera
 */
public class GetCurrentUser implements Supplier<User>
{
    private RestContext<AbiquoApi, AbiquoAsyncApi> context;

    @Inject
    public GetCurrentUser(final RestContext<AbiquoApi, AbiquoAsyncApi> context)
    {
        this.context = checkNotNull(context, "context");
    }

    @Override
    public User get()
    {
        UserDto user = context.getApi().getAdminApi().getCurrentUser();
        return wrap(context, User.class, user);
    }

}
