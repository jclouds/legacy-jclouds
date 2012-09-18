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

package org.jclouds.vsphere.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.vmware.vim25.mo.ServiceInstance;

@Singleton
public class CreateAndConnectVSphereClient implements Supplier<ServiceInstance> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<URI> providerSupplier;
   private transient ServiceInstance client;
   private transient String identity;
   private transient String credential;

   @Inject
   public CreateAndConnectVSphereClient(Function<Supplier<NodeMetadata>, ServiceInstance> providerContextToCloud,
                                        Factory runScriptOnNodeFactory,
                                        @Provider Supplier<URI> providerSupplier,
                                        @Nullable @Identity String identity,
                                        @Nullable @Credential String credential) {
      this.identity = checkNotNull(identity, "userid");
      this.credential = checkNotNull(credential, "password");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to vSphere node or vCenter server is needed");
      start();
   }

   public synchronized void start() {
      URI provider = providerSupplier.get();
      try {
         client = new ServiceInstance(new URL(provider.toASCIIString()), identity, credential, true);
      } catch (RemoteException e) {
         throw Throwables.propagate(e);
      } catch (MalformedURLException e) {
          throw Throwables.propagate(e);
      }
   }

   @Override
   public ServiceInstance get() {
      checkState(client != null, "start not called");
      return client;
   }

}
