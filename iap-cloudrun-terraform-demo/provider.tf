provider "google" {
  credentials = ""
  project     = var.project_id
  region      = var.region
}

provider "google-beta" {
  credentials = ""
  project     = var.project_id
  region      = var.region
}
