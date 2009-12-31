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
package org.jclouds.codegen.ec2.queryapi.parser;

import org.jclouds.codegen.ec2.queryapi.Category;
import org.jclouds.codegen.ec2.queryapi.Content;
import org.jclouds.codegen.ec2.queryapi.DataType;
import org.jclouds.codegen.ec2.queryapi.Query;
import org.jclouds.codegen.ec2.queryapi.parser.AmazonEC2QueryAPIParser;
import org.jclouds.codegen.ec2.queryapi.parser.AmazonEC2QueryAPIValidator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests to ensure that the json file parsed from the EC2 site appears correct.
 * 
 * @author Adrian Cole
 */
@Test(testName = "ec2.AmazonEC2QueryAPIParserTest")
public class AmazonEC2QueryAPIParserTest extends AmazonEC2QueryAPIValidator {
   @BeforeTest
   public void setUp() throws Exception {
      setModel(new AmazonEC2QueryAPIParser().parseJSONResource("/objmodels/ec2.json"));
   }

   @Test
   public void testCategoriesParsedNames() {
      super.validateCategories();
   }

   @AfterTest
   public void tearDown() throws Exception {
      setModel(null);
   }

   @SuppressWarnings("unused")
   private void codeUsedToGenerateThisClass() {
      StringBuilder builder = new StringBuilder();
      builder
               .append("   private Map<String, Set<String>> expectedQueryNamesForCategoryName = new ImmutableMap.Builder");
      builder.append("<String, Set<String>>()").append("\n");

      for (Category category : getModel().getCategories().values()) {
         builder.append(String.format(".put(\"%1$s\",%n   ImmutableSet.of(", category.getName()));
         for (String string : category.getQueries().keySet()) {
            builder.append(String.format("\"%1$s\",", string));
         }
         builder.append(String.format("))%n"));
      }
      builder.append(String.format(".build();"));
      System.out.println(builder.toString().replaceAll(",\\)", ")"));

      builder = new StringBuilder();
      builder
               .append("   private Map<String, Set<String>> expectedFieldNamesForBeanName = new ImmutableMap.Builder");
      builder.append("<String, Set<String>>()").append("\n");

      for (DataType bean : getModel().getDataTypes().values()) {
         builder.append(String.format(".put(\"%1$s\",%n   ImmutableSet.of(", bean.getType()));
         for (Content field : bean.getContents()) {
            builder.append(String.format("\"%1$s\",", field.getName()));
         }
         builder.append(String.format("))%n"));
      }
      for (Category category : getModel().getCategories().values()) {
         for (DataType bean : category.getQueries().values()) {
            builder.append(String.format(".put(\"%1$s\",%n   ImmutableSet.of(", bean.getType()));
            for (Content field : bean.getContents()) {
               builder.append(String.format("\"%1$s\",", field.getName()));
            }
            builder.append(String.format("))%n"));
         }
      }
      builder.append(String.format(".build();"));
      System.out.println(builder.toString().replaceAll(",\\)", ")"));

      for (String string : getModel().getCategories().keySet()) {
         System.out.printf("   @Test%n   public void test%1$sQueryNames(){%n  "
                  + "validateQueriesInCategory(\"%2$s\");" + "}%n", string.replaceAll(" ", ""),
                  string);
      }

      for (Category cat : getModel().getCategories().values()) {
         for (Query bean : cat.getQueries().values()) {
            System.out.printf("   @Test%n   public void test%1$sQuery(){%n  "
                     + "validateQueryInCategory(\"%1$s\", \"%2$s\");%n}%n", bean.getType(), cat
                     .getName());
         }

      }
      for (DataType bean : getModel().getDataTypes().values()) {
         System.out.printf("   @Test%n   public void test%1$sBean(){%n  "
                  + "validateFieldsOfBean(getModel().getDomain().get(\"%1$s\"));%n}%n", bean
                  .getType());
      }
      System.out.printf("         public EC2ModelValidator validateCommands() {%n");
      for (Category cat : getModel().getCategories().values()) {
         for (Query bean : cat.getQueries().values()) {
            System.out.printf("validateQueryInCategory(\"%1$s\", \"%2$s\");%n", bean.getType(), cat
                     .getName());
         }
      }
      System.out.printf("return this;%n         }%n");

      System.out.printf("         public EC2ModelValidator validateDomain() {%n");
      for (DataType bean : getModel().getDataTypes().values()) {
         System.out.printf("validateFieldsOfBean(getModel().getDomain().get(\"%1$s\"));%n", bean
                  .getType());
      }

      System.out.printf("return this;%n         }%n");

   }

