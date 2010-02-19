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

package org.jclouds.tools.ebsresize;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.internal.ImmutableSet;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContext;
import org.jclouds.tools.ebsresize.facade.ElasticBlockStoreFacade;
import org.jclouds.tools.ebsresize.facade.InstanceFacade;
import org.jclouds.tools.ebsresize.util.SshExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Launches a sequence of commands to change the size of
 * EBS volume.
 *
 * This results in several minutes of downtime of the instance.
 * More details available at:
 * <a href="http://alestic.com/2010/02/ec2-resize-running-ebs-root" />
 * 
 *
 * @author Oleksiy Yarmula
 */

public class InstanceVolumeManager {

    private final ComputeServiceContext context;

    @SuppressWarnings({"FieldCanBeLocal"})
    private final RestContext<EC2AsyncClient, EC2Client> ec2context;
    private final EC2Client api;
    private final ElasticBlockStoreFacade ebsApi;
    private final InstanceFacade instanceApi;
    private final Predicate<InetSocketAddress> socketOpen =
                new RetryablePredicate<InetSocketAddress>(new SocketOpen(), 180, 5, TimeUnit.SECONDS);

    public InstanceVolumeManager(String accessKeyId, String secretKey) {
        this(accessKeyId, secretKey, new Properties());
    }

    @VisibleForTesting
    InstanceVolumeManager(String accessKeyId, String secretKey, Properties overridesForContext) {
        try {
            context = new ComputeServiceContextFactory()
                    .createContext("ec2", accessKeyId, secretKey,
                            ImmutableSet.<Module> of(), overridesForContext);
        } catch(IOException e) { throw new RuntimeException(e); }

        ec2context = context.getProviderSpecificContext();
        api = ec2context.getApi();

        ebsApi = new ElasticBlockStoreFacade(api.getElasticBlockStoreServices());
        instanceApi = new InstanceFacade(api.getInstanceServices());
    }

    public void resizeVolume(String instanceId, Region region,
                             Credentials instanceCredentials, String pathToKeyPair, int newSize) {
        RunningInstance instance = instanceApi.getInstanceByIdAndRegion(instanceId, region);

        instanceApi.stopInstance(instance);

        Volume volume = ebsApi.getRootVolumeForInstance(instance);

        ebsApi.detachVolumeFromStoppedInstance(volume, instance);

        Volume newVolume = ebsApi.cloneVolumeWithNewSize(volume, newSize);

        ebsApi.attachVolumeToStoppedInstance(newVolume, instance);

        instanceApi.startInstance(instance);

        api.getElasticBlockStoreServices().deleteVolumeInRegion(instance.getRegion(), volume.getId());

        runRemoteResizeCommands(instance, instanceCredentials, pathToKeyPair);
    }

    public void runRemoteResizeCommands(RunningInstance instance, Credentials instanceCredentials,
                                         String keyPair) {

        Map<String, ? extends ComputeMetadata> nodes = context.getComputeService().getNodes();

        //if don't set it here, nodeMetadata.getCredentials = null
        NodeMetadata nodeMetadata = addCredentials((NodeMetadata) nodes.get(instance.getId()),
                instanceCredentials);

        InetSocketAddress socket =
                new InetSocketAddress(Iterables.getLast(nodeMetadata.getPublicAddresses()), 22);

        SshExecutor sshExecutor = new SshExecutor(nodeMetadata, instanceCredentials,
                                                    keyPair, socket);

        waitForSocket(socket);

        sshExecutor.connect();
        sshExecutor.execute("sudo resize2fs " + instance.getRootDeviceName());
    }

    private NodeMetadata addCredentials(NodeMetadata nodeMetadata, Credentials credentials) {
        return new
                NodeMetadataImpl(nodeMetadata.getId(), nodeMetadata.getName(),
                nodeMetadata.getLocationId(),
                nodeMetadata.getUri(),
                nodeMetadata.getUserMetadata(), nodeMetadata.getTag(),
                nodeMetadata.getState(), nodeMetadata.getPublicAddresses(),
                nodeMetadata.getPrivateAddresses(), nodeMetadata.getExtra(),
                credentials);
    }

    public void waitForSocket(InetSocketAddress socket) {
        checkState(socketOpen.apply(socket),
                            /*or throw*/ "Couldn't connect to instance");
    }

    public void closeContext() {
        context.close();
    }

    public ElasticBlockStoreFacade getEbsApi() {
        return ebsApi;
    }

    public InstanceFacade getInstanceApi() {
        return instanceApi;
    }

    public EC2Client getApi() {
        return api;
    }

    public ComputeServiceContext getContext() {
        return context;
    }
}
