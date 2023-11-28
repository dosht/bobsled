gcloud functions deploy get-upload-url \
  --gen2 \
  --trigger-http \
  --runtime=nodejs18 \
  --service-account=gcf-get-upload-url@my-project.iam.gserviceaccount.com \
  --region="europe-west4" \
  --no-allow-unauthenticated \
  --env-vars-file .env.yaml \
  --source=.
