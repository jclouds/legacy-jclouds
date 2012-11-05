/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_LIST_SIZE_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_LIST_SIZE_GE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.jclouds.crypto.SshKeys;
import org.jclouds.io.Payloads;
import org.jclouds.json.Json;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.File;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * Tests behavior of {@code Key Pairs}
 * 
 * @author andrea turli
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "KeyPairsApiLiveTest")
public class KeyPairsApiLiveTest extends BaseVCloudDirectorApiLiveTest {

	@Inject
	protected Json json;
	public static final String MEDIA = "media";

	protected VdcApi vdcApi;
	protected MediaApi mediaApi;

	private Map<String, String> sshKey;
	private String keyPairContainer = "keypairs";

	@Override
	@BeforeClass(alwaysRun = true)
	public void setupRequiredApis() {
		vdcApi = context.getApi().getVdcApi();
		mediaApi = context.getApi().getMediaApi();
	}

	@Test(description = "Create Key Pair")
	public void testCreateKeyPair() throws URISyntaxException {
		sshKey = SshKeys.generate();
		String keyPairName = "NewKeyPair";
		Vdc currentVDC = lazyGetVdc();
		Media keyPairsContainer = findOrCreateKeyPairContainerInVDCNamed(currentVDC,
				keyPairContainer, keyPairName);
		String keypairValue = mediaApi.getMetadataApi(
				keyPairsContainer.getId()).get(keyPairName);
		assertEquals(keypairValue, generateKeyPair(keyPairName));
	}

	@Test(description = "DeleteKeyPair", dependsOnMethods = { "testCreateKeyPair" })
	public void testDeleteKeyPair() {
		String keyPairName = "NewKeyPair";
		Vdc currentVDC = lazyGetVdc();
		Media keyPairsContainer = findOrCreateKeyPairContainerInVDCNamed(currentVDC,
				keyPairContainer, keyPairName);
		Task removeMedia = mediaApi.remove(keyPairsContainer.getId());
		Checks.checkTask(removeMedia);
		assertTrue(retryTaskSuccess.apply(removeMedia),
				String.format(TASK_COMPLETE_TIMELY, "removeMedia"));

		keyPairsContainer = mediaApi.get(keyPairsContainer.getId());
		assertNull(keyPairsContainer, String.format(OBJ_DEL, MEDIA,
				keyPairsContainer != null ? keyPairsContainer.toString() : ""));
	}

	private Media findOrCreateKeyPairContainerInVDCNamed(Vdc currentVDC,
			String keyPairsContainerName, final String keyPairName) {
		Media keyPairsContainer = null;

		Optional<Media> optionalKeyPairsContainer = Iterables.tryFind(
				findAllEmptyMediaInOrg(), new Predicate<Media>() {

					@Override
					public boolean apply(Media input) {
						return mediaApi.getMetadataApi(input.getId()).get(
								keyPairName) != null;
					}
				});

		if (optionalKeyPairsContainer.isPresent())
			keyPairsContainer = optionalKeyPairsContainer.get();

		if (keyPairsContainer == null) {
			keyPairsContainer = uploadKeyPairInVCD(currentVDC,
					keyPairsContainerName, keyPairName);
		}
		return keyPairsContainer;
	}

	private Media uploadKeyPairInVCD(Vdc currentVDC,
			String keyPairsContainerName, String keyPairName) {
		Media keyPairsContainer = addEmptyMediaInVDC(currentVDC,
				keyPairsContainerName);
		assertNotNull(keyPairsContainer.getFiles(),
				String.format(OBJ_FIELD_REQ, MEDIA, "files"));
		assertEquals(1, keyPairsContainer.getFiles().size(), String.format(
				OBJ_FIELD_LIST_SIZE_EQ, MEDIA, "files", 1, keyPairsContainer
						.getFiles().size()));

		Link uploadLink = getUploadLinkForMedia(keyPairsContainer);
		// generate an empty iso
		byte[] iso = new byte[] {};
		context.getApi()
				.getUploadApi()
				.upload(uploadLink.getHref(), Payloads.newByteArrayPayload(iso));

		Checks.checkMediaFor(VCloudDirectorMediaType.MEDIA, keyPairsContainer);
		setKeyPairOnkeyPairsContainer(keyPairsContainer, keyPairName, generateKeyPair(keyPairName));

		return keyPairsContainer;
	}

	private Link getUploadLinkForMedia(Media emptyMedia) {
		File uploadFile = getFirst(emptyMedia.getFiles(), null);
		assertNotNull(uploadFile,
				String.format(OBJ_FIELD_REQ, MEDIA, "files.first"));
		assertEquals(uploadFile.getSize(), Long.valueOf(0));
		assertEquals(uploadFile.getSize().longValue(), emptyMedia.getSize(),
				String.format(OBJ_FIELD_EQ, MEDIA, "uploadFile.size()",
						emptyMedia.getSize(), uploadFile.getSize()));

		Set<Link> links = uploadFile.getLinks();
		assertNotNull(links,
				String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links"));
		assertTrue(links.size() >= 1, String.format(OBJ_FIELD_LIST_SIZE_GE,
				MEDIA, "uploadfile.links", 1, links.size()));
		assertTrue(Iterables.all(links, Predicates.or(
				LinkPredicates.relEquals(Link.Rel.UPLOAD_DEFAULT),
				LinkPredicates.relEquals(Link.Rel.UPLOAD_ALTERNATE))),
				String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links.first"));

		Link uploadLink = Iterables.find(links,
				LinkPredicates.relEquals(Link.Rel.UPLOAD_DEFAULT));
		return uploadLink;
	}

	private Media addEmptyMediaInVDC(Vdc currentVDC, String keyPairName) {
		Link addMedia = find(
				currentVDC.getLinks(),
				and(relEquals("add"), typeEquals(VCloudDirectorMediaType.MEDIA)));

		Media sourceMedia = Media.builder().type(VCloudDirectorMediaType.MEDIA)
				.name(keyPairName).size(0).imageType(Media.ImageType.ISO)
				.description("iso generated as KeyPair bucket").build();

		Media emptyMedia = mediaApi.add(addMedia.getHref(), sourceMedia);
		Checks.checkMediaFor(MEDIA, emptyMedia);
		return emptyMedia;
	}

	private String generateKeyPair(String keyPairName) {
		Map<String, String> key = Maps.newHashMap();
		key.put("keyName", keyPairName);
		key.put("keyFingerprint", SshKeys.sha1PrivateKey(sshKey.get("private")));
		key.put("publicKey", sshKey.get("public"));
		return json.toJson(key);
	}

	private void setKeyPairOnkeyPairsContainer(Media media, String keyPairName,
			String keyPair) {
		Task setKeyPair = mediaApi.getMetadataApi(media.getId()).put(
				keyPairName, keyPair);
		Checks.checkTask(setKeyPair);
		assertTrue(retryTaskSuccess.apply(setKeyPair),
				String.format(TASK_COMPLETE_TIMELY, "setKeyPair"));
	}
}
