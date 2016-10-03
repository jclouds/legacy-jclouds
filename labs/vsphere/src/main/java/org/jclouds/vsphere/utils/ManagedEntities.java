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

package org.jclouds.vsphere.utils;

import static com.google.common.base.Throwables.propagate;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.VirtualMachine;

public class ManagedEntities {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected static Logger logger = Logger.NULL;
    
    /**
     * An Helper method to list ManagedEntities
     * 
     * @param folder main folder where to start the search
     * @param managedEntityClass the class of the ManagedEntity to cast the result
     * @return Iterable of cast ManagedEntities
     */
    public static <T> Iterable<T> listManagedEntities(Folder folder, final Class<T> managedEntityClass) {
       Iterable<T> managedEntities = ImmutableSet.<T> of();
       String managedEntityName = managedEntityClass.getSimpleName();
          try {
             managedEntities =  
                   Iterables.transform(
                         Arrays.asList(new InventoryNavigator(folder).searchManagedEntities(managedEntityName)), 
                         new Function<ManagedEntity, T>() {
                            public T apply(ManagedEntity input) {
                               return managedEntityClass.cast(input);
                            }
                         });
          } catch (InvalidProperty e) {
             logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
             throw propagate(e);
          } catch (RuntimeFault e) {
             logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
             throw propagate(e);
          } catch (RemoteException e) {
             logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
             throw propagate(e);
          }
       return managedEntities;
    }

    public static <T> Optional<T> tryFindManagedEntity(Folder folder, final Class<T> managedEntityClass) {
       Iterable<T> managedEntities = listManagedEntities(folder, managedEntityClass);
       return Iterables.tryFind(managedEntities, Predicates.notNull());
    }

    public static Optional<VirtualMachine> tryFindVmByName(Folder folder, final String vmName) {
       return FluentIterable.from(listManagedEntities(folder, VirtualMachine.class)).filter(new Predicate<VirtualMachine>() {

          @Override
          public boolean apply(VirtualMachine input) {
             return input.getName().equals(vmName);
          }}).first();
    }
    
    public static Set<Datastore> fetchDatastores(Folder folder) {
        try {
           ManagedEntity[] datacenterEntities = new InventoryNavigator(folder).searchManagedEntities("Datacenter");
           FluentIterable<Datacenter> datacenters = FluentIterable.from(Arrays.asList(datacenterEntities)).transform(
                 new Function<ManagedEntity, Datacenter>() {
                    public Datacenter apply(ManagedEntity input) {
                       return (Datacenter) input;
                    }
                 });
           Set<Datastore> datastores = Sets.newLinkedHashSet();
           for (Datacenter datacenter : datacenters) {
              Iterables.addAll(datastores, Arrays.asList(datacenter.getDatastores()));
           }
           return datastores;
        } catch (Exception e) {
           logger.error("Problem in finding a datastore", e);
           throw propagate(e);
        }
     }

    public static Datastore getDatastoreByPolicy(Set<Datastore> availableDatastores, Ordering<Datastore> policy) {
        return policy.max(availableDatastores);
    }

}
