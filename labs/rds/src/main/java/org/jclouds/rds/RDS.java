package org.jclouds.rds;

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

import com.google.common.base.Function;

/**
 * Utilities for using RDS.
 * 
 * @author Adrian Cole
 */
public class RDS {

   /**
    * List instances based on the criteria in the {@link ListInstancesOptions} passed in.
    * 
    * @param instanceClient
    *           the {@link InstanceClient} to use for the request
    * @param options
    *           the {@link ListInstancesOptions} describing the ListInstances request
    * 
    * @return iterable of instances fitting the criteria
    */
   public static Iterable<Instance> listInstances(final InstanceClient instanceClient,
            final ListInstancesOptions options) {
      return PaginatedIterables.lazyContinue(instanceClient.list(options),
               new Function<Object, PaginatedIterable<Instance>>() {

                  @Override
                  public PaginatedIterable<Instance> apply(Object input) {
                     return instanceClient.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listInstances(" + options + ")";
                  }
               });
   }

   public static Iterable<Instance> listInstances(InstanceClient instanceClient) {
      return listInstances(instanceClient, new ListInstancesOptions());
   }

   /**
    * List securityGroups based on the criteria in the {@link ListSecurityGroupsOptions} passed in.
    * 
    * @param securityGroupClient
    *           the {@link SecurityGroupClient} to use for the request
    * @param options
    *           the {@link ListSecurityGroupsOptions} describing the ListSecurityGroups request
    * 
    * @return iterable of securityGroups fitting the criteria
    */
   public static Iterable<SecurityGroup> listSecurityGroups(final SecurityGroupClient securityGroupClient,
            final ListSecurityGroupsOptions options) {
      return PaginatedIterables.lazyContinue(securityGroupClient.list(options),
               new Function<Object, PaginatedIterable<SecurityGroup>>() {

                  @Override
                  public PaginatedIterable<SecurityGroup> apply(Object input) {
                     return securityGroupClient.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listSecurityGroups(" + options + ")";
                  }
               });
   }

   public static Iterable<SecurityGroup> listSecurityGroups(SecurityGroupClient securityGroupClient) {
      return listSecurityGroups(securityGroupClient, new ListSecurityGroupsOptions());
   }

   /**
    * List subnetGroups based on the criteria in the {@link ListSubnetGroupsOptions} passed in.
    * 
    * @param subnetGroupClient
    *           the {@link SubnetGroupClient} to use for the request
    * @param options
    *           the {@link ListSubnetGroupsOptions} describing the ListSubnetGroups request
    * 
    * @return iterable of subnetGroups fitting the criteria
    */
   public static Iterable<SubnetGroup> listSubnetGroups(final SubnetGroupClient subnetGroupClient,
            final ListSubnetGroupsOptions options) {
      return PaginatedIterables.lazyContinue(subnetGroupClient.list(options),
               new Function<Object, PaginatedIterable<SubnetGroup>>() {

                  @Override
                  public PaginatedIterable<SubnetGroup> apply(Object input) {
                     return subnetGroupClient.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listSubnetGroups(" + options + ")";
                  }
               });
   }

   public static Iterable<SubnetGroup> listSubnetGroups(SubnetGroupClient subnetGroupClient) {
      return listSubnetGroups(subnetGroupClient, new ListSubnetGroupsOptions());
   }

}
