package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the Snapshot createSnapshot method.
 *
 * @see org.jclouds.cloudstack.features.SnapshotClient#createSnapshot
 * @see org.jclouds.cloudstack.features.SnapshotAsyncClient#createSnapshot
 * @author Richard Downer
 */
public class CreateSnapshotOptions extends AccountInDomainOptions {

   public static final CreateSnapshotOptions NONE = new CreateSnapshotOptions(); 

   /**
    * @param policyId policy id of the snapshot, if this is null, then use MANUAL_POLICY.
    */
   public CreateSnapshotOptions policyId(String policyId) {
      this.queryParameters.replaceValues("policyid", ImmutableSet.of(policyId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account The account of the snapshot.
       * @param domainId The domain ID of the snapshot.
       */
      public static CreateSnapshotOptions accountInDomain(String account, String domainId) {
         return (CreateSnapshotOptions) new CreateSnapshotOptions().accountInDomain(account, domainId);
      }

      /**
       * @param domainId The domain ID of the snapshot.
       */
      public static CreateSnapshotOptions domainId(String domainId) {
         return (CreateSnapshotOptions) new CreateSnapshotOptions().domainId(domainId);
      }

      /**
       * @param policyId policy id of the snapshot, if this is null, then use MANUAL_POLICY.
       */
      public static CreateSnapshotOptions policyId(String policyId) {
         return new CreateSnapshotOptions().policyId(policyId);
      }
   }

}
