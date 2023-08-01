# Terminal 22 - Accelerate development with [Telepresence<img src="telepresence.svg" width=100px />](https://www.telepresence.io/)

Migrating to kubernetes and splitting into microservices has given a lot of advantages but
developing and testing these services on a remote Kubernetes cluster can be challenging for these reasons:

- Slow feedback loops: once a code change is made and want to be tested in a Kubernetes cluster, it requires to be deployed running a 
deployment pipeline. This requires few minutes to be completed and so it is time-consuming.
- Memory and CPU can be insufficient if we want to run all the services locally.

Telepresence might be really useful to overcome these limitations.

## What is Telepresence
Telepresence is an open source tool which allow to make changes to your service locally without having to run all the dependencies on your local machine.

Using telepresence allows you to use custom tools, such as a debugger and IDE, for a local service and provides the service full access to ConfigMap, secrets, and the services running on the remote cluster.

The aim of this is to investigate more about this tool and find out if it might be useful introduce it in our tech stack.

### How does it work?
Telepresence consists of two architecture components: 
- the client-side (CLI) telepresence binary
- the cluster side (Kubernetes or Openshift) traffic-manager and traffic-agent.

Two main capabilities:
- access to the remote K8s services as if they were running locally using the command `` telepresence connect``
- route remote traffic to your local dev machine with the command `` telepresence intercept <service-name> ``
  - a proxy container is injected into the pods associated to the target services

