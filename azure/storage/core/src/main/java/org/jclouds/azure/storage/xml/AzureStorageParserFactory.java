package org.jclouds.azure.storage.xml;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.http.functions.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates Parsers needed to interpret Azure Storage Service messages. This class uses guice
 * assisted inject, which mandates the creation of many single-method interfaces. These interfaces
 * are not intended for public api.
 * 
 * @author Adrian Cole
 */
public class AzureStorageParserFactory {

   @VisibleForTesting
   public static interface GenericParseFactory<T> {
      ParseSax<T> create(ParseSax.HandlerWithResult<T> handler);
   }

   @Inject
   private GenericParseFactory<AzureStorageError> parseErrorFactory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   /**
    * @return a parser used to handle error conditions.
    */
   public ParseSax<AzureStorageError> createErrorParser() {
      return parseErrorFactory.create(errorHandlerProvider.get());
   }

}