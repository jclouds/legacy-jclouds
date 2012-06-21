---
layout: jclouds
title: Filesystem provider
---
# Filesystem provider

## Package description

There is only one package that contains all the code for filesystem provider implementation, 
and could be found inside /filesystem folder in jclouds project

## Architecture and classes responsibilities

  * *FilesystemAsyncBlobStore* is the main actor of the provider. 
	It is responsible for all container and blob tasks. Currently, when the high-level and
	 generic BlobStore methods are called (like putBlob or deleteBlob), 
	the code inside this class is executed. 
	It relies on *FilesystemStorageStrategy* and *FileSystemBlobUtilsImpl* for IO specific tasks. 
	Generally, first exploration of the code should be performed inside this class.
	
  * *FilesystemStorageStrategy* and derived class *FilesystemStorageStrategyImpl* performs 
	IO task on filesystem, like creation and deletion of containers and blobs.
	
  * *FileSystemBlobUtilsImpl* implements the required BlobUtils interface and redirects to
 	the *FilesystemStorageStrategy* all its methods.

  * *FilesystemBlobKeyValidator* and derived class *FilesystemBlobKeyValidatorImpl* validates the key of the blob.

  * *FilesystemContainerNameValidator* and derived class *FilesystemContainerNameValidatorImpl* 
	validates the container name.
	
  * *FilesystemBlobStoreContextBuilder*, *FilesystemBlobStorePropertiesBuilder* and
 	*FilesystemBlobStoreModule* are used by jclouds specific IoC implementation.

  * *FilesystemBlobStoreContextModule* is the [Module](http://code.google.com/p/google-guice/) that
 	link interfaces and their implementation. 

## Common developer customization
Changes validation rules for container name and blob key

Currently validation is performed inside classes *FilesystemBlobKeyValidatorImpl* and
 *FilesystemContainerNameValidatorImpl*, of package _org.jclouds.filesystem.predicates.validators.internal_. 

If you want to change these rules, create a new class that implements interface *FilesystemBlobKeyValidator* or
 *FilesystemContainerNameValidator* and assign it your personalized modules list, used when creating the context.


## Know issues and limits 

  * blobstore list method lists all the file inside the container in a recursive way. 
	Other options of the method aren't manager yes.
	
  * There are issues when compiling the class under Windows. Need to be fixed

  * No blob metadata are stored in this current implementation. Only the name and
 	the content of the source payload is stored. 
`
 
