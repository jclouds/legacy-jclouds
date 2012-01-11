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
package org.jclouds.cloudstack.features;

import java.util.Properties;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientExpectTest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Base class for writing CloudStack Rest Client Expect tests
 * 
 * @author Andrei Savu
 */
public abstract class BaseCloudStackRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {

   public BaseCloudStackRestClientExpectTest() {
      provider = "cloudstack";
   }

   @Override
   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return clientFrom(CloudStackContext.class.cast(new ComputeServiceContextFactory(setupRestProperties())
               .createContext(provider, "identity", "credential", ImmutableSet.<Module> of(new ExpectModule(fn),
                        new NullLoggingModule(), module), props)));
   }

   protected abstract S clientFrom(CloudStackContext context);

}
