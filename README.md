
# FIWARE Usage Control Kubernetes


[![FIWARE Security](https://nexus.lab.fiware.org/repository/raw/public/badges/chapters/security.svg)](https://www.fiware.org/developers/catalogue/)
![License](https://img.shields.io/github/license/ging/fiware-usage-control.svg)
[![](https://img.shields.io/badge/tag-fiware-orange.svg?logo=stackoverflow)](http://stackoverflow.com/questions/tagged/fiware)
<br/>

This deployment is intended to use as a proof of concept of the UCON architecture, implemented using the FIWARE usage control component over kubernetes. The deployments provided in this repository are stateless. However, we provide some templates as an example of how volumes can be created and mounted for some of those deployments. As a prerequisite, you need to have a Gcloud account with access to GKE (Google Kubernetes Engine).

## Steps

1. Create a project in GC, after that, open the GC shell and configure your project:

	```
	gcloud config set project [your-project-name]
	```

2. Create a Kubernetes cluster in your GC project:

	```
	gcloud services enable container.googleapis.com
	gcloud container clusters create demo --enable-autoupgrade \
	    --enable-autoscaling --min-nodes=3 --max-nodes=10 --num-nodes=5 \
	    --zone=us-central1-a
	```
3. Clone the repository for deploying the Data Provider Infrastructure over kubernetes:

	```
	git clone -b ucon-kubernetes https://github.com/ging/fiware-usage-control.git
	cd fiware-usage-control
	```
	At this point, we provide a manifest script for deploying all the usage control components except the PEP-PTP pod, because this has to be created after you log in to the IDM and get the PEP-Proxy and the APP- credentials.

4. Run the manifest script:
	```
	./manifest.sh
	```
5. Once all the deployments and services were created you can see the external IP in the GC platform for the following services:
	```
	IDM: [External-IP]:3000
	Control Panel : [External-IP]:3001
	Orion Context Broker: [External-IP]:1026
	CEP-Jobmanager:  [External-IP]:8081
	PEP-PTP: [External-IP]:24225
	```
6. At this point, you need to register the app in the IDM and get the credentials.

7. Once you have the credentials for your deployment, add those values to the pep-ptp deployment file (deployments/pep-ptp-deployment.yaml) . You need to change the following values:
	```
	- name: PEP_PASSWORD
	  value: pep_proxy_xxxxxxxxxx
	- name: PEP_PROXY_APP_ID
	  value: a2b33c0c-xxxxxxxxxxxx
	- name: PEP_PROXY_USERNAME
	  value: pep_proxy_xxxxxxxxxxxxxx
	  ```

8. Create the pep-ptp deployment:
	```
	kubectl create -f deployments/pep-ptp-deployment.yml
	```
9. Now, you can deploy the data consumer infrastructure in a VM on external Server. In your DC&#39;s instance clone the repository (You must have installed docker and docker-compose):
	```
	git clone -b ucon-kubernetes https://github.com/ging/fiware-usage-control.git
	```
10. Access the flink directory in order to run the docker-compose file:
	```
	cd fiware-usage-control/flink
	```
1. Change the environment variables for pointing to the DP:

	Flink-master
	```
	- IDM_URL=http://IDM-external-IP:3000

	- CLIENT_ID=your-client-id

	- CLIENT_SECRET=your-client-secret

	- CALLBACK_URL=your-callbackurl

	- FLINK_PORT=8081

	- OAUTH_CLIENT_PORT=80

	- FLUENT_HOST=pep-ptp-external-IP

	- FLUENT_PORT=24225
	```
	Flink-worker
	```
	- FLUENT_HOST=pep-ptp-external-IP

	- FLUENT_PORT=24225
	```
12. Run Containers:
	```
	sudo docker-compose up -d
	```
13. Modify and run the subscription to Orion External IP
	```
	cd ..

	./subscriptionSupermarket.sh
	```
14. Follow the demo video to test the scenario.