### More info
- [Official doc](https://www.telepresence.io/)
- [GitHub repository](https://github.com/telepresenceio/telepresence)
- [Telepresence for Docker](https://www.docker.com/products/telepresence-for-docker/)

## Let's demo it!

### Prerequisites
- Java 17
- **kubectl** or **oc** CLI installed and configured.
- A Kubernetes cluster.
  - Docker Desktop or Minikube if you want to install a single node cluster locally.

### Checkout and test the simple Spring Boot Service
This repository contains a Spring Boot application which exposes a simple web API and returns a welcome message with
the current services' version:

```shell
curl http://localhost:8080/example-service-telepresence/

Hello from example-service-telepresence v1!
```

### Create the docker image

Create and push in the local container registry following these steps:
- build a Docker image with Jib and push to the local container register using the following command: `` ./gradlew jibDockerBuild ``
- run this command to check the images are present: `` docker images | grep example-service-telepresence ``
- if you are using Minikube, load the image into it with the command: `` minikube image load example-service-telepresence:<version> ``

### Deploy the example-service-telepresence to your K8s cluster

#### Run the following command:
```shell
~/example-service-telepresence % kubectl apply -f k8s-manifests/deployment.yaml
namespace/t22 created
service/example-service-telepresence created
deployment.apps/example-service-telepresence created
```
#### Check if everything is running with command:
```shell
~/example-service-telepresence % kubectl get all -n t22
NAME                                                READY   STATUS    RESTARTS   AGE
pod/example-service-telepresence-649bdc588f-9sc8l   2/2     Running   0          26m

NAME                                   TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
service/example-service-telepresence   LoadBalancer   10.97.66.208   localhost     8080:32109/TCP   76m

NAME                                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/example-service-telepresence   1/1     1            1           76m

NAME                                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/example-service-telepresence-649bdc588f   1         1         1       26m
replicaset.apps/example-service-telepresence-878c748b7    0         0         0       76m
```

### Install Telepresence
- Install Telepresence on your machine and in your K8s cluster following this guide: https://www.telepresence.io/docs/latest/quick-start/
- Check if the traffic manager has been correctly deployed on the K8s cluster in the ``ambassador`` namespace:
```shell
~/example-service-telepresence % kubectl get ns | grep ambassador
ambassador        Active   7m26s

~/example-service-telepresence % kubectl get all -n ambassador
NAME                                   READY   STATUS    RESTARTS   AGE
pod/traffic-manager-6546b9b9f4-f22b5   1/1     Running   0          8m51s

NAME                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)              AGE
service/agent-injector    ClusterIP   10.101.140.103   <none>        443/TCP              8m56s
service/traffic-manager   ClusterIP   None             <none>        8081/TCP,15766/TCP   8m56s

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/traffic-manager   1/1     1            1           8m56s

NAME                                         DESIRED   CURRENT   READY   AGE
replicaset.apps/traffic-manager-6546b9b9f4   1         1         1       8m56s
```

### Connect to the K8s cluster

- Run the command to connect to your cluster with telepresence:
```shell
~/example-service-telepresence % telepresence connect
Connected to context docker-desktop (https://kubernetes.docker.internal:6443)
```
- Check the status:

```shell
~/example-service-telepresence % telepresence status
OSS User Daemon: Running
  Version           : v2.14.2
  Executable        : /usr/local/bin/telepresence
  Kubernetes context: docker-desktop
  Manager namespace : ambassador
  Intercepts        : 0 total
OSS Root Daemon: Running
  Version    : v2.14.2
  Version    : v2.14.2
  DNS        :
    Local IP        : 172.31.0.1
    Remote IP       : 10.1.0.68
    Exclude suffixes: [.com .io .net .org .ru]
    Include suffixes: []
    Timeout         : 8s
  Also Proxy : (0 subnets)
  Never Proxy: (1 subnets)
    - 127.0.0.1/32

```

Access the remote service as if it were local, using the Kubernetes service name:
```shell
~/example-service-telepresence % curl example-service-telepresence.t22:8080/example-service-telepresence/
Hello from example-service-telepresence v1!
```

### Intercept the example-service-telepresence running new version locally
In this step we will use Telepresence to route the traffic to the local version with **Telepresence intercept**.

- Change the version of the application in the *build.gradle* file : ``version = 'v2'`` and run the application locally.


- Check if the **example-service-telepresence** is listed with the command:
```shell
~/example-service-telepresence % telepresence list -n t22
example-service-telepresence: ready to intercept (traffic-agent not yet installed)
```

- Intercept all the traffic going to our service in the cluster:
```shell
~/example-service-telepresence % telepresence intercept example-service-telepresence --port 9090:8080 -n t22
Using Deployment example-service-telepresence
   Intercept name         : example-service-telepresence-t22
   State                  : ACTIVE
   Workload kind          : Deployment
   Destination            : 127.0.0.1:8080
   Service Port Identifier: http
   Volume Mount Error     : sshfs is not installed on your local machine
   Intercepting           : all TCP connections
```

- Access to the remote service as we did previously with *curl* command:
```shell
~/example-service-telepresence % curl example-service-telepresence.t22:8080/example-service-telepresence/
Hello from example-service-telepresence v2!
```

Now the traffic is routed to the new version ``V2`` locally, and we can also debug our service speeding up our development and troubleshooting.

## Clean-up resources and close daemon processes

Run the following commands to clean-up the K8s environment:
- Release Telepresence intercept: ``telepresence leave example-service-telepresence-t22``
- Quit Telepresence: ``telepresence quit``
- Delete resources: ``kubectl delete -n t22 -f k8s-manifests/deployment.yaml``

## What's next
Install Telepresence to real cluster test environment such Openshift.

## Conclusion
This tool looks like really promising and helpful in our daily job since it might speed up our development giving to us these advantages:

- A fast local dev loop, with no waiting for a container build / push / deploy
- Ability to use their favorite local tools (IDE, debugger, etc.)
- Ability to run large-scale applications that can't run locally

## Useful external resources
- [Telepresence - Official doc](https://www.telepresence.io/)
-  [Telepresence - Quickstart](https://www.telepresence.io/docs/latest/quick-start)
- [Telepresence - GitHub report](https://github.com/telepresenceio/telepresence)
- [Kubernetes.io - Debug services](https://kubernetes.io/docs/tasks/debug/debug-cluster/local-debugging/)