/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.parser;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class AmazonEC2QueryAPIExpectations {
   protected final Set<String> queryNames;

   public AmazonEC2QueryAPIExpectations() {
      queryNames = Sets.newHashSet();
      for (Set<String> qNames : expectedQueryNamesForCategoryName.values()) {
         queryNames.addAll(qNames);
      }
   }

   // Please do not reformat this class, as it will not likely look as nice.
   protected static final Map<String, Set<String>> expectedQueryNamesForCategoryName = new ImmutableMap.Builder<String, Set<String>>()
            .put("Amazon DevPay", ImmutableSet.of("ConfirmProductInstance")).put(
                     "AMIs",
                     ImmutableSet.of("DeregisterImage", "ModifyImageAttribute",
                              "DescribeImageAttribute", "DescribeImages")).put(
                     "Availability Zones and Regions",
                     ImmutableSet.of("DescribeAvailabilityZones", "DescribeRegions")).put(
                     "Elastic Block Store",
                     ImmutableSet.of("DeleteVolume", "DescribeSnapshots", "DescribeVolumes",
                              "DetachVolume", "AttachVolume", "CreateSnapshot", "CreateVolume",
                              "DeleteSnapshot")).put(
                     "Elastic IP Addresses",
                     ImmutableSet.of("AllocateAddress", "DisassociateAddress", "ReleaseAddress",
                              "AssociateAddress", "DescribeAddresses")).put("General",
                     ImmutableSet.of("GetConsoleOutput")).put("Images",
                     ImmutableSet.of("ResetImageAttribute", "RegisterImage")).put(
                     "Instances",
                     ImmutableSet.of("TerminateInstances", "DescribeInstances", "RunInstances",
                              "RebootInstances")).put("Key Pairs",
                     ImmutableSet.of("DescribeKeyPairs", "CreateKeyPair", "DeleteKeyPair")).put(
                     "Monitoring", ImmutableSet.of("MonitorInstances", "UnmonitorInstances")).put(
                     "Reserved Instances",
                     ImmutableSet.of("DescribeReservedInstances",
                              "DescribeReservedInstancesOfferings",
                              "PurchaseReservedInstancesOffering")).put(
                     "Security Groups",
                     ImmutableSet.of("DescribeSecurityGroups", "AuthorizeSecurityGroupIngress",
                              "CreateSecurityGroup", "DeleteSecurityGroup",
                              "RevokeSecurityGroupIngress")).put("Windows",
                     ImmutableSet.of("CancelBundleTask", "DescribeBundleTasks", "BundleInstance"))
            .build();
   protected static final Map<String, Set<String>> expectedFieldNamesForDataTypeName = new ImmutableMap.Builder<String, Set<String>>()
            .put("ReservationSetType", ImmutableSet.of("item")).put("DeleteKeyPairResponse",
                     ImmutableSet.of("return", "requestId")).put(
                     "DescribeKeyPairsResponseInfoType", ImmutableSet.of("item")).put(
                     "AuthorizeSecurityGroupIngressResponse",
                     ImmutableSet.of("return", "requestId")).put("AttachmentSetItemResponseType",
                     ImmutableSet.of("device", "status", "volumeId", "instanceId", "attachTime"))
            .put("DescribeAddressesResponseInfoType", ImmutableSet.of("item")).put(
                     "DescribeReservedInstancesResponse",
                     ImmutableSet.of("requestId", "reservedInstancesSet")).put(
                     "DescribeVolumesSetItemResponseType",
                     ImmutableSet.of("status", "size", "createTime", "snapshotId",
                              "availabilityZone", "volumeId", "attachmentSet")).put(
                     "LaunchPermissionItemType", ImmutableSet.of("group", "userId")).put(
                     "DescribeSnapshotsSetItemResponseType",
                     ImmutableSet.of("status", "snapshotId", "volumeId", "startTime", "progress"))
            .put(
                     "RunningInstancesItemType",
                     ImmutableSet.of("dnsName", "instanceState", "launchTime", "privateDnsName",
                              "reason", "monitoring", "platform", "productCodes", "amiLaunchIndex",
                              "keyName", "ramdiskId", "kernelId", "imageId", "instanceType",
                              "instanceId", "placement")).put(
                     "DescribeReservedInstancesOfferingsResponse",
                     ImmutableSet.of("requestId", "reservedInstancesOfferingsSet")).put(
                     "BlockDeviceMappingType", ImmutableSet.of("item")).put(
                     "ResetImageAttributeResponse", ImmutableSet.of("requestId", "imageId")).put(
                     "AvailabilityZoneSetType", ImmutableSet.of("item")).put(
                     "RevokeSecurityGroupIngressResponse", ImmutableSet.of("return", "requestId"))
            .put("ReservationInfoType",
                     ImmutableSet.of("groupSet", "reservationId", "instancesSet", "ownerId")).put(
                     "RebootInstancesResponse", ImmutableSet.of("return", "requestId")).put(
                     "GroupItemType", ImmutableSet.of("groupId")).put("AvailabilityZoneItemType",
                     ImmutableSet.of("regionName", "zoneName", "zoneState")).put(
                     "RunningInstancesSetType", ImmutableSet.of("item")).put(
                     "CreateSecurityGroupResponse", ImmutableSet.of("return", "requestId")).put(
                     "ReleaseAddressResponse", ImmutableSet.of("return", "requestId")).put(
                     "SecurityGroupItemType",
                     ImmutableSet.of("groupName", "ipPermissions", "groupDescription", "ownerId"))
            .put("NullableAttributeValueType", ImmutableSet.of("value")).put(
                     "DescribeSnapshotsResponse", ImmutableSet.of("requestId", "snapshotSet")).put(
                     "BundleInstanceResponse", ImmutableSet.of("bundleInstanceTask", "requestId"))
            .put("DescribeKeyPairsResponseItemType", ImmutableSet.of("keyName", "keyFingerprint"))
            .put("BundleInstanceTasksSetType", ImmutableSet.of("bundleInstanceTask"))
            .put("DescribeAddressesResponseItemType", ImmutableSet.of("publicIp", "instanceId"))
            .put("MonitorInstancesResponse", ImmutableSet.of("requestId", "instancesSet")).put(
                     "DescribeImagesResponse", ImmutableSet.of("imagesSet", "requestId")).put(
                     "MonitorInstancesResponseSetItemType",
                     ImmutableSet.of("monitoring", "instanceId")).put(
                     "BundleInstanceTaskErrorType", ImmutableSet.of("code", "message")).put(
                     "ProductCodesSetType", ImmutableSet.of("item")).put(
                     "UnmonitorInstancesResponse", ImmutableSet.of("instancesSet", "requestId"))
            .put(
                     "IpPermissionType",
                     ImmutableSet.of("groups", "portRange", "fromPort", "ipProtocol", "icmpPort",
                              "toPort", "ipRanges")).put("IpPermissionSetType",
                     ImmutableSet.of("item")).put("DeleteSecurityGroupResponse",
                     ImmutableSet.of("requestId", "return")).put(
                     "RunInstancesResponse",
                     ImmutableSet.of("instancesSet", "requesterId", "groupSet", "reservationId",
                              "requestId", "ownerId")).put("DeregisterImageResponse",
                     ImmutableSet.of("return", "requestId")).put("TerminateInstancesResponse",
                     ImmutableSet.of("requestId", "instancesSet")).put(
                     "DescribeSnapshotsSetResponseType", ImmutableSet.of("item")).put(
                     "DescribeReservedInstancesOfferingsResponseSetType", ImmutableSet.of("item"))
            .put(
                     "BundleInstanceTaskType",
                     ImmutableSet.of("updateTime", "startTime", "progress", "instanceId", "state",
                              "bundleId", "storage", "error")).put(
                     "ConfirmProductInstanceResponse",
                     ImmutableSet.of("ownerId", "requestId", "return")).put(
                     "CreateKeyPairResponse",
                     ImmutableSet.of("keyName", "keyMaterial", "requestId", "keyFingerprint")).put(
                     "RegisterImageResponse", ImmutableSet.of("imageId", "requestId")).put(
                     "IpRangeSetType", ImmutableSet.of("item")).put("RegionSetType",
                     ImmutableSet.of("item")).put("InstanceStateType",
                     ImmutableSet.of("name", "code")).put(
                     "DescribeReservedInstancesOfferingsResponseSetItemType",
                     ImmutableSet.of("productDescription", "reservedInstancesOfferingId",
                              "usagePrice", "fixedPrice", "availabilityZone", "duration",
                              "instanceType")).put(
                     "BundleInstanceS3StorageType",
                     ImmutableSet.of("awsAccessKeyId", "uploadPolicy", "secret-access-key",
                              "bucket", "prefix", "uploadPolicySignature")).put(
                     "DescribeVolumesResponse", ImmutableSet.of("requestId", "volumeSet")).put(
                     "TerminateInstancesResponseInfoType", ImmutableSet.of("item")).put(
                     "DeleteSnapshotResponse", ImmutableSet.of("return", "requestId")).put(
                     "BundleInstanceTaskStorageType", ImmutableSet.of("S3")).put(
                     "DescribeAvailabilityZonesResponse",
                     ImmutableSet.of("requestId", "availabilityZoneInfo")).put(
                     "CreateVolumeResponse",
                     ImmutableSet.of("snapshotId", "size", "volumeId", "availabilityZone",
                              "requestId", "createTime", "status")).put(
                     "DescribeReservedInstancesResponseSetType", ImmutableSet.of("item")).put(
                     "DescribeAddressesResponse", ImmutableSet.of("requestId", "addressesSet"))
            .put("InstanceMonitoringStateType", ImmutableSet.of("state")).put(
                     "DetachVolumeResponse",
                     ImmutableSet.of("status", "requestId", "instanceId", "volumeId", "device",
                              "attachTime")).put("DescribeKeyPairsResponse",
                     ImmutableSet.of("requestId", "keySet")).put(
                     "AttachVolumeResponse",
                     ImmutableSet.of("device", "attachTime", "requestId", "volumeId", "status",
                              "instanceId")).put("DescribeBundleTasksResponse",
                     ImmutableSet.of("bundleInstanceTasksSet", "requestId")).put(
                     "TerminateInstancesResponseItemType",
                     ImmutableSet.of("shutdownState", "previousState", "instanceId")).put(
                     "SecurityGroupSetType", ImmutableSet.of("item")).put(
                     "AttachmentSetResponseType", ImmutableSet.of("item")).put(
                     "DescribeImageAttributeResponse",
                     ImmutableSet.of("kernel", "productCodes", "requestId", "ramdisk",
                              "launchPermission", "blockDeviceMapping", "imageId")).put(
                     "PurchaseReservedInstancesOfferingResponse",
                     ImmutableSet.of("reservedInstancesId", "requestId")).put(
                     "CreateSnapshotResponse",
                     ImmutableSet.of("requestId", "status", "startTime", "volumeId", "snapshotId",
                              "progress")).put("GroupSetType", ImmutableSet.of("item")).put(
                     "ProductCodesSetItemType", ImmutableSet.of("productCode")).put(
                     "AllocateAddressResponse", ImmutableSet.of("requestId", "publicIp")).put(
                     "ProductCodeItemType", ImmutableSet.of("productCode")).put(
                     "UserIdGroupPairType", ImmutableSet.of("userId", "groupName ")).put(
                     "DescribeVolumesSetResponseType", ImmutableSet.of("item")).put(
                     "ProductCodeListType", ImmutableSet.of("item")).put("DescribeRegionsResponse",
                     ImmutableSet.of("requestId", "regionInfo")).put("RegionItemType",
                     ImmutableSet.of("regionEndpoint", "regionName")).put(
                     "DescribeInstancesResponse", ImmutableSet.of("requestId", "reservationSet"))
            .put(
                     "DescribeImagesResponseItemType",
                     ImmutableSet.of("kernelId", "productCodes", "isPublic", "imageId",
                              "ramdiskId", "imageOwnerId", "platform", "imageType", "imageState",
                              "imageLocation", "architecture")).put("LaunchPermissionListType",
                     ImmutableSet.of("item")).put("BlockDeviceMappingItemType",
                     ImmutableSet.of("virtualName", "deviceName")).put("DeleteVolumeResponse",
                     ImmutableSet.of("return", "requestId")).put("DescribeImagesResponseInfoType",
                     ImmutableSet.of("item")).put("IpRangeItemType", ImmutableSet.of("cidrIp "))
            .put("DisassociateAddressResponse", ImmutableSet.of("requestId", "return")).put(
                     "UserIdGroupPairSetType", ImmutableSet.of("item"))
            .put("CancelBundleTaskResponse", ImmutableSet.of("bundleInstanceTask", "requestId"))
            .put("MonitorInstancesResponseSetType", ImmutableSet.of("item")).put(
                     "DescribeReservedInstancesResponseSetItemType",
                     ImmutableSet.of("state", "duration", "usagePrice", "instanceType",
                              "instanceCount", "productDescription", "reservedInstancesId",
                              "fixedPrice", "availabilityZone", "start")).put(
                     "DescribeSecurityGroupsResponse",
                     ImmutableSet.of("requestId", "securityGroupInfo")).put(
                     "GetConsoleOutputResponse",
                     ImmutableSet.of("requestId", "output", "instanceId", "timestamp")).put(
                     "ModifyImageAttributeResponse", ImmutableSet.of("requestId", "return"))
            .put("AssociateAddressResponse", ImmutableSet.of("requestId", "publicIp", "return"))
            .put("ConfirmProductInstance", ImmutableSet.of("ProductCode", "InstanceId")).put(
                     "DeregisterImage", ImmutableSet.of("ImageId")).put(
                     "ModifyImageAttribute",
                     ImmutableSet.of("Group.n", "UserId.n", "ImageId", "ProductCode.n", "Group.n",
                              "UserId.n")).put("DescribeImageAttribute",
                     ImmutableSet.of("Attribute", "ImageId")).put("DescribeImages",
                     ImmutableSet.of("explicit", "public", "implicit")).put(
                     "DescribeAvailabilityZones", ImmutableSet.of("ZoneName")).put(
                     "DescribeRegions", ImmutableSet.of("RegionName.n")).put("DeleteVolume",
                     ImmutableSet.of("VolumeId")).put("DescribeSnapshots",
                     ImmutableSet.of("SnapshotId.n")).put("DescribeVolumes",
                     ImmutableSet.of("VolumeId")).put("DetachVolume",
                     ImmutableSet.of("Device", "VolumeId", "InstanceId", "Force")).put(
                     "AttachVolume", ImmutableSet.of("InstanceId", "Device", "VolumeId")).put(
                     "CreateSnapshot", ImmutableSet.of("VolumeId")).put("CreateVolume",
                     ImmutableSet.of("SnapshotId", "Size", "AvailabilityZone")).put(
                     "DeleteSnapshot", ImmutableSet.of("SnapshotId")).put("AllocateAddress",
                     ImmutableSet.of("AllocateAddressResponse", "requestId", "publicIp")).put(
                     "DisassociateAddress", ImmutableSet.of("PublicIp")).put("ReleaseAddress",
                     ImmutableSet.of("PublicIp")).put("AssociateAddress",
                     ImmutableSet.of("InstanceId", "PublicIp")).put("DescribeAddresses",
                     ImmutableSet.of("PublicIp.n")).put("GetConsoleOutput",
                     ImmutableSet.of("InstanceId")).put("ResetImageAttribute",
                     ImmutableSet.of("ImageId")).put("RegisterImage",
                     ImmutableSet.of("ImageLocation")).put("TerminateInstances",
                     ImmutableSet.of("InstanceId.n")).put("DescribeInstances",
                     ImmutableSet.of("InstanceId.n")).put(
                     "RunInstances",
                     ImmutableSet.of("Monitoring.Enabled", "Encoding", "AvailabilityZone",
                              "KernelId", "MaxCount", "Data", "ImageId", "InstanceType",
                              "MinCount", "groupId", "VirtualName", "AddressingType", "KeyName",
                              "DeviceName", "Version", "RamdiskId")).put("RebootInstances",
                     ImmutableSet.of("InstanceId.n")).put("DescribeKeyPairs",
                     ImmutableSet.of("KeyName.n")).put("CreateKeyPair", ImmutableSet.of("KeyName"))
            .put("DeleteKeyPair", ImmutableSet.of("KeyName")).put("MonitorInstances",
                     ImmutableSet.of("InstanceId.n")).put("UnmonitorInstances",
                     ImmutableSet.of("InstanceId.n")).put("DescribeReservedInstances",
                     ImmutableSet.of("ReservedInstancesId.n")).put(
                     "DescribeReservedInstancesOfferings",
                     ImmutableSet.of("ProductDescription", "ReservedInstancesOfferingId",
                              "AvailabilityZone", "InstanceType")).put(
                     "PurchaseReservedInstancesOffering",
                     ImmutableSet.of("ReservedInstancesOfferingId.n", "InstanceCount.n")).put(
                     "DescribeSecurityGroups", ImmutableSet.of("GroupName.n")).put(
                     "AuthorizeSecurityGroupIngress",
                     ImmutableSet.of("GroupName", "GroupName", "UserId", "IpProtocol", "CidrIp",
                              "ToPort", "FromPort", "UserId")).put("CreateSecurityGroup",
                     ImmutableSet.of("GroupDescription", "GroupName")).put("DeleteSecurityGroup",
                     ImmutableSet.of("GroupName")).put(
                     "RevokeSecurityGroupIngress",
                     ImmutableSet.of("IpProtocol", "GroupName", "UserId", "CidrIp", "FromPort",
                              "ToPort", "GroupName", "UserId")).put("CancelBundleTask",
                     ImmutableSet.of("BundleId")).put("DescribeBundleTasks",
                     ImmutableSet.of("BundleId")).put(
                     "BundleInstance",
                     ImmutableSet.of("Storage.S3.UploadPolicy", "InstanceId", "Storage.S3.Prefix",
                              "Storage.S3.AWSAccessKeyId", "Storage.S3.Bucket",
                              "Storage.S3.UploadPolicySignature")).build();
}