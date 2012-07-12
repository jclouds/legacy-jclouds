package org.jclouds.rds;

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
    * @param instanceApi
    *           the {@link InstanceApi} to use for the request
    * @param options
    *           the {@link ListInstancesOptions} describing the ListInstances request
    * 
    * @return iterable of instances fitting the criteria
    */
   public static Iterable<Instance> listInstances(final InstanceApi instanceApi,
            final ListInstancesOptions options) {
      return PaginatedIterables.lazyContinue(instanceApi.list(options),
               new Function<Object, PaginatedIterable<Instance>>() {

                  @Override
                  public PaginatedIterable<Instance> apply(Object input) {
                     return instanceApi.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listInstances(" + options + ")";
                  }
               });
   }

   public static Iterable<Instance> listInstances(InstanceApi instanceApi) {
      return listInstances(instanceApi, new ListInstancesOptions());
   }

   /**
    * List securityGroups based on the criteria in the {@link ListSecurityGroupsOptions} passed in.
    * 
    * @param securityGroupApi
    *           the {@link SecurityGroupApi} to use for the request
    * @param options
    *           the {@link ListSecurityGroupsOptions} describing the ListSecurityGroups request
    * 
    * @return iterable of securityGroups fitting the criteria
    */
   public static Iterable<SecurityGroup> listSecurityGroups(final SecurityGroupApi securityGroupApi,
            final ListSecurityGroupsOptions options) {
      return PaginatedIterables.lazyContinue(securityGroupApi.list(options),
               new Function<Object, PaginatedIterable<SecurityGroup>>() {

                  @Override
                  public PaginatedIterable<SecurityGroup> apply(Object input) {
                     return securityGroupApi.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listSecurityGroups(" + options + ")";
                  }
               });
   }

   public static Iterable<SecurityGroup> listSecurityGroups(SecurityGroupApi securityGroupApi) {
      return listSecurityGroups(securityGroupApi, new ListSecurityGroupsOptions());
   }

   /**
    * List subnetGroups based on the criteria in the {@link ListSubnetGroupsOptions} passed in.
    * 
    * @param subnetGroupApi
    *           the {@link SubnetGroupApi} to use for the request
    * @param options
    *           the {@link ListSubnetGroupsOptions} describing the ListSubnetGroups request
    * 
    * @return iterable of subnetGroups fitting the criteria
    */
   public static Iterable<SubnetGroup> listSubnetGroups(final SubnetGroupApi subnetGroupApi,
            final ListSubnetGroupsOptions options) {
      return PaginatedIterables.lazyContinue(subnetGroupApi.list(options),
               new Function<Object, PaginatedIterable<SubnetGroup>>() {

                  @Override
                  public PaginatedIterable<SubnetGroup> apply(Object input) {
                     return subnetGroupApi.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listSubnetGroups(" + options + ")";
                  }
               });
   }

   public static Iterable<SubnetGroup> listSubnetGroups(SubnetGroupApi subnetGroupApi) {
      return listSubnetGroups(subnetGroupApi, new ListSubnetGroupsOptions());
   }

}
