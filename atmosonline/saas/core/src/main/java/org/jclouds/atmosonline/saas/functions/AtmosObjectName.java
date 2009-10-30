package org.jclouds.atmosonline.saas.functions;

import org.jclouds.atmosonline.saas.domain.AtmosObject;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class AtmosObjectName implements Function<Object, String> {

   public String apply(Object in) {
      AtmosObject from = (AtmosObject) in;
      return from.getContentMetadata().getName() != null ? from.getContentMetadata().getName()
               : from.getSystemMetadata().getObjectName();
   }

}