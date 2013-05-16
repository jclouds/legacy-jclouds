package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the Snapshot listSnapshotPolicies method.
 *
 * @see org.jclouds.cloudstack.features.SnapshotClient#listSnapshotPolicies
 * @see org.jclouds.cloudstack.features.SnapshotAsyncClient#listSnapshotPolicies
 * @author Richard Downer
 */
public class ListSnapshotPoliciesOptions extends AccountInDomainOptions {

   public static final ListSnapshotPoliciesOptions NONE = new ListSnapshotPoliciesOptions(); 

   /**
    * @param keyword List by keyword
    */
   public ListSnapshotPoliciesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account lists snapshot policies for the specified account.
       */
      public static ListSnapshotPoliciesOptions accountInDomain(String account, String domainId) {
         return (ListSnapshotPoliciesOptions) new ListSnapshotPoliciesOptions().accountInDomain(account, domainId);
      }

      /**
       * @param domainId the domain ID.
       */
      public static ListSnapshotPoliciesOptions domainId(String domainId) {
         return (ListSnapshotPoliciesOptions) new ListSnapshotPoliciesOptions().domainId(domainId);
      }

      /**
       * @param keyword List by keyword
       */
      public static ListSnapshotPoliciesOptions keyword(String keyword) {
         return new ListSnapshotPoliciesOptions().keyword(keyword);
      }
   }

}
