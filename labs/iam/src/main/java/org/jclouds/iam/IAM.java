package org.jclouds.iam;

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.collect.PaginatedIterables;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserApi;
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
    * @param userApi
    *           the {@link UserApi} to use for the request
    * @param options
    *           the {@link ListUsersOptions} describing the ListUsers request
    * 
    * @return iterable of users fitting the criteria
    */
   public static Iterable<User> list(final UserApi userApi, final ListUsersOptions options) {
      return PaginatedIterables.lazyContinue(userApi.list(options), new Function<Object, PaginatedIterable<User>>() {

         @Override
         public PaginatedIterable<User> apply(Object input) {
            return userApi.list(options.clone().afterMarker(input));
         }

         @Override
         public String toString() {
            return "listUsers(" + options + ")";
         }
      });
   }

}
