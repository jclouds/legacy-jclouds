package org.jclouds.azure.storage.blob.xml;

import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.domain.MetadataList;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates Parsers needed to interpret Azure Blob Service messages. This class uses guice assisted
 * inject, which mandates the creation of many single-method interfaces. These interfaces are not
 * intended for public api.
 * 
 * @author Adrian Cole
 */
public class AzureBlobParserFactory extends AzureStorageParserFactory {

   @Inject
   private GenericParseFactory<MetadataList<ContainerMetadata>> parseContainerMetadataListFactory;

   @Inject
   Provider<AccountNameEnumerationResultsHandler> containerMetaListHandlerProvider;

   public ParseSax<MetadataList<ContainerMetadata>> createContainerMetadataListParser() {
      return parseContainerMetadataListFactory.create(containerMetaListHandlerProvider.get());
   }
}