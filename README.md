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


### Example Spring Boot Service

This repository contains a Spring Boot application which exposes a simple web API and returns a welcome message with
the current services' version:

```shell
curl http://localhost:8080/example-service-telepresence/

Hello from example-service-telepresence v1!
```



    


