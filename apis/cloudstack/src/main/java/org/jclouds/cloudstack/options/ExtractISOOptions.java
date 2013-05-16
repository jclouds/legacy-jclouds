package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO extractISO method.
 *
 * @see org.jclouds.cloudstack.features.ISOClient#extractISO
 * @see org.jclouds.cloudstack.features.ISOAsyncClient#extractISO
 * @author Richard Downer
 */
public class ExtractISOOptions extends BaseHttpRequestOptions {

   public static final ExtractISOOptions NONE = new ExtractISOOptions();

   /**
    * @param url the url to which the ISO would be extracted
    */
   public ExtractISOOptions url(String url) {
      this.queryParameters.replaceValues("url", ImmutableSet.of(url + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param url the url to which the ISO would be extracted
       */
      public static ExtractISOOptions url(String url) {
         return new ExtractISOOptions().url(url);
      }
   }

}
