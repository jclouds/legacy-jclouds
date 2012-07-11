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
import org.jclouds.rds.features.InstanceClient;
import org.jclouds.rds.features.SecurityGroupClient;
import org.jclouds.rds.features.SubnetGroupClient;
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
      InstanceClient instanceClient = createMock(InstanceClient.class);
      ListInstancesOptions options = new ListInstancesOptions();
      PaginatedIterable<Instance> response = PaginatedIterables.forward(ImmutableSet.of(createMock(Instance.class)));

      expect(instanceClient.list(options)).andReturn(response).once();

      EasyMock.replay(instanceClient);

      Assert.assertEquals(1, Iterables.size(RDS.listInstances(instanceClient, options)));
   }

   @Test
   public void testMultiPageResultInstance() throws Exception {
      InstanceClient instanceClient = createMock(InstanceClient.class);
      ListInstancesOptions options = new ListInstancesOptions();
      PaginatedIterable<Instance> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(Instance.class)), "NEXTTOKEN");
      PaginatedIterable<Instance> response2 = PaginatedIterables.forward(ImmutableSet.of(createMock(Instance.class)));

      expect(instanceClient.list(anyObject(ListInstancesOptions.class))).andReturn(response1).once();
      expect(instanceClient.list(anyObject(ListInstancesOptions.class))).andReturn(response2).once();

      EasyMock.replay(instanceClient);

      Assert.assertEquals(2, Iterables.size(RDS.listInstances(instanceClient, options)));
   }

   @Test
   public void testSinglePageResultSubnetGroup() throws Exception {
      SubnetGroupClient subnetGroupClient = createMock(SubnetGroupClient.class);
      ListSubnetGroupsOptions options = new ListSubnetGroupsOptions();
      PaginatedIterable<SubnetGroup> response = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SubnetGroup.class)));

      expect(subnetGroupClient.list(options)).andReturn(response).once();

      EasyMock.replay(subnetGroupClient);

      Assert.assertEquals(1, Iterables.size(RDS.listSubnetGroups(subnetGroupClient, options)));
   }

   @Test
   public void testMultiPageResultSubnetGroup() throws Exception {
      SubnetGroupClient subnetGroupClient = createMock(SubnetGroupClient.class);
      ListSubnetGroupsOptions options = new ListSubnetGroupsOptions();
      PaginatedIterable<SubnetGroup> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(SubnetGroup.class)), "NEXTTOKEN");
      PaginatedIterable<SubnetGroup> response2 = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SubnetGroup.class)));

      expect(subnetGroupClient.list(anyObject(ListSubnetGroupsOptions.class))).andReturn(response1).once();
      expect(subnetGroupClient.list(anyObject(ListSubnetGroupsOptions.class))).andReturn(response2).once();

      EasyMock.replay(subnetGroupClient);

      Assert.assertEquals(2, Iterables.size(RDS.listSubnetGroups(subnetGroupClient, options)));
   }

   @Test
   public void testSinglePageResultSecurityGroup() throws Exception {
      SecurityGroupClient securityGroupClient = createMock(SecurityGroupClient.class);
      ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
      PaginatedIterable<SecurityGroup> response = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SecurityGroup.class)));

      expect(securityGroupClient.list(options)).andReturn(response).once();

      EasyMock.replay(securityGroupClient);

      Assert.assertEquals(1, Iterables.size(RDS.listSecurityGroups(securityGroupClient, options)));
   }

   @Test
   public void testMultiPageResultSecurityGroup() throws Exception {
      SecurityGroupClient securityGroupClient = createMock(SecurityGroupClient.class);
      ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
      PaginatedIterable<SecurityGroup> response1 = PaginatedIterables.forwardWithMarker(
               ImmutableSet.of(createMock(SecurityGroup.class)), "NEXTTOKEN");
      PaginatedIterable<SecurityGroup> response2 = PaginatedIterables.forward(ImmutableSet
               .of(createMock(SecurityGroup.class)));

      expect(securityGroupClient.list(anyObject(ListSecurityGroupsOptions.class))).andReturn(response1).once();
      expect(securityGroupClient.list(anyObject(ListSecurityGroupsOptions.class))).andReturn(response2).once();

      EasyMock.replay(securityGroupClient);

      Assert.assertEquals(2, Iterables.size(RDS.listSecurityGroups(securityGroupClient, options)));
   }

}
