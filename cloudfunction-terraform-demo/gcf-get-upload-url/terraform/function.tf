terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.34.0"
    }
  }
}

resource "random_id" "default" {
  byte_length = 8
}

resource "google_storage_bucket" "default" {
  name                        = "${random_id.default.hex}-gcf-source" # Every bucket name must be globally unique
  location                    = "EU"
  uniform_bucket_level_access = true
}

data "archive_file" "default" {
  type        = "zip"
  output_path = "/tmp/function-source.zip"
  source_dir  = "../build"
}

resource "google_storage_bucket_object" "object" {
  name   = "function-source.${data.archive_file.default.output_md5}.zip"
  bucket = google_storage_bucket.default.name
  source = data.archive_file.default.output_path # Add path to the zipped function source code
}

resource "google_cloudfunctions2_function" "default" {
  name        = "gcf_get_upload_url"
  location    = "europe-west4"
  description = "A template GCP Functions project"

  build_config {
    runtime     = "nodejs18"
    entry_point = "gcf_get_upload_url"
    source {
      storage_source {
        bucket = google_storage_bucket.default.name
        object = google_storage_bucket_object.object.name
      }
    }
  }

  service_config {
    max_instance_count = 1
    available_memory   = "256M"
    timeout_seconds    = 60
    service_account_email = google_service_account.account.email
  }
  }

resource "google_service_account" "account" {
  account_id   = "gcf-get-upload-url"
  display_name = "GCF Get Upload URL Service Account - used for both the cloud function and eventarc trigger"
}

output "function_uri" {
  value = google_cloudfunctions2_function.default.service_config[0].uri
}

# Grant Cloud Function IAM permissions for singing gcs urls
# Permitions required: 
#   iam.serviceAccounts.signBlob
#   storage.objects.create
#   storage.objects.get
#   storage.objects.list
resource "google_project_iam_member" "gcf_service_agent" {
  project = data.google_project.project.project_id
  role    = "roles/cloudfunctions.serviceAgent"
  member  = "serviceAccount:${google_service_account.account.email}"
}
