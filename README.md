# Fiware Usage Control


[![FIWARE Security](https://nexus.lab.fiware.org/repository/raw/public/badges/chapters/security.svg)](https://www.fiware.org/developers/catalogue/)
![License](https://img.shields.io/github/license/ging/fiware-usage-control.svg)
[![](https://img.shields.io/badge/tag-fiware-orange.svg?logo=stackoverflow)](http://stackoverflow.com/questions/tagged/fiware)
<br/>
[![Known Vulnerabilities](https://snyk.io/test/github/ging/fiware-usage-control/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ging/fiware-usage-control?targetFile=pom.xml)

Usage control is a promising approach for access control in open, distributed, heterogeneous and network-connected computer environments. It encompasses and enhances traditional access control models, Trust Management (TM) and Digital Rights Management (DRM), and its main novelties are mutability of attributes and continuity of access decision evaluation.

Usage control encompass Data Access control and Data Usage Control, a good representation of this concepts are showed in the next figure:

![usage-control-concept](docs/images/usage-concept.png)

**Data Access Control:**
 * Specify who can access what resource
 * Also the rights to access it (actions)

**Data Usage Control:**
 * Ensures data sovereignty
 * Regulates what is allowed to happen with the data  (future use).
 * Related with data ingestion and processing
 * Context of intellectual property protection, privacy protection, compliance with regulations and digital rights management

In order to include the capabilities of usage control, in this repo are included a set of components and operations for providing usage control capabilities over data coming from the Orion Context Broker and processed by a data streaming processing engine (Apache Flink). First, the architecture and scenario are presented, and then the instructions and resources of how you can replicate the case of use presented.
## Architecture

The next figure presents an abstract representation of the architecture of usage control proposed.
A general overview of the architecture is presented in the next figure . This scheme is based on a hybrid model based on Data Privacy Directive 95/46/EC and the IDS reference architecture 
and it is divided in three essential parts:  data provider, data consumer and data controller.

![usage-architecture-1](docs/images/usage-architecture-1.png)
 
For some cases the Data Provider and Data Controller can be integrated in only one stakeholder inside of the architecture, this is represented in the next figure.

![usage-architecture-2](docs/images/usage-architecture-2.png)
 
## Scenario
The scenario presented in this repository  is composed by:

**Data Consumer:**

 * Apache Flink  (1 Job manager and 1 task manager)
 * A streaming Job for making the Aggregations and operations of some values of a notified Entity created in the Orion context Broker

**Data Provider:**

 * One IDM keyrock instance for Access control and define the Usage control Policies
 * One Orion (with mongo) instance where the entities are created.
 * One PEP proxy instance for access control
 * One instance of a Control Panel Web application for monitoring the usage control rules and punishments
 * On instance of PTP (Policy Translation Point) for translating the FI-ODRL Policies into streaming program 
 * One Apache Flink instance with complex event processing capabilities for analyzing the logs ensure the compliant of the obligations defined in the IDM 
 * One instance of a Supermarket tickets Dataset posting data to the Orion Context Broker
![usage-scenario](docs/images/usage-scenario.png) 


## Deployment

For deploying and running this scenario you need to be pre-installed and running docker and docker compose
1. Clone the repository
```
git clone https://github.com/ging/fiware-usage-control.git
```
2. Go to the root directory
```
cd fiware-usage-control
```
For deploying the Data Usage control components of the Data Provider-Controller side run containers 
defined in the docker-compose.yml file with their respective ENV variables

3. Run containers
```
sudo docker-compose up -d
```
4. Check if all the containers are running
```
sudo docker ps
```
6. Check the orion entities
```
curl localhost:1026/v2/entities -s -S --header 'Accept: application/json' | python -mjson.tool
```
Now, for deploying the component of the Data Consumer side follow the next steps:

1. Go to the flink folder
```
cd flink
```
2. Run Flink containers
```
sudo docker-compose up -d
```
3. Check if all the containers are running
```
sudo docker ps
```
Once you have all up and running you can continue to follow the demo video for the next steps.

[Demo Video](https://drive.google.com/file/d/1o_4KPLG026xG67lXitQeAj98rbZjCGx7/view?usp=sharing) 
