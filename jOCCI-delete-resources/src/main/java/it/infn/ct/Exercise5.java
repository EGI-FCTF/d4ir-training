
/*
 *  Copyright 2016 EGI Foundation
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package it.infn.ct;

import java.util.List;
import java.util.Arrays;
import java.util.Properties;

import java.net.URI;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URISyntaxException;

import cz.cesnet.cloud.occi.Model;
import cz.cesnet.cloud.occi.api.Client;
import cz.cesnet.cloud.occi.api.EntityBuilder;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.exception.EntityBuildingException;
import cz.cesnet.cloud.occi.api.http.HTTPClient;
import cz.cesnet.cloud.occi.api.http.auth.HTTPAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.VOMSAuthentication;
import cz.cesnet.cloud.occi.core.Action;
import cz.cesnet.cloud.occi.core.ActionInstance;
import cz.cesnet.cloud.occi.core.Attribute;
import cz.cesnet.cloud.occi.core.Resource;
import cz.cesnet.cloud.occi.exception.AmbiguousIdentifierException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.exception.RenderingException;
import cz.cesnet.cloud.occi.infrastructure.Compute;
import cz.cesnet.cloud.occi.parser.MediaType;
import cz.cesnet.cloud.occi.infrastructure.enumeration.ComputeState;

public class Exercise5
{
    // Delete cloud resources from selected provider.
    // Available resources that can be deleted via API are the following:
    // - compute = computing resource, 
    // - storage = storage resources,
    // - storagelink = storage link

    // Deleting resources
    public static void doDelete(Properties properties, EntityBuilder eb, Client client)
    {
	try {
		System.out.println();

		if ((properties.getProperty("OCCI_RESOURCE_ID").contains("storage")) ||
                   (properties.getProperty("OCCI_RESOURCE_ID").contains("storagelink")))
                {
                        System.out.println("[+] Clean up!");
                        boolean status = client.delete(URI.create(properties.getProperty("OCCI_RESOURCE_ID")));
                        client.delete(URI.create(properties.getProperty("OCCI_RESOURCE_ID")));
                        if (status) System.out.println("[-] Deleted: OK");
                        else System.out.println("[-] Deleted: FAIL");
                } // end 'storage'

		else {
			System.out.println("[+] Trigger a 'stop' action to the 'compute' resource");
			ActionInstance actionInstance =
	        	eb.getActionInstance(URI.create("http://schemas.ogf.org/occi/infrastructure/compute/action#stop"));

		 	try { Thread.sleep(5000); } catch (InterruptedException ex) { }
	
			boolean status = client.trigger(URI
				.create(properties.getProperty("OCCI_RESOURCE_ID")), actionInstance);

			if (status) System.out.println("[-] Triggered: OK");
	        	else System.out.println("[-] Triggered: FAIL");

			System.out.println("[+] Delete the virtual appliance");
        		status = client.delete(URI.create(properties.getProperty("OCCI_RESOURCE_ID")));
		 	
			try { Thread.sleep(5000); } catch (InterruptedException ex) { }

		        if (status) System.out.println("[-] Deleted: OK");
        		else System.out.println("[-] Deleted: FAIL");
		} // end 'compute'

	} catch (EntityBuildingException | CommunicationException ex) {
		throw new RuntimeException(ex);
	}
    }


    public static void main (String[] args)
    {
	String AUTH = "x509"; 
        String TRUSTED_CERT_REPOSITORY_PATH = "/etc/grid-security/certificates";
        String PROXY_PATH = "/tmp/x509up_u1041"; // <= Change here!
        
	String OCCI_ENDPOINT_HOST = "https://carach5.ics.muni.cz:11443"; // <= Change here!
	//String OCCI_ENDPOINT_HOST = "http://stack-server.ct.infn.it:8787/occi1.1"; // <= Change here!
	//String OCCI_ENDPOINT_HOST = "https://rocci.iihe.ac.be:11443"; // <= Change here!
	
	Boolean verbose = true;

	// [ Deleting available resources ]
	String ACTION = "delete";

	// CESNET-MetaCloud
        // - Deleting running VM
	List<String> RESOURCE = Arrays.asList("compute", 
	"https://carach5.ics.muni.cz:11443/compute/102559"); // <= Change here!

        // - Deleting block storage
	//List<String> RESOURCE = Arrays.asList("storage", 
	//"https://carach5.ics.muni.cz:11443/storage/4263"); // <= Change here!

	// - Deleting storage link
	//List<String> RESOURCE = Arrays.asList("storage", 
	//"/link/storagelink/compute_102559_disk_2"); // <= Change here!

	
	// INFN-CATANIA-STACK
        // - Deleting running VM
        //List<String> RESOURCE = Arrays.asList("compute",
        //"http://stack-server.ct.infn.it:8787/occi1.1/compute/d9932cb6-b301-4a71-a52f-eed9f03694f2");


	// BEgrid-BELNET
	// - Deleting running VM
	//List<String> RESOURCE = Arrays.asList("compute", 
	//"https://rocci.iihe.ac.be:11443/compute/13598"); // <= Change here!

        // - Deleting block storage
	/*List<String> RESOURCE = Arrays.asList("storage", 
	"https://occi.nebula.finki.ukim.mk:443/storage/629"); // <= Change here!*/

        // - Deleting storage link
	//List<String> RESOURCE = Arrays.asList("storage", 
	//"/link/storagelink/compute_15238_disk_2"); // <= Change here!



	// BIFI
        //List<String> RESOURCE = Arrays.asList("compute",
        //"http://server4-eupt.unizar.es:8787/compute/67779542-2fb0-46d6-901f-ffea8028254d"); // <= Change here!

	//List<String> RESOURCE = Arrays.asList("storage", // <= Detach the storage link!
        //"/storage/link/67779542-2fb0-46d6-901f-ffea8028254d_e5102e95-5cde-4db5-bf85-2677c5359203");
	
	//List<String> RESOURCE = Arrays.asList("storage", // <= Delete block storage
        //"http://server4-eupt.unizar.es:8787/storage/e5102e95-5cde-4db5-bf85-2677c5359203");


	// IFCA-LCG2
	/*List<String> RESOURCE = Arrays.asList("compute",
        "https://cloud.ifca.es:8787/occi1.1/compute/1d29edf9-44ba-40b0-bd05-f39a843959b6");*/


	// CETA-GRID
	/*List<String> RESOURCE = Arrays.asList("storage", // <= Delete the storage link
        "/storage/link/142f103a-0ad3-4d39-b165-8f7c1fdc28bd_11f02030-32b1-411c-b885-27013ea9be41");*/

	/*List<String> RESOURCE = Arrays.asList("storage", // <= Delete the block storage
	"https://controller.ceta-ciemat.es:8787/storage/11f02030-32b1-411c-b885-27013ea9be41");*/

	//List<String> RESOURCE = Arrays.asList("compute",
	//"https://controller.ceta-ciemat.es:8787/compute/142f103a-0ad3-4d39-b165-8f7c1fdc28bd");

	if (verbose) {
		System.out.println();
		if (ACTION != null && !ACTION.isEmpty()) 
			System.out.println("[ACTION] = " + ACTION);
		else	
			System.out.println("[ACTION] = Get dump model");
		System.out.println("AUTH = " + AUTH);
		//System.out.println("VOMS = " + VOMS);
		if (OCCI_ENDPOINT_HOST != null && !OCCI_ENDPOINT_HOST.isEmpty()) 
			System.out.println("OCCI_ENDPOINT_HOST = " + OCCI_ENDPOINT_HOST);
		if (RESOURCE != null && !RESOURCE.isEmpty()) 
			System.out.println("RESOURCE = " + RESOURCE);
		if (TRUSTED_CERT_REPOSITORY_PATH != null && !TRUSTED_CERT_REPOSITORY_PATH.isEmpty()) 
			System.out.println("TRUSTED_CERT_REPOSITORY_PATH = " + TRUSTED_CERT_REPOSITORY_PATH);
		if (PROXY_PATH != null && !PROXY_PATH.isEmpty()) 
			System.out.println("PROXY_PATH = " + PROXY_PATH);
		if (verbose) System.out.println("Verbose = True ");
		else System.out.println("Verbose = False ");
	}

        Properties properties = new Properties();
	if (ACTION != null && !ACTION.isEmpty())
	        properties.setProperty("ACTION", ACTION);
	if (OCCI_ENDPOINT_HOST != null && !OCCI_ENDPOINT_HOST.isEmpty())
	        properties.setProperty("OCCI_ENDPOINT_HOST", OCCI_ENDPOINT_HOST);

	if (RESOURCE != null && !RESOURCE.isEmpty()) 
        for (int i=0; i<RESOURCE.size(); i++) {
	        if ( (!RESOURCE.get(i).equals("compute")) && 
	             (!RESOURCE.get(i).equals("storage")) &&
	             (!RESOURCE.get(i).equals("network")) &&
	             (!RESOURCE.get(i).equals("os_tpl")) &&
	             (!RESOURCE.get(i).equals("resource_tpl")) )
        		properties.setProperty("OCCI_RESOURCE_ID", RESOURCE.get(i));
        		
		else { 
			properties.setProperty("RESOURCE", RESOURCE.get(i));
        		properties.setProperty("OCCI_RESOURCE_ID", "empty");
		}
        }
			
        properties.setProperty("TRUSTED_CERT_REPOSITORY_PATH", TRUSTED_CERT_REPOSITORY_PATH);
        properties.setProperty("PROXY_PATH", PROXY_PATH);
        properties.setProperty("OCCI_AUTH", AUTH);

	try {
		HTTPAuthentication authentication = new VOMSAuthentication(PROXY_PATH);
		
		authentication.setCAPath(TRUSTED_CERT_REPOSITORY_PATH);
	        
		Client client = new HTTPClient(URI.create(OCCI_ENDPOINT_HOST),
                                authentication, MediaType.TEXT_PLAIN, false);

            	//Connect client
            	client.connect();
		
		Model model = client.getModel();
                EntityBuilder eb = new EntityBuilder(model);

		if  (ACTION.equals("delete")) 
			doDelete(properties, eb, client);

	} catch (CommunicationException ex ) {
                 throw new RuntimeException(ex);
        }
    }
}
