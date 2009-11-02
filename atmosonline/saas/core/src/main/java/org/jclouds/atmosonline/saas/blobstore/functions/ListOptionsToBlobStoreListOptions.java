package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ListOptionsToBlobStoreListOptions implements
         Function<org.jclouds.atmosonline.saas.options.ListOptions[], ListContainerOptions> {
   public ListContainerOptions apply(org.jclouds.atmosonline.saas.options.ListOptions[] optionsList) {
      ListContainerOptions options = new ListContainerOptions();
      if (optionsList.length != 0) {
         if (optionsList[0].getToken() != null) {
            options.afterMarker(optionsList[0].getToken());
         }
         if (optionsList[0].getLimit() != null) {
            options.maxResults(optionsList[0].getLimit());
         }
      }
      return options;
   }
}