package org.jclouds.vcloud.director.v1_5.domain;

import java.util.Arrays;
import java.util.List;

import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryClient;

public class Role { //TODO: placeholder for implementation
   
   public static final class DefaultRoles {
      public static final String USER = "vApp User";
      public static final String AUTHOR = "vApp Author";
      public static final String CATALOG_AUTHOR = "Catalog Author";
      public static final String CONSOLE = "Console Access Only";
      public static final String ORG_ADMIN = "Organization Administrator";
      
      /**
       * All default {@link AdminQueryClient#roleReferencesQueryAll()} values.
       * <p/>
       * This list must be updated whenever a new default role is added.
       */
      public static final List<String> ALL = Arrays.asList(
            USER, AUTHOR, CATALOG_AUTHOR, CONSOLE, ORG_ADMIN
      );
   }
   
}
