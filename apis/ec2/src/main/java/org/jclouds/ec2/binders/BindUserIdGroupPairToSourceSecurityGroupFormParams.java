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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * Binds the String [] to query parameters named with GroupName.index
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindUserIdGroupPairToSourceSecurityGroupFormParams implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof UserIdGroupPair,
            "this binder is only valid for UserIdGroupPair!");
      UserIdGroupPair pair = (UserIdGroupPair) input;
      Builder<String, String> builder = ImmutableMultimap.builder();
      builder.put("SourceSecurityGroupOwnerId", pair.getUserId());
      builder.put("SourceSecurityGroupName", pair.getGroupName());
      return ModifyRequest.putFormParams(request, builder.build());

   }
}