   @Test
   public void testAmazonDevPayQueryNames() {
      validateQueriesInCategory("Amazon DevPay");
   }

   @Test
   public void testAMIsQueryNames() {
      validateQueriesInCategory("AMIs");
   }

   @Test
   public void testAvailabilityZonesandRegionsQueryNames() {
      validateQueriesInCategory("Availability Zones and Regions");
   }

   @Test
   public void testElasticBlockStoreQueryNames() {
      validateQueriesInCategory("Elastic Block Store");
   }

   @Test
   public void testElasticIPAddressesQueryNames() {
      validateQueriesInCategory("Elastic IP Addresses");
   }

   @Test
   public void testGeneralQueryNames() {
      validateQueriesInCategory("General");
   }

   @Test
   public void testImagesQueryNames() {
      validateQueriesInCategory("Images");
   }

   @Test
   public void testInstancesQueryNames() {
      validateQueriesInCategory("Instances");
   }

   @Test
   public void testKeyPairsQueryNames() {
      validateQueriesInCategory("Key Pairs");
   }

   @Test
   public void testMonitoringQueryNames() {
      validateQueriesInCategory("Monitoring");
   }

   @Test
   public void testReservedInstancesQueryNames() {
      validateQueriesInCategory("Reserved Instances");
   }

   @Test
   public void testSecurityGroupsQueryNames() {
      validateQueriesInCategory("Security Groups");
   }

   @Test
   public void testWindowsQueryNames() {
      validateQueriesInCategory("Windows");
   }

   @Test
   public void testConfirmProductInstanceQuery() {
      validateQueryInCategory("ConfirmProductInstance", "Amazon DevPay");
   }

   @Test
   public void testDeregisterImageQuery() {
      validateQueryInCategory("DeregisterImage", "AMIs");
   }

   @Test
   public void testModifyImageAttributeQuery() {
      validateQueryInCategory("ModifyImageAttribute", "AMIs");
   }

   @Test
   public void testDescribeImageAttributeQuery() {
      validateQueryInCategory("DescribeImageAttribute", "AMIs");
   }

   @Test
   public void testDescribeImagesQuery() {
      validateQueryInCategory("DescribeImages", "AMIs");
   }

   @Test
   public void testDescribeAvailabilityZonesQuery() {
      validateQueryInCategory("DescribeAvailabilityZones", "Availability Zones and Regions");
   }

   @Test
   public void testDescribeRegionsQuery() {
      validateQueryInCategory("DescribeRegions", "Availability Zones and Regions");
   }

   @Test
   public void testDeleteVolumeQuery() {
      validateQueryInCategory("DeleteVolume", "Elastic Block Store");
   }

   @Test
   public void testDescribeSnapshotsQuery() {
      validateQueryInCategory("DescribeSnapshots", "Elastic Block Store");
   }

   @Test
   public void testDescribeVolumesQuery() {
      validateQueryInCategory("DescribeVolumes", "Elastic Block Store");
   }

   @Test
   public void testDetachVolumeQuery() {
      validateQueryInCategory("DetachVolume", "Elastic Block Store");
   }

   @Test
   public void testAttachVolumeQuery() {
      validateQueryInCategory("AttachVolume", "Elastic Block Store");
   }

   @Test
   public void testCreateSnapshotQuery() {
      validateQueryInCategory("CreateSnapshot", "Elastic Block Store");
   }

   @Test
   public void testCreateVolumeQuery() {
      validateQueryInCategory("CreateVolume", "Elastic Block Store");
   }

