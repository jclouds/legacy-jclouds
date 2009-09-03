package org.jclouds.azure.storage.xml.config;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.azure.storage.xml.ErrorHandler;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret AzureStorage responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class AzureStorageParserModule extends AbstractModule {
   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<AzureStorageError>> errorTypeLiteral = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<AzureStorageError>>() {
   };

   @Override
   protected void configure() {
      bindErrorHandler();
      bindCallablesThatReturnParseResults();
      bindParserImplementationsToReturnTypes();
   }

   protected void bindParserImplementationsToReturnTypes() {
   }

   protected void bindCallablesThatReturnParseResults() {
      bind(errorTypeLiteral).toProvider(
               FactoryProvider.newFactory(errorTypeLiteral,
                        new TypeLiteral<ParseSax<AzureStorageError>>() {
                        }));
   }

   protected void bindErrorHandler() {
      bind(new TypeLiteral<ParseSax.HandlerWithResult<AzureStorageError>>() {
      }).to(ErrorHandler.class);
   }

}