/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.services;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceStateChange;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface InstanceClient {

   /**
    * Returns information about instances that you own.
    * <p/>
    * 
    * If you specify one or more instance IDs, Amazon EC2 returns information for those instances.
    * If you do not specify instance IDs, Amazon EC2 returns information for all relevant instances.
    * If you specify an invalid instance ID, a fault is returned. If you specify an instance that
    * you do not own, it will not be included in the returned results.
    * <p/>
    * Recently terminated instances might appear in the returned results.This interval is usually
    * less than one hour.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * 
    * @see #runInstancesInRegion
    * @see #terminateInstancesInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeInstances.html"
    *      />
    */
   Set<Reservation> describeInstancesInRegion(Region region, String... instanceIds);

   /**
    * Launches a specified number of instances of an AMI for which you have permissions.
    * <p/>
    * 
    * If Amazon EC2 cannot launch the minimum number AMIs you request, no instances will be
    * launched. If there is insufficient capacity to launch the maximum number of AMIs you request,
    * Amazon EC2 launches the minimum number specified for each AMI and allocate the remaining
    * available instances using round robin.
    * <p/>
    * <h4>Security Groups</h4>
    * <b>Note:</b> Every instance is launched in a security group (created using the
    * CreateSecurityGroup operation.
    * <h4>Key Pair</h4>
    * You can provide an optional key pair ID for each image in the launch request (created using
    * the CreateKeyPair operation). All instances that are created from images that use this key
    * pair will have access to the associated public key at boot. You can use this key to provide
    * secure access to an instance of an image on a per-instance basis. Amazon EC2 public images use
    * this feature to provide secure access without passwords.
    * <p/>
    * <b>Note:</b> Launching public images without a key pair ID will leave them inaccessible.
    * <p/>
    * The public key material is made available to the instance at boot time by placing it in the
    * openssh_id.pub file on a logical device that is exposed to the instance as /dev/sda2 (the
    * instance store). The format of this file is suitable for use as an entry within
    * ~/.ssh/authorized_keys (the OpenSSH format). This can be done at boot (e.g., as part of
    * rc.local) allowing for secure access without passwords.
    * <h4>User Data</h4>
    * Optional user data can be provided in the launch request. All instances that collectively
    * comprise the launch request have access to this data. For more information, go the Amazon
    * Elastic Compute Cloud Developer Guide.
    * <h4>Product Codes</h4>
    * 
    * <b>Note:</b> If any of the AMIs have a product code attached for which the user has not
    * subscribed, the RunInstances call will fail.
    * <h4>Kernel</h4>
    * 
    * <b>Important:</b> We strongly recommend using the 2.6.18 Xen stock kernel with High-CPU and
    * High-Memory instances. Although the default Amazon EC2 kernels will work, the new kernels
    * provide greater stability and performance for these instance types. For more information about
    * kernels, go the Amazon Elastic Compute Cloud Developer Guide.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param nullableAvailabilityZone
    *           Specifies the placement constraints (Availability Zones) for launching the
    *           instances. If null, Amazon will determine the best availability zone to place the
    *           instance.
    * @param imageId
    *           Unique ID of a machine image, returned by a call to
    * @param minCount
    *           Minimum number of instances to launch. If the value is more than Amazon EC2 can
    *           launch, no instances a re launched at all. Constraints: Between 1 and the maximum
    *           number allowed for your account (default: 20).
    * @param maxCount
    *           Maximum number of instances to launch. If the value is more than Amazon EC2 can
    *           launch, the largest possible number above minCount will be launched instead.
    *           Constraints: Between 1 and the maximum number allowed for your account (default:
    *           20).
    * @see #describeInstancesInRegion
    * @see #terminateInstancesInRegion
    * @see #authorizeSecurityGroupIngressInRegion
    * @see #revokeSecurityGroupIngressInRegion
    * @see #describeSecurityGroupsInRegion
    * @see #createSecurityGroupInRegion
    * @see #createKeyPairInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RunInstances.html"
    *      />
    * @see RunInstancesOptions
    */
   Reservation runInstancesInRegion(Region region,
            @Nullable AvailabilityZone nullableAvailabilityZone, String imageId, int minCount,
            int maxCount, RunInstancesOptions... options);

   /**
    * Shuts down one or more instances. This operation is idempotent; if you terminate an instance
    * more than once, each call will succeed.
    * <p/>
    * Terminated instances will remain visible after termination (approximately one hour).
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceIds
    *           Instance ID to terminate.
    * @see #describeInstancesInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-TerminateInstances.html"
    *      />
    */
   Set<InstanceStateChange> terminateInstancesInRegion(Region region, String... instanceIds);

   /**
    * Stops an instance that uses an Amazon EBS volume as its root device.
    * <p/>
    * Instances that use Amazon EBS volumes as their root devices can be quickly stopped and
    * started. When an instance is stopped, the compute resources are released and you are not
    * billed for hourly instance usage. However, your root partition Amazon EBS volume remains,
    * continues to persist your data, and you are charged for Amazon EBS volume usage. You can
    * restart your instance at any time.
    * <h3>Note</h3>
    * Before stopping an instance, make sure it is in a state from which it can be restarted.
    * Stopping an instance does not preserve data stored in RAM.
    * <p/>
    * Performing this operation on an instance that uses an instance store as its root device
    * returns an error.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param force
    *           Forces the instance to stop. The instance will not have an opportunity to flush file
    *           system caches nor file system meta data. If you use this option, you must perform
    *           file system check and repair procedures. This option is not recommended for Windows
    *           instances.
    * @param instanceIds
    *           Instance ID to stop.
    * 
    * @see #startInstancesInRegion
    * @see #runInstancesInRegion
    * @see #describeInstancesInRegion
    * @see #terminateInstancesInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-StopInstances.html"
    *      />
    */
   Set<InstanceStateChange> stopInstancesInRegion(Region region, boolean force,
            String... instanceIds);

   /**
    * Requests a reboot of one or more instances. This operation is asynchronous; it only queues a
    * request to reboot the specified instance(s). The operation will succeed if the instances are
    * valid and belong to you. Requests to reboot terminated instances are ignored. <h3>Note</h3> If
    * a Linux/UNIX instance does not cleanly shut down within four minutes, Amazon EC2 will perform
    * a hard reboot.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * 
    * @param instanceIds
    *           Instance ID to reboot.
    * 
    * @see #startInstancesInRegion
    * @see #runInstancesInRegion
    * @see #describeInstancesInRegion
    * @see #terminateInstancesInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-StopInstances.html"
    *      />
    */
   void rebootInstancesInRegion(Region region, String... instanceIds);

   /**
    * Starts an instance that uses an Amazon EBS volume as its root device.
    * <p/>
    * Instances that use Amazon EBS volumes as their root devices can be quickly stopped and
    * started. When an instance is stopped, the compute resources are released and you are not
    * billed for hourly instance usage. However, your root partition Amazon EBS volume remains,
    * continues to persist your data, and you are charged for Amazon EBS volume usage. You can
    * restart your instance at any time.
    * <h3>Note</h3>
    * Before stopping an instance, make sure it is in a state from which it can be restarted.
    * Stopping an instance does not preserve data stored in RAM.
    * <p/>
    * Performing this operation on an instance that uses an instance store as its root device
    * returns an error.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceIds
    *           Instance ID to start.
    * 
    * @see #stopInstancesInRegion
    * @see #runInstancesInRegion
    * @see #describeInstancesInRegion
    * @see #terminateInstancesInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-StartInstances.html"
    *      />
    */
   Set<InstanceStateChange> startInstancesInRegion(Region region, String... instanceIds);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return unencoded user data
    */
   String getUserDataForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return The root device name (e.g., /dev/sda1).
    */
   String getRootDeviceNameForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return the ID of the RAM disk associated with the AMI.
    */
   String getRamdiskForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return the ID of the kernel associated with the AMI.
    */
   String getKernelForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return Specifies whether the instance can be terminated using the APIs. You must modify this
    *         attribute before you can terminate any "locked" instances from the APIs.
    */
   boolean isApiTerminationDisabledForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return The instance type of the instance.
    */
   InstanceType getInstanceTypeForInstanceInRegion(Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return whether the instance's Amazon EBS volumes are stopped or terminated when the instance
    *         is shut down.
    */
   InstanceInitiatedShutdownBehavior getInstanceInitiatedShutdownBehaviorForInstanceInRegion(
            Region region, String instanceId);

   /**
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to describe the attribute of
    * @return Describes the mapping that defines native device names to use when exposing virtual
    *         devices.
    */
   Map<String, EbsBlockDevice> getBlockDeviceMappingForInstanceInRegion(Region region,
            String instanceId);

   /**
    * Resets an attribute of an instance to its default value.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to reset the attribute of
    * @return the ID of the RAM disk associated with the AMI.
    */
   String resetRamdiskForInstanceInRegion(Region region, String instanceId);

   /**
    * Resets an attribute of an instance to its default value.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to reset the attribute of
    * @return the ID of the kernel associated with the AMI.
    */
   String resetKernelForInstanceInRegion(Region region, String instanceId);

   /**
    * Sets the userData used for starting the instance.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param unencodedData
    *           unencoded data to set as userData
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setUserDataForInstanceInRegion(Region region, String instanceId, byte[] unencodedData);

   /**
    * Sets the ramdisk used for starting the instance.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param ramdisk
    *           ramdisk used to start the instance
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setRamdiskForInstanceInRegion(Region region, String instanceId, String ramdisk);

   /**
    * Sets the kernelId used for starting the instance.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param kernel
    *           kernelId used to start the instance
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setKernelForInstanceInRegion(Region region, String instanceId, String kernel);

   /**
    * This command works while the instance is running and controls whether or not the api can be
    * used to terminate the instance.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to reset the attribute of
    * @param apiTerminationDisabled
    *           true to disable api termination
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setApiTerminationDisabledForInstanceInRegion(Region region, String instanceId,
            boolean apiTerminationDisabled);

   /**
    * Sets the instanceType used for starting the instance.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param instanceType
    *           instanceType used to start the instance
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setInstanceTypeForInstanceInRegion(Region region, String instanceId,
            InstanceType instanceType);

   /**
    * Specifies whether the instance's Amazon EBS volumes are stopped or terminated when the
    * instance is shut down.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param instanceInitiatedShutdownBehavior
    *           whether the instance's Amazon EBS volumes are stopped or terminated when the
    *           instance is shut down.
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setInstanceInitiatedShutdownBehaviorForInstanceInRegion(Region region, String instanceId,
            InstanceInitiatedShutdownBehavior instanceInitiatedShutdownBehavior);

   /**
    * Sets the blockDeviceMapping used for starting the instance.
    * <p/>
    * The instance needs to be in a {@link InstanceState#STOPPED} state, which implies two things:
    * <ol>
    * <li>The instance was launched from an EBS-backed AMI so that it can stop</li>
    * <li>You have stopped and waited for the instance to transition from
    * {@link InstanceState#STOPPING} to {@link InstanceState#STOPPED}</li>
    * </ol>
    * 
    * The {@code blockDeviceMapping} option takes a value in the following format:
    * <device-name>=<snapshot-id>[:<volume-size>[:<deleteOnTermination>]]
    * <ol>
    * <li>device-name - this is the device name as it should be exposed to the instance, for example
    * /dev/sdb. This is the same as the device field specified in the AttachVolume call today. This
    * field also serves as the key for the structure.</li>
    * <li>snapshot-id - the ID of the EBS snapshot to be used when creating the volume. This field
    * is optional. If it is not specified, the volume-size field must be present to create a blank
    * volume of the specified size.</li>
    * <li>volume-size - the size (GiBs) of the volume. This field is optional unless no snapshot-id
    * is present. If a snapshot-id is present, the size must be equal to or larger than the
    * snapshot's volume size.</li>
    * <li>delete-on-termination - this indicates whether the volume should be deleted on
    * termination. It defaults to ' true '.</li>
    * </ol>
    * <p/>
    * Note that the device names between Linux and Windows differ. For Linux, ensure that your
    * device name is in the form /dev/sd[a-z] . For example, /dev/sda , /dev/sdb and /dev/sdh are
    * all valid device names.
    * <p/>
    * For Windows, the root device is still referred to as /dev/sda1 . For other devices, ensure
    * that they are in the form /xvd[c-p] . For example, /xvde , /xvdf and /xvdp are all valid
    * Windows device names.
    * <p/>
    * Here are a few extra examples on how this functionality can be used.
    * 
    * <ol>
    * <li>
    * resize the root volume: @{code /dev/sda1=:100}</li>
    * <li>
    * don't delete the root volume: {@code /dev/sda1=:100:false}</li>
    * <li>
    * TODO: unverified: create and attach an additional volume at launch: {@code
    * /dev/sdb=snap-e8a23d81,/dev/sdc=:200}
    * <p/>
    * The above example will create and attach the following EBS volumes at launch time:
    * <ol>
    * <li>
    * /dev/sdb - a 10 GiBs EBS volume containing an ext3 file system; this is an Amazon shared
    * snapshot.</li>
    * <li>
    * /dev/sdc - an empty 200 GiB EBS volume.</li>
    * </ol>
    * </li>
    * <li>
    * TODO: unverified: cresize the root partition of a Windows 2008 image and add an additional 100
    * GiB device: {@code /dev/sda1=:100,/dev/xvdc=:100}</li>
    * </ol>
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @param instanceId
    *           which instance to change the attribute of
    * @param blockDeviceMapping
    *           blockDeviceMapping used to start the instance
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyInstanceAttribute.html"
    *      />
    */
   void setBlockDeviceMappingForInstanceInRegion(Region region, String instanceId,
            String blockDeviceMapping);
}