   @Test
   public void testDeleteSnapshotQuery() {
      validateQueryInCategory("DeleteSnapshot", "Elastic Block Store");
   }

   @Test
   public void testAllocateAddressQuery() {
      validateQueryInCategory("AllocateAddress", "Elastic IP Addresses");
   }

   @Test
   public void testDisassociateAddressQuery() {
      validateQueryInCategory("DisassociateAddress", "Elastic IP Addresses");
   }

   @Test
   public void testReleaseAddressQuery() {
      validateQueryInCategory("ReleaseAddress", "Elastic IP Addresses");
   }

   @Test
   public void testAssociateAddressQuery() {
      validateQueryInCategory("AssociateAddress", "Elastic IP Addresses");
   }

   @Test
   public void testDescribeAddressesQuery() {
      validateQueryInCategory("DescribeAddresses", "Elastic IP Addresses");
   }

   @Test
   public void testGetConsoleOutputQuery() {
      validateQueryInCategory("GetConsoleOutput", "General");
   }

   @Test
   public void testResetImageAttributeQuery() {
      validateQueryInCategory("ResetImageAttribute", "Images");
   }

   @Test
   public void testRegisterImageQuery() {
      validateQueryInCategory("RegisterImage", "Images");
   }

   @Test
   public void testTerminateInstancesQuery() {
      validateQueryInCategory("TerminateInstances", "Instances");
   }

   @Test
   public void testDescribeInstancesQuery() {
      validateQueryInCategory("DescribeInstances", "Instances");
   }

   @Test
   public void testRunInstancesQuery() {
      validateQueryInCategory("RunInstances", "Instances");
   }

   @Test
   public void testRebootInstancesQuery() {
      validateQueryInCategory("RebootInstances", "Instances");
   }

   @Test
   public void testDescribeKeyPairsQuery() {
      validateQueryInCategory("DescribeKeyPairs", "Key Pairs");
   }

   @Test
   public void testCreateKeyPairQuery() {
      validateQueryInCategory("CreateKeyPair", "Key Pairs");
   }

   @Test
   public void testDeleteKeyPairQuery() {
      validateQueryInCategory("DeleteKeyPair", "Key Pairs");
   }

   @Test
   public void testMonitorInstancesQuery() {
      validateQueryInCategory("MonitorInstances", "Monitoring");
   }

   @Test
   public void testUnmonitorInstancesQuery() {
      validateQueryInCategory("UnmonitorInstances", "Monitoring");
   }

   @Test
   public void testDescribeReservedInstancesQuery() {
      validateQueryInCategory("DescribeReservedInstances", "Reserved Instances");
   }

   @Test
   public void testDescribeReservedInstancesOfferingsQuery() {
      validateQueryInCategory("DescribeReservedInstancesOfferings", "Reserved Instances");
   }

   @Test
   public void testPurchaseReservedInstancesOfferingQuery() {
      validateQueryInCategory("PurchaseReservedInstancesOffering", "Reserved Instances");
   }

   @Test
   public void testDescribeSecurityGroupsQuery() {
      validateQueryInCategory("DescribeSecurityGroups", "Security Groups");
   }

   @Test
   public void testAuthorizeSecurityGroupIngressQuery() {
      validateQueryInCategory("AuthorizeSecurityGroupIngress", "Security Groups");
   }

   @Test
   public void testCreateSecurityGroupQuery() {
      validateQueryInCategory("CreateSecurityGroup", "Security Groups");
   }

   @Test
   public void testDeleteSecurityGroupQuery() {
      validateQueryInCategory("DeleteSecurityGroup", "Security Groups");
   }

   @Test
   public void testRevokeSecurityGroupIngressQuery() {
      validateQueryInCategory("RevokeSecurityGroupIngress", "Security Groups");
   }

   @Test
   public void testCancelBundleTaskQuery() {
      validateQueryInCategory("CancelBundleTask", "Windows");
   }

   @Test
   public void testDescribeBundleTasksQuery() {
      validateQueryInCategory("DescribeBundleTasks", "Windows");
   }

