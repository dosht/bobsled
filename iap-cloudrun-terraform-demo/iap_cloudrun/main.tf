//TODO: Add resources CPU and Memory
//TODO: Split demos in another repo

variable "demo_name" {
  description = "Name for demo and associated resources"
}

variable "iap_client_id" {
  type      = string
  sensitive = false
}

variable "iap_client_secret" {
  type      = string
  sensitive = true
}

variable "serverless-vpc-connector" {
  type      = string
  sensitive = true   
}

variable "domain" {
  type      = string
  sensitive = true
}

variable "region" {
  type      = string
  sensitive = true
}

variable "image" {
  type      = string
  sensitive = true
  default = "gcr.io/cloudrun/hello"
}

variable "container_port" {
  type      = string
  sensitive = true
  default = "8888"
}

variable "cpu" {
  type      = string
  sensitive = true
  default = "1000m"
}

variable "memory" {
  type      = string
  sensitive = true
  default = "1024Mi"
}

variable "service_account_name" {
  type      = string
  sensitive = true
  default   = "42343645686-compute@developer.gserviceaccount.com"
}

variable "maxScale" {
  type      = string
  sensitive = true
  default   = "5"
}

data "google_project" "project" {
}

resource "google_cloud_run_service" "default" {
  name     = "${var.demo_name}-service"
  location = var.region
  project  = data.google_project.project.project_id

  metadata {
    annotations = {
      "run.googleapis.com/ingress" : "internal-and-cloud-load-balancing"
      "run.googleapis.com/launch-stage" = "BETA"
    }
  }
  template {
    metadata {
      annotations = {
        "run.googleapis.com/vpc-access-connector"   = var.serverless-vpc-connector
        "autoscaling.knative.dev/maxScale"          = var.maxScale
         "run.googleapis.com/execution-environment" = "gen2"
         "run.googleapis.com/vpc-access-egress"     = "all-traffic"
      }
      labels = {
        "app" = var.demo_name
      }
    }
    spec {
      service_account_name  = var.service_account_name
      container_concurrency = 80
      timeout_seconds       = 300
      containers {
        image   = var.image
        args    = []
        command = []
        ports {
          container_port = var.container_port
          name           = "http1"
        }
        resources {
          limits = {
            "cpu"    = var.cpu
            "memory" = var.memory
          }
          requests = {}
        }
      }
    }
  }
}

resource "google_compute_region_network_endpoint_group" "serverless_neg" {
  provider              = google
  name                  = "${var.demo_name}-serverless-neg"
  network_endpoint_type = "SERVERLESS"
  region                = var.region
  cloud_run {
    service = google_cloud_run_service.default.name
  }
}

module "lb-http" {
  source  = "GoogleCloudPlatform/lb-http/google//modules/serverless_negs"
  version = "5.1.0"

  project = data.google_project.project.project_id
  name    = "${var.demo_name}-load-balancer"

  ssl                             = true
  managed_ssl_certificate_domains = [var.domain]
  https_redirect                  = true

  backends = {
    default = {
      description = null
      groups = [
        {
          group = google_compute_region_network_endpoint_group.serverless_neg.id
        }
      ]
      enable_cdn             = false
      security_policy        = null
      custom_request_headers = null

      iap_config = {
        enable               = true
        oauth2_client_id     = var.iap_client_id
        oauth2_client_secret = var.iap_client_secret
      }
      log_config = {
        enable      = false
        sample_rate = null
      }
    }
  }
}

data "google_iam_policy" "iap" {
  binding {
    role = "roles/iap.httpsResourceAccessor"
    members = [
      "group:data-science@projectX.com", // a google group
      // "allAuthenticatedUsers"          // anyone with a Google account (not recommended)
      // "user:ahmetalpbalkan@gmail.com", // a particular user
    ]
  }
}

resource "google_iap_web_backend_service_iam_policy" "policy" {
  project             = data.google_project.project.project_id
  web_backend_service = "${var.demo_name}-load-balancer-backend-default"
  policy_data         = data.google_iam_policy.iap.policy_data
  depends_on = [
    module.lb-http
  ]
}

output "load-balancer-ip" {
  value = module.lb-http.external_ip
}

output "oauth2-redirect-uri" {
  value = "https://iap.googleapis.com/v1/oauth/clientIds/${var.iap_client_id}:handleRedirect"
}

resource "google_dns_record_set" "root" {
  name         = "${var.domain}."
  type         = "A"
  ttl          = 3600
  managed_zone = "projectX-labs-com"

  rrdatas = [module.lb-http.external_ip]
}
