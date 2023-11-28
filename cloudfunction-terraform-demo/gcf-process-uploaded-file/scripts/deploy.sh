gcloud functions deploy get-upload-url \
  --gen2 \
  --trigger-resource transgate \
  --trigger-event google.storage.object.finalize \
  --trigger-location eu \
  --runtime=nodejs16 \
  --service-account=gcf-process-uploaded-file@my-project.iam.gserviceaccount.com \
  --region="europe-west4" \
  --no-allow-unauthenticated \
  --env-vars-file .env.yaml \
  --source=.
