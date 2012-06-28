package org.jclouds.iam;

import org.jclouds.collect.PaginatedSet;
import org.jclouds.collect.PaginatedSets;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserClient;
import org.jclouds.iam.options.ListUsersOptions;

import com.google.common.base.Function;

/**
 * Utilities for using IAM.
 * 
 * @author Adrian Cole
 */
public class IAM {

   /**
    * List users based on the criteria in the {@link ListUsersOptions} passed in.
    * 
    * @param userClient
    *           the {@link UserClient} to use for the request
    * @param options
    *           the {@link ListUsersOptions} describing the ListUsers request
    * 
    * @return iterable of users fitting the criteria
    */
   public static Iterable<User> list(final UserClient userClient, final ListUsersOptions options) {
      return PaginatedSets.lazyContinue(userClient.list(options), new Function<String, PaginatedSet<User>>() {

         @Override
         public PaginatedSet<User> apply(String input) {
            return userClient.list(options.clone().marker(input));
         }

         @Override
         public String toString() {
            return "listUsers(" + options + ")";
         }
      });
   }

}
