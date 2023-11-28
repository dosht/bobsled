#!/usr/bin/env python3

import click
import json

from ruamel.yaml import YAML
from pathlib import Path
from plumbum import local
from plumbum import RETCODE


@click.command()
@click.argument("demo_name")
@click.option("--nreplicas", "nreplicas", default=2)
def add_demo(demo_name, nreplicas):
    yaml = YAML()

    docker_img = f"eu.gcr.io/projectX-data-science/{demo_name}:latest"

    retcode, stdout, stderr = local["docker"]["inspect", docker_img].run()
    local["gcloud"]["container", "images", "list-tags", "--project=projectX-data-science", "--format=json", docker_img]

    assert retcode == 0, f"Error finding ({docker_img})!\n{stderr}"

    assert len(json.loads(stdout)) > 0, f"Image ({docker_img}) not found!"

    demo_dir = Path(".")

    with (demo_dir / "kustomization.yaml").open("r") as kustomizationf:
        kustomization = yaml.load(kustomizationf)
        if demo_name not in kustomization["resources"]:
            kustomization["resources"].append(demo_name)

    with (demo_dir / "kustomization.yaml").open("w") as kustomizationf:
        yaml.dump(kustomization, kustomizationf)

    demo_url = f"{demo_name}.projectX-labs.com"

    with (demo_dir / "tls-certificates.yaml").open("r") as tlsf:
        tls = yaml.load(tlsf)

        if demo_url not in tls["spec"]["domains"]:
            tls["spec"]["domains"].append(demo_url)

    with (demo_dir / "tls-certificates.yaml").open("w") as tlsf:
        yaml.dump(tls, tlsf)

    with (demo_dir / "ingress.yaml").open("r") as ingressf:
        ingress = yaml.load(ingressf)
        existing_hosts = [rule["host"] for rule in ingress["spec"]["rules"]]

        if demo_url not in existing_hosts:
            new_rule = {
                "host": demo_url,
                "http": {
                    "paths": [
                        {
                            "path": "/*",
                            "pathType": "ImplementationSpecific",
                            "backend": {
                                "service": {"name": demo_name, "port": {"number": 8888}}
                            },
                        }
                    ]
                },
            }
            ingress["spec"]["rules"].append(new_rule)

    with (demo_dir / "ingress.yaml").open("w") as ingressf:
        yaml.dump(ingress, ingressf)

    (demo_dir / demo_name).mkdir(exist_ok=True)

    with (demo_dir / demo_name / "deployment.yaml").open("w") as deploymentf:
        demo_deployment = f"""apiVersion: apps/v1
kind: Deployment
metadata:
  name: {demo_name}
spec:
  replicas: {nreplicas}
  revisionHistoryLimit: 2
  selector:
    matchLabels:
      app: {demo_name}
  template:
    metadata:
      labels:
        app: {demo_name}
    spec:
      serviceAccountName: experiments
      containers:
        - name: voila
          image: {docker_img}
          ports:
            - containerPort: 8888
          imagePullPolicy: Always
        """

        deploymentf.write(demo_deployment)

    with (demo_dir / demo_name / "service.yaml").open("w") as servicef:
        demo_service = f"""apiVersion: v1
kind: Service
metadata:
  name: {demo_name}
  annotations:
    cloud.google.com/backend-config: '{{"default": "demos-bc"}}'
    cloud.google.com/neg: '{{"ingress": true}}'
spec:
  type: ClusterIP
  ports:
  - port: 8888
    targetPort: 8888
  selector:
    app: {demo_name}
        """

        servicef.write(demo_service)

    with (demo_dir / demo_name / "kustomization.yaml").open("w") as kustomizationf:
        demo_kustomization = f"""apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- deployment.yaml
- service.yaml
        """

        kustomizationf.write(demo_kustomization)


if __name__ == "__main__":
    add_demo()
