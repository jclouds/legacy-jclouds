package org.jclouds.azure.servicemanagement.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Create the .cer and pfx files using http://www.windowsazure4j.org/learn/labs/Management/index.html or http://msdn.microsoft.com/en-us/library/gg551722
 * 
 * Add it to azure : http://msdn.microsoft.com/en-us/library/gg551726
 * 
 * Generate the keystore
 * http://stackoverflow.com/questions/4217107/how-to-convert-pfx-file-to-keystore-with-private-key
 * 
 * openssl pkcs12 -in azure-servicemanagement.pfx -out azure-servicemanagement.pem
 * 
 * 
 * TODO remove this useless shitty class
 */
public class CertificateConverter {
	
	public static void main(String[] args) throws Exception {
		convert("C:\\Neotys\\certificates\\azure-servicemanagement.pfx", "C:\\Neotys\\certificates\\azure-servicemanagement.keystore",
		        "azure-servicemanagement", "azure-servicemanagement", "keyalias");
	}
	
	public static void convert(String pfxFile, String keystoreFile,
			String inputKey, String outputKey, String alias) throws Exception {

		File fileIn = new File(pfxFile);
		File fileOut = new File(keystoreFile);
		if (!fileIn.canRead()) {
			System.out.println("Unable to access input keystore: "
					+ fileIn.getPath());
			throw new Exception("Unable to access input keystore: "
					+ fileIn.getPath());
		}
		if (fileOut.exists() && !fileOut.canWrite()) {
			System.out.println("Output file is not writable: "
					+ fileOut.getPath());
			throw new Exception("Output file is not writable: "
					+ fileOut.getPath());
		}
		KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
		KeyStore ksjks = KeyStore.getInstance("jks");
		char[] inphrase = inputKey.toCharArray();
		char[] outphrase = outputKey.toCharArray();
		kspkcs12.load(new FileInputStream(fileIn), inphrase);
		ksjks.load((fileOut.exists()) ? new FileInputStream(fileOut) : null,
				outphrase);
		Enumeration<String> eAliases = kspkcs12.aliases();
		int n = 0;
		List<String> list = new ArrayList<String>();
		if (!eAliases.hasMoreElements()) {
			throw new Exception(
					"Certificate is not valid. It does not contain any alias.");
		}
		while (eAliases.hasMoreElements()) {
			String strAlias = (String) eAliases.nextElement();
			System.out.println("Alias " + n++ + ": " + strAlias);
			if (kspkcs12.isKeyEntry(strAlias)) {
				System.out.println("Adding key for alias " + strAlias);
				Key key = kspkcs12.getKey(strAlias, inphrase);
				Certificate[] chain = kspkcs12.getCertificateChain(strAlias);

				if (alias != null)
					strAlias = alias;

				ksjks.setKeyEntry(strAlias, key, outphrase, chain);
				list.add(strAlias);
			}
		}
		OutputStream out = new FileOutputStream(fileOut);
		ksjks.store(out, outphrase);
		out.close();
	}
}
