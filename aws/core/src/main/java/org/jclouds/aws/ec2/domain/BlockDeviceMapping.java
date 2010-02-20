package org.jclouds.aws.ec2.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.internal.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

/**
 * Defines the mapping of volumes for
 * {@link org.jclouds.aws.ec2.services.InstanceClient#setBlockDeviceMappingForInstanceInRegion}.
 *
 * @author Oleksiy Yarmula
 */
public class BlockDeviceMapping {

    private final List<RunningInstance.EbsBlockDevice> ebsBlockDevices = Lists.newArrayList();

    public BlockDeviceMapping() {
    }

    /**
     * Creates block device mapping from the list of {@link RunningInstance.EbsBlockDevice devices}.
     *
     * This method copies the values of the list.
     * @param ebsBlockDevices
     *                      devices to be changed for the volume
     */
    public BlockDeviceMapping(List<RunningInstance.EbsBlockDevice> ebsBlockDevices) {
        this.ebsBlockDevices.addAll(checkNotNull(ebsBlockDevices,
                /*or throw*/ "EbsBlockDevices can't be null"));
    }

    /**
     * Adds a {@link RunningInstance.EbsBlockDevice} to the mapping.
     * @param ebsBlockDevice
     *                      ebsBlockDevice to be added
     * @return the same instance for method chaining purposes
     */
    public BlockDeviceMapping addEbsBlockDevice(RunningInstance.EbsBlockDevice ebsBlockDevice) {
        this.ebsBlockDevices.add(checkNotNull(ebsBlockDevice,
                /*or throw*/ "EbsBlockDevice can't be null"));
        return this;
    }

    public List<RunningInstance.EbsBlockDevice> getEbsBlockDevices() {
        return ImmutableList.copyOf(ebsBlockDevices);
    }

    public static class EbsBlockDevice2 {
        private final String volumeId;
        private final String deviceName;
        private final Boolean deleteOnTermination;
        private final Attachment.Status attachmentStatus;
        private final Date attachTime;

        /**
         *
         * @param volumeId
         *              required parameter (can not be null)
         * @param deviceName
         *              name of the device (ie "/dev/sda1")
         * @param deleteOnTermination
         *              whether the volume will be deleted on instance termination
         */
        public EbsBlockDevice2(String volumeId, @Nullable String deviceName,
                              @Nullable Boolean deleteOnTermination) {
            this(volumeId, deviceName, null, null, deleteOnTermination);
        }


        /**
         *
         * @param volumeId
         *              required parameter (can not be null)
         * @param deviceName
         *              name of the device (ie "/dev/sda1")
         * @param attachmentStatus
         *              whether the device is attached, detached
         * @param attachTime
         *              when the device was attached
         * @param deleteOnTermination
         *              whether the volume will be deleted on instance termination
         */
        public EbsBlockDevice2(String volumeId, @Nullable String deviceName,
                              @Nullable Attachment.Status attachmentStatus,
                              @Nullable Date attachTime, @Nullable Boolean deleteOnTermination) {
            this.volumeId = checkNotNull(volumeId, /*or throw*/ "VolumeId is required");
            this.deviceName = deviceName;
            this.deleteOnTermination = deleteOnTermination;
            this.attachmentStatus = attachmentStatus;
            this.attachTime = attachTime;
        }

        /**
         * Returns the volume id
         * @return volume id. This value is never null
         */
        public String getVolumeId() {
            return volumeId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public Boolean isDeleteOnTermination() {
            return deleteOnTermination;
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EbsBlockDevice2 other = (EbsBlockDevice2) obj;
            if (volumeId == null) {
                if (other.volumeId != null)
                    return false;
            } else if (!volumeId.equals(other.volumeId))
                return false;
            if (attachTime == null) {
                if (other.attachTime != null)
                    return false;
            } else if (!attachTime.equals(other.attachTime))
                return false;
            if (attachmentStatus == null) {
                if (other.attachmentStatus != null)
                    return false;
            } else if (!attachmentStatus.equals(other.attachmentStatus))
                return false;
            return deleteOnTermination == other.deleteOnTermination;

        }
    }

}
