package org.jclouds.vcloud.bluelock.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.VAppTemplate;

/**
 * 
 * from https://forums.bluelock.com/faq.php?faq=vcloudexpressfaq
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class DefaultLoginCredentialsFromBlueLockFAQ implements
         PopulateDefaultLoginCredentialsForImageStrategy {

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      checkNotNull(resourceToAuthenticate);
      checkArgument(resourceToAuthenticate instanceof VAppTemplate,
               "Resource must be an VAppTemplate (for Terremark)");
      VAppTemplate template = (VAppTemplate) resourceToAuthenticate;
      if (template.getDescription().indexOf("Windows") >= 0) {
         return new Credentials("expressuser", "ExpressPassword#1");
      } else {
         if (template.getDescription().indexOf("buntu") != -1) {
            return new Credentials("express", "ExpressPassword#1");
         } else {
            return new Credentials("root", "ExpressPassword#1");
         }
      }
   }
}
