package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreListOptionsToListOptions implements Function<ListContainerOptions[], org.jclouds.atmosonline.saas.options.ListOptions> {
   public org.jclouds.atmosonline.saas.options.ListOptions apply(ListContainerOptions[] optionsList) {
      org.jclouds.atmosonline.saas.options.ListOptions httpOptions = new org.jclouds.atmosonline.saas.options.ListOptions();
      if (optionsList.length != 0) {
         if (optionsList[0].getMarker() != null) {
            httpOptions.token(optionsList[0].getMarker());
         }
         if (optionsList[0].getMaxResults() != null) {
            httpOptions.limit(optionsList[0].getMaxResults());
         }
      }
      return httpOptions;
   }
}