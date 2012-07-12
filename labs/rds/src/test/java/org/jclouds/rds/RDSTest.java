package org.jclouds.rds;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.jclouds.collect.PaginatedIterable;
import org.jclouds.collect.PaginatedIterables;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.features.InstanceApi;
import org.jclouds.rds.features.SecurityGroupApi;
import org.jclouds.rds.features.SubnetGroupApi;
import org.jclouds.rds.options.ListInstancesOptions;
import org.jclouds.rds.options.ListSecurityGroupsOptions;
import org.jclouds.rds.options.ListSubnetGroupsOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code RDS}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "RDSTest")
public class RDSTest {

   @Test
   public void testSinglePageResultInstance() throws Exception {
      InstanceApi instanceApi = createMock(InstanceApi.class);
      ListInstancesOptions options = new ListInstancesOptions();
      PaginatedIterable<Instance> response = PaginatedIterables.forward(ImmutableSet.of(createMock(Instance.class)));

      expect(instanceApi.list(options)).andReturn(response).once();

      EasyMock.replay(instanceApi);

      Assert.assertEquals(1, Iterables.size(RDS.listInstances(instanceApi, options)));
   }

   @Test
   public void testMultiPageResultInstance() throws Exception {
      InstanceApi instanceApi = createMock(InstanceApi.class);
      ListInstancesOptions options = new ListInstancesOptions();
      PaginatedIterable<Instance> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(Instance.class)), "NEXTTOKEN");
      PaginatedIterable<Instance> response2 = PaginatedIterables.forward(ImmutableSet.of(createMock(Instance.class)));

      expect(instanceApi.list(anyObject(ListInstancesOptions.class))).andReturn(response1).once();
      expect(instanceApi.list(anyObject(ListInstancesOptions.class))).andReturn(response2).once();

      EasyMock.replay(instanceApi);

      Assert.assertEquals(2, Iterables.size(RDS.listInstances(instanceApi, options)));
   }

   @Test
   public void testSinglePageResultSubnetGroup() throws Exception {
      SubnetGroupApi subnetGroupApi = createMock(SubnetGroupApi.class);
      ListSubnetGroupsOptions options = new ListSubnetGroupsOptions();
      PaginatedIterable<SubnetGroup> response = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SubnetGroup.class)));

      expect(subnetGroupApi.list(options)).andReturn(response).once();

      EasyMock.replay(subnetGroupApi);

      Assert.assertEquals(1, Iterables.size(RDS.listSubnetGroups(subnetGroupApi, options)));
   }

   @Test
   public void testMultiPageResultSubnetGroup() throws Exception {
      SubnetGroupApi subnetGroupApi = createMock(SubnetGroupApi.class);
      ListSubnetGroupsOptions options = new ListSubnetGroupsOptions();
      PaginatedIterable<SubnetGroup> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(SubnetGroup.class)), "NEXTTOKEN");
      PaginatedIterable<SubnetGroup> response2 = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SubnetGroup.class)));

      expect(subnetGroupApi.list(anyObject(ListSubnetGroupsOptions.class))).andReturn(response1).once();
      expect(subnetGroupApi.list(anyObject(ListSubnetGroupsOptions.class))).andReturn(response2).once();

      EasyMock.replay(subnetGroupApi);

      Assert.assertEquals(2, Iterables.size(RDS.listSubnetGroups(subnetGroupApi, options)));
   }

   @Test
   public void testSinglePageResultSecurityGroup() throws Exception {
      SecurityGroupApi securityGroupApi = createMock(SecurityGroupApi.class);
      ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
      PaginatedIterable<SecurityGroup> response = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SecurityGroup.class)));

      expect(securityGroupApi.list(options)).andReturn(response).once();

      EasyMock.replay(securityGroupApi);

      Assert.assertEquals(1, Iterables.size(RDS.listSecurityGroups(securityGroupApi, options)));
   }

   @Test
   public void testMultiPageResultSecurityGroup() throws Exception {
      SecurityGroupApi securityGroupApi = createMock(SecurityGroupApi.class);
      ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
      PaginatedIterable<SecurityGroup> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(SecurityGroup.class)), "NEXTTOKEN");
      PaginatedIterable<SecurityGroup> response2 = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SecurityGroup.class)));

      expect(securityGroupApi.list(anyObject(ListSecurityGroupsOptions.class))).andReturn(response1).once();
      expect(securityGroupApi.list(anyObject(ListSecurityGroupsOptions.class))).andReturn(response2).once();

      EasyMock.replay(securityGroupApi);

      Assert.assertEquals(2, Iterables.size(RDS.listSecurityGroups(securityGroupApi, options)));
   }

}
