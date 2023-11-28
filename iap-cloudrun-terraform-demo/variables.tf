variable "domain" {
  type    = string
  default = "projectX-labs.com"
}

variable "project_id" {
    type = string
    default = "projectX-data-science"
}

variable "region" {
    type = string
    default = "europe-west2"
}

variable "vpc-connector" {
    type = string
    default = "serverless-vpc-connector"
}

variable "iap_client_id" {
  type      = string
  sensitive = false
}

variable "iap_client_secret" {
  type      = string
  sensitive = true
}

variable "serverless_vpc_access_connector_name" {
  type      = string
  sensitive = true
  default   = "serverless-vpc-connector"
}
