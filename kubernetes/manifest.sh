#UCON kubernetes deployment

kubectl create -f ./namespace.yaml

#create the disk in the GCE with the name usage-control-volume and type ssd capacity 100 GB, then:

kubectl  create -f ucon-storage.yaml

kubectl  create -f vol-mysql-persistentvolumeclaim.yaml

kubectl  create -f vol-mongo-persistentvolumeclaim.yaml

kubectl  create -f deployments/mysql-deployment.yaml 

kubectl  create -f services/mysql-service.yaml

kubectl  create -f services/mongo-service.yaml

kubectl  create -f deployments/mongo-deployment.yaml 

kubectl  create -f services/fiware-idm-service.yaml

kubectl  create -f deployments/fiware-idm-deployment.yaml

kubectl  create -f services/orion-service.yaml

kubectl  create -f deployments/orion-deployment.yaml

kubectl  create -f config-orion.yaml

#Flink deployment for CEP
kubectl  create -f config/flink-configuration-configmap.yaml
kubectl  create -f services/jobmanager-service.yaml
kubectl  create -f services/jobmanager-rest-service.yaml
kubectl  create -f deployments/jobmanager-deployment.yaml
kubectl  create -f services/taskmanager-service.yaml
kubectl  create -f deployments/taskmanager-deployment.yaml

#Dataset for running example
kubectl  create -f services/control-panel-service.yaml

kubectl  create -f deployments/control-panel-deployment.yaml

kubectl  create -f services/mongo-ds-service.yaml

kubectl  create -f deployments/mongo-ds-deployment.yaml

kubectl  create -f services/dataset-service.yaml

kubectl  create -f deployments/dataset-deployment.yaml

#PTP for CEP

kubectl  create -f services/ptp-service.yaml

kubectl  create -f deployments/ptp-deployment.yaml

kubectl  create -f services/pep-ptp-service.yaml

kubectl  create -f deployments/pep-ptp-deployment.yaml

