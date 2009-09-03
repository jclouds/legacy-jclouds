package org.jclouds.azure.storage.queue.xml.config;

import org.jclouds.azure.storage.domain.MetadataList;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.queue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret Azure Queue Service responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class AzureQueueParserModule extends AzureStorageParserModule {
   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<MetadataList<QueueMetadata>>> accountNameEnumerationResultsHandler = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<MetadataList<QueueMetadata>>>() {
   };

   @Override
   protected void bindParserImplementationsToReturnTypes() {
      super.bindParserImplementationsToReturnTypes();
      bind(new TypeLiteral<ParseSax.HandlerWithResult<MetadataList<QueueMetadata>>>() {
      }).to(AccountNameEnumerationResultsHandler.class);
   }

   @Override
   protected void bindCallablesThatReturnParseResults() {
      super.bindCallablesThatReturnParseResults();
      bind(accountNameEnumerationResultsHandler).toProvider(
               FactoryProvider.newFactory(accountNameEnumerationResultsHandler,
                        new TypeLiteral<ParseSax<MetadataList<QueueMetadata>>>() {
                        }));
   }

}