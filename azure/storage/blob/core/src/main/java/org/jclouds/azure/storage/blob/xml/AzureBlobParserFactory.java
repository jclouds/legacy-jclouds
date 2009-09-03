package org.jclouds.azure.storage.blob.xml;

import org.jclouds.azure.storage.blob.domain.ContainerMetadataList;
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
   private GenericParseFactory<ContainerMetadataList> parseContainerMetadataListFactory;

   @Inject
   Provider<AccountNameEnumerationResultsHandler> containerMetaListHandlerProvider;

   public ParseSax<ContainerMetadataList> createContainerMetadataListParser() {
      return parseContainerMetadataListFactory.create(containerMetaListHandlerProvider.get());
   }
}