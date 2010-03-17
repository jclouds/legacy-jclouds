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

package org.jclouds.tools.ebsresize.facade;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.aws.ec2.domain.*;
import org.jclouds.aws.ec2.options.CreateSnapshotOptions;
import org.jclouds.aws.ec2.options.DetachVolumeOptions;
import org.jclouds.aws.ec2.predicates.SnapshotCompleted;
import org.jclouds.aws.ec2.predicates.VolumeAttached;
import org.jclouds.aws.ec2.predicates.VolumeAvailable;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.predicates.RetryablePredicate;

import java.util.concurrent.TimeUnit;

/**
 * Aggregates several methods of jClouds' EC2 functionality
 *  to work with elastic block store.
 * 
 * @author Oleksiy Yarmula
 */
public class ElasticBlockStoreFacade {

    final private ElasticBlockStoreClient elasticBlockStoreServices;
    final private Predicate<Volume> volumeAvailable;
    final private Predicate<Attachment> volumeAttached;
    final private Predicate<Snapshot> snapshotCompleted;

    public ElasticBlockStoreFacade(ElasticBlockStoreClient elasticBlockStoreServices) {
        this.elasticBlockStoreServices = elasticBlockStoreServices;
        this.volumeAvailable =
                new RetryablePredicate<Volume>(new VolumeAvailable(elasticBlockStoreServices),
                        600, 10, TimeUnit.SECONDS);
        this.volumeAttached =
                new RetryablePredicate<Attachment>(new VolumeAttached(elasticBlockStoreServices),
                        600, 10, TimeUnit.SECONDS);
        this.snapshotCompleted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(
               elasticBlockStoreServices), 600, 10, TimeUnit.SECONDS);

    }

    /**
     * Returns the root volume for instance with EBS type of root device.
     *
     * @param runningInstance
     *              instance of EBS type that has volume(s) attached
     * @return root device volume
     */
    public Volume getRootVolumeForInstance(RunningInstance runningInstance) {
        checkArgument(runningInstance.getRootDeviceType() == RootDeviceType.EBS,
                "Only storage for instances with EBS type of root device can be resized.");

        String rootDevice = checkNotNull(runningInstance.getRootDeviceName());

        //get volume id
        String volumeId = null;
        for(String ebsVolumeId : runningInstance.getEbsBlockDevices().keySet()) {
            if(! rootDevice.equals(ebsVolumeId)) continue;
            RunningInstance.EbsBlockDevice device = runningInstance.getEbsBlockDevices().get(ebsVolumeId);
            volumeId = checkNotNull(device.getVolumeId(), "Device's volume id must not be null.");
        }

        //return volume by volume id
        return Iterables.getOnlyElement(elasticBlockStoreServices.
                                    describeVolumesInRegion(runningInstance.getRegion(), volumeId));
    }

    /**
     * Detaches volume from an instance.
     *
     * This method blocks until the operations are fully completed.
     * @param volume
     *              volume to detach
     * @param stoppedInstance
     *              instance to which the volume is currently attached
     */
    public void detachVolumeFromStoppedInstance(Volume volume, RunningInstance stoppedInstance) {
        elasticBlockStoreServices.detachVolumeInRegion(stoppedInstance.getRegion(), volume.getId(), false,
                DetachVolumeOptions.Builder.fromInstance(stoppedInstance.getId()));
        checkState(volumeAvailable.apply(volume),
                                /*or throw*/ "Couldn't detach the volume from instance");
    }

    /**
     * Makes a 'copy' of current volume with different size.
     * Behind the scenes, if creates a snapshot of current volume,
     * and then creates a new volume with given size from the snapshot.
     *
     * This method blocks until the operations are fully completed.
     *
     * @param volume volume to be cloned
     * @param newSize size of new volume
     * @return newly created volume
     */
    public Volume cloneVolumeWithNewSize(Volume volume, int newSize) {
        Snapshot createdSnapshot =
                elasticBlockStoreServices.createSnapshotInRegion(volume.getRegion(), volume.getId(),
                CreateSnapshotOptions.Builder.withDescription("snapshot to test extending volumes"));
        checkState(snapshotCompleted.apply(createdSnapshot),
                                            /*or throw*/ "Couldn't create a snapshot");

        Volume newVolume = elasticBlockStoreServices.createVolumeFromSnapshotInAvailabilityZone(
                volume.getAvailabilityZone(), newSize,
                createdSnapshot.getId());
        checkState(volumeAvailable.apply(newVolume),
                                /*or throw*/ "Couldn't create a volume from the snapshot");

        elasticBlockStoreServices.deleteSnapshotInRegion(volume.getRegion(), createdSnapshot.getId());
        return newVolume;
    }

    /**
     * Attaches volume to a (stopped) instance.
     *
     * This method blocks until the operations are fully completed.
     * @param volume
     *              volume to attach
     * @param instance
     *              instance to which the volume is to be attached, must be in
     *              a 'stopped' state
     */
    public void attachVolumeToStoppedInstance(Volume volume, RunningInstance instance) {
        Attachment volumeAttachment = elasticBlockStoreServices.
                        attachVolumeInRegion(instance.getRegion(), volume.getId(), instance.getId(),
                                        instance.getRootDeviceName());
        checkState(volumeAttached.apply(volumeAttachment),
                                    /*or throw*/ "Couldn't attach volume back to the instance");
    }
}
