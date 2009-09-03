package org.jclouds.azure.storage.queue.xml;

import org.jclouds.azure.storage.domain.MetadataList;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates Parsers needed to interpret Azure Queue Service messages. This class uses guice assisted
 * inject, which mandates the creation of many single-method interfaces. These interfaces are not
 * intended for public api.
 * 
 * @author Adrian Cole
 */
public class AzureQueueParserFactory extends AzureStorageParserFactory {

   @Inject
   private GenericParseFactory<MetadataList<QueueMetadata>> parseContainerMetadataListFactory;

   @Inject
   Provider<AccountNameEnumerationResultsHandler> containerMetaListHandlerProvider;

   public ParseSax<MetadataList<QueueMetadata>> createContainerMetadataListParser() {
      return parseContainerMetadataListFactory.create(containerMetaListHandlerProvider.get());
   }
}