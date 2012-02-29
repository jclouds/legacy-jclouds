package org.jclouds.greenqloud.storage;

import java.net.URI;
import java.util.Set;

import org.jclouds.aws.s3.AWSS3ProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.collect.ImmutableSet;

public class GreenQloudStorageProviderMetadata extends AWSS3ProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "greenqloud-storage";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.BLOBSTORE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "GreenQloud Simple Storage Service (S3)";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "Access Key ID";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Secret Access Key";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://www.greenqloud.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://manage.greenqloud.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("http://docs.amazonwebservices.com/AmazonS3/latest/API");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("greenqloud-storage", "greenqloud-compute");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("IS-1");
   }

}