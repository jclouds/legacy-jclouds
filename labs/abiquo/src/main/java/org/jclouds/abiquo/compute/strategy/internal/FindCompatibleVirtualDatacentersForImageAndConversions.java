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
package org.jclouds.abiquo.compute.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.jclouds.abiquo.predicates.cloud.VirtualDatacenterPredicates.compatibleWithTemplateOrConversions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.compute.strategy.FindCompatibleVirtualDatacenters;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.predicates.cloud.VirtualDatacenterPredicates;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * Default implementation for the {@link FindCompatibleVirtualDatacenters} strategy.
 * <p>
 * This strategy assumes that the datacenter will have different hypervisor technologies, and images
 * will have conversions to each of them.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class FindCompatibleVirtualDatacentersForImageAndConversions implements
    FindCompatibleVirtualDatacenters
{
    private final RestContext<AbiquoApi, AbiquoAsyncApi> context;

    private final CloudService cloudService;

    @Inject
    public FindCompatibleVirtualDatacentersForImageAndConversions(
        final RestContext<AbiquoApi, AbiquoAsyncApi> context, final CloudService cloudService)
    {
        this.context = checkNotNull(context, "context");
        this.cloudService = checkNotNull(cloudService, "cloudService");
    }

    @Override
    public Iterable<VirtualDatacenter> execute(final VirtualMachineTemplate template)
    {
        // Build the transport object with the available information to avoid making an unnecessary
        // call to the target API (we only need the id of the datacenter, and it is present in the
        // link).
        DatacenterDto datacenterDto = new DatacenterDto();
        datacenterDto.setId(template.unwrap().getIdFromLink(ParentLinkName.DATACENTER_REPOSITORY));
        Datacenter datacenter = wrap(context, Datacenter.class, datacenterDto);

        Iterable<VirtualDatacenter> vdcs =
            cloudService.listVirtualDatacenters(VirtualDatacenterPredicates.datacenter(datacenter));

        return filter(vdcs, compatibleWithTemplateOrConversions(template));
    }

}