   @Test
   public void testBundleInstanceQuery() {
      validateQueryInCategory("BundleInstance", "Windows");
   }

   @Test
   public void testReservationSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("ReservationSetType"));
   }

   @Test
   public void testDeleteKeyPairResponseBean() {
      validateDataType(getModel().getDataTypes().get("DeleteKeyPairResponse"));
   }

   @Test
   public void testDescribeKeyPairsResponseInfoTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeKeyPairsResponseInfoType"));
   }

   @Test
   public void testAuthorizeSecurityGroupIngressResponseBean() {
      validateDataType(getModel().getDataTypes().get("AuthorizeSecurityGroupIngressResponse"));
   }

   @Test
   public void testAttachmentSetItemResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("AttachmentSetItemResponseType"));
   }

   @Test
   public void testDescribeAddressesResponseInfoTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeAddressesResponseInfoType"));
   }

   @Test
   public void testDescribeReservedInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeReservedInstancesResponse"));
   }

   @Test
   public void testDescribeVolumesSetItemResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeVolumesSetItemResponseType"));
   }

   @Test
   public void testLaunchPermissionItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("LaunchPermissionItemType"));
   }

   @Test
   public void testDescribeSnapshotsSetItemResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeSnapshotsSetItemResponseType"));
   }

   @Test
   public void testRunningInstancesItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("RunningInstancesItemType"));
   }

   @Test
   public void testDescribeReservedInstancesOfferingsResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeReservedInstancesOfferingsResponse"));
   }

   @Test
   public void testBlockDeviceMappingTypeBean() {
      validateDataType(getModel().getDataTypes().get("BlockDeviceMappingType"));
   }

   @Test
   public void testResetImageAttributeResponseBean() {
      validateDataType(getModel().getDataTypes().get("ResetImageAttributeResponse"));
   }

   @Test
   public void testAvailabilityZoneSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("AvailabilityZoneSetType"));
   }

   @Test
   public void testRevokeSecurityGroupIngressResponseBean() {
      validateDataType(getModel().getDataTypes().get("RevokeSecurityGroupIngressResponse"));
   }

   @Test
   public void testReservationInfoTypeBean() {
      validateDataType(getModel().getDataTypes().get("ReservationInfoType"));
   }

   @Test
   public void testRebootInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("RebootInstancesResponse"));
   }

   @Test
   public void testGroupItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("GroupItemType"));
   }

   @Test
   public void testAvailabilityZoneItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("AvailabilityZoneItemType"));
   }

   @Test
   public void testRunningInstancesSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("RunningInstancesSetType"));
   }

   @Test
   public void testCreateSecurityGroupResponseBean() {
      validateDataType(getModel().getDataTypes().get("CreateSecurityGroupResponse"));
   }

   @Test
   public void testReleaseAddressResponseBean() {
      validateDataType(getModel().getDataTypes().get("ReleaseAddressResponse"));
   }

   @Test
   public void testSecurityGroupItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("SecurityGroupItemType"));
   }

   @Test
   public void testNullableAttributeValueTypeBean() {
      validateDataType(getModel().getDataTypes().get("NullableAttributeValueType"));
   }

   @Test
   public void testDescribeSnapshotsResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeSnapshotsResponse"));
   }

   @Test
   public void testBundleInstanceResponseBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceResponse"));
   }

   @Test
   public void testDescribeKeyPairsResponseItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeKeyPairsResponseItemType"));
   }

   @Test
   public void testBundleInstanceTasksSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceTasksSetType"));
   }

   @Test
   public void testDescribeAddressesResponseItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeAddressesResponseItemType"));
   }

   @Test
   public void testMonitorInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("MonitorInstancesResponse"));
   }

   @Test
   public void testDescribeImagesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeImagesResponse"));
   }

   @Test
   public void testMonitorInstancesResponseSetItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("MonitorInstancesResponseSetItemType"));
   }

   @Test
   public void testBundleInstanceTaskErrorTypeBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceTaskErrorType"));
   }

   @Test
   public void testProductCodesSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("ProductCodesSetType"));
   }

   @Test
   public void testUnmonitorInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("UnmonitorInstancesResponse"));
   }

   @Test
   public void testIpPermissionTypeBean() {
      validateDataType(getModel().getDataTypes().get("IpPermissionType"));
   }

   @Test
   public void testIpPermissionSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("IpPermissionSetType"));
   }

   @Test
   public void testDeleteSecurityGroupResponseBean() {
      validateDataType(getModel().getDataTypes().get("DeleteSecurityGroupResponse"));
   }

   @Test
   public void testRunInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("RunInstancesResponse"));
   }

   @Test
   public void testDeregisterImageResponseBean() {
      validateDataType(getModel().getDataTypes().get("DeregisterImageResponse"));
   }

   @Test
   public void testTerminateInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("TerminateInstancesResponse"));
   }

   @Test
   public void testDescribeSnapshotsSetResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeSnapshotsSetResponseType"));
   }

   @Test
   public void testDescribeReservedInstancesOfferingsResponseSetTypeBean() {
      validateDataType(getModel().getDataTypes().get(
               "DescribeReservedInstancesOfferingsResponseSetType"));
   }

   @Test
   public void testBundleInstanceTaskTypeBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceTaskType"));
   }

   @Test
   public void testConfirmProductInstanceResponseBean() {
      validateDataType(getModel().getDataTypes().get("ConfirmProductInstanceResponse"));
   }

   @Test
   public void testCreateKeyPairResponseBean() {
      validateDataType(getModel().getDataTypes().get("CreateKeyPairResponse"));
   }

   @Test
   public void testRegisterImageResponseBean() {
      validateDataType(getModel().getDataTypes().get("RegisterImageResponse"));
   }

   @Test
   public void testIpRangeSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("IpRangeSetType"));
   }

   @Test
   public void testRegionSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("RegionSetType"));
   }

   @Test
   public void testInstanceStateTypeBean() {
      validateDataType(getModel().getDataTypes().get("InstanceStateType"));
   }

   @Test
   public void testDescribeReservedInstancesOfferingsResponseSetItemTypeBean() {
      validateDataType(getModel().getDataTypes().get(
               "DescribeReservedInstancesOfferingsResponseSetItemType"));
   }

   @Test
   public void testBundleInstanceS3StorageTypeBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceS3StorageType"));
   }

   @Test
   public void testDescribeVolumesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeVolumesResponse"));
   }

   @Test
   public void testTerminateInstancesResponseInfoTypeBean() {
      validateDataType(getModel().getDataTypes().get("TerminateInstancesResponseInfoType"));
   }

   @Test
   public void testDeleteSnapshotResponseBean() {
      validateDataType(getModel().getDataTypes().get("DeleteSnapshotResponse"));
   }

   @Test
   public void testBundleInstanceTaskStorageTypeBean() {
      validateDataType(getModel().getDataTypes().get("BundleInstanceTaskStorageType"));
   }

   @Test
   public void testDescribeAvailabilityZonesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeAvailabilityZonesResponse"));
   }

   @Test
   public void testCreateVolumeResponseBean() {
      validateDataType(getModel().getDataTypes().get("CreateVolumeResponse"));
   }

   @Test
   public void testDescribeReservedInstancesResponseSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeReservedInstancesResponseSetType"));
   }

   @Test
   public void testDescribeAddressesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeAddressesResponse"));
   }

   @Test
   public void testInstanceMonitoringStateTypeBean() {
      validateDataType(getModel().getDataTypes().get("InstanceMonitoringStateType"));
   }

   @Test
   public void testDetachVolumeResponseBean() {
      validateDataType(getModel().getDataTypes().get("DetachVolumeResponse"));
   }

   @Test
   public void testDescribeKeyPairsResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeKeyPairsResponse"));
   }

   @Test
   public void testAttachVolumeResponseBean() {
      validateDataType(getModel().getDataTypes().get("AttachVolumeResponse"));
   }

   @Test
   public void testDescribeBundleTasksResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeBundleTasksResponse"));
   }

   @Test
   public void testTerminateInstancesResponseItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("TerminateInstancesResponseItemType"));
   }

   @Test
   public void testSecurityGroupSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("SecurityGroupSetType"));
   }

   @Test
   public void testAttachmentSetResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("AttachmentSetResponseType"));
   }

   @Test
   public void testDescribeImageAttributeResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeImageAttributeResponse"));
   }

   @Test
   public void testPurchaseReservedInstancesOfferingResponseBean() {
      validateDataType(getModel().getDataTypes().get("PurchaseReservedInstancesOfferingResponse"));
   }

   @Test
   public void testCreateSnapshotResponseBean() {
      validateDataType(getModel().getDataTypes().get("CreateSnapshotResponse"));
   }

   @Test
   public void testGroupSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("GroupSetType"));
   }

   @Test
   public void testProductCodesSetItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("ProductCodesSetItemType"));
   }

   @Test
   public void testAllocateAddressResponseBean() {
      validateDataType(getModel().getDataTypes().get("AllocateAddressResponse"));
   }

   @Test
   public void testProductCodeItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("ProductCodeItemType"));
   }

   @Test
   public void testUserIdGroupPairTypeBean() {
      validateDataType(getModel().getDataTypes().get("UserIdGroupPairType"));
   }

   @Test
   public void testDescribeVolumesSetResponseTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeVolumesSetResponseType"));
   }

   @Test
   public void testProductCodeListTypeBean() {
      validateDataType(getModel().getDataTypes().get("ProductCodeListType"));
   }

   @Test
   public void testDescribeRegionsResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeRegionsResponse"));
   }

   @Test
   public void testRegionItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("RegionItemType"));
   }

   @Test
   public void testDescribeInstancesResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeInstancesResponse"));
   }

   @Test
   public void testDescribeImagesResponseItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeImagesResponseItemType"));
   }

   @Test
   public void testLaunchPermissionListTypeBean() {
      validateDataType(getModel().getDataTypes().get("LaunchPermissionListType"));
   }

   @Test
   public void testBlockDeviceMappingItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("BlockDeviceMappingItemType"));
   }

   @Test
   public void testDeleteVolumeResponseBean() {
      validateDataType(getModel().getDataTypes().get("DeleteVolumeResponse"));
   }

   @Test
   public void testDescribeImagesResponseInfoTypeBean() {
      validateDataType(getModel().getDataTypes().get("DescribeImagesResponseInfoType"));
   }

   @Test
   public void testIpRangeItemTypeBean() {
      validateDataType(getModel().getDataTypes().get("IpRangeItemType"));
   }

   @Test
   public void testDisassociateAddressResponseBean() {
      validateDataType(getModel().getDataTypes().get("DisassociateAddressResponse"));
   }

   @Test
   public void testUserIdGroupPairSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("UserIdGroupPairSetType"));
   }

   @Test
   public void testCancelBundleTaskResponseBean() {
      validateDataType(getModel().getDataTypes().get("CancelBundleTaskResponse"));
   }

   @Test
   public void testMonitorInstancesResponseSetTypeBean() {
      validateDataType(getModel().getDataTypes().get("MonitorInstancesResponseSetType"));
   }

   @Test
   public void testDescribeReservedInstancesResponseSetItemTypeBean() {
      validateDataType(getModel().getDataTypes().get(
               "DescribeReservedInstancesResponseSetItemType"));
   }

   @Test
   public void testDescribeSecurityGroupsResponseBean() {
      validateDataType(getModel().getDataTypes().get("DescribeSecurityGroupsResponse"));
   }

   @Test
   public void testGetConsoleOutputResponseBean() {
      validateDataType(getModel().getDataTypes().get("GetConsoleOutputResponse"));
   }

   @Test
   public void testModifyImageAttributeResponseBean() {
      validateDataType(getModel().getDataTypes().get("ModifyImageAttributeResponse"));
   }

   @Test
   public void testAssociateAddressResponseBean() {
      validateDataType(getModel().getDataTypes().get("AssociateAddressResponse"));
   }
}
