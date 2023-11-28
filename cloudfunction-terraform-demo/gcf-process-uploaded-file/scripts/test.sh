
#!/bin/bash

CLOUD_URL="https://europe-west4-my-project.cloudfunctions.net/gcf_process_uploaded_file"
LOCAL_URL="http://localhost:8080"

# Check if an argument is provided and it is either "prod" or "local"
if [ "$1" == "prod" ]; then
  # Use the CLOUD_URL for "prod"
  URL="$CLOUD_URL"
elif [ "$1" == "local" ]; then
  # Use the LOCAL_URL for "local"
  URL="$LOCAL_URL"
else
  # Invalid argument or no argument provided, use the default LOCAL_URL
  URL="$LOCAL_URL"
fi

# Now you can use the URL variable as needed in your script
echo "Using URL: $URL"

curl --max-time 70 -X POST $URL \
  -H "ce-id: 123451234512345" \
  -H "ce-specversion: 1.0" \
  -H "ce-time: 2022-12-31T00:00:00.0Z" \
  -H "ce-type: google.cloud.storage.object.v1.finalized" \
  -H "ce-source: //storage.googleapis.com/projects/_/buckets/transgate-audios" \
  -H "ce-subject: objects/userId=69ddb2a9-0415-40ff-b66f-63baff060e9c/jobId=352/Ticari ve Sosyal Projeler Ek.5 Bilgi formu ve Ek.4 Komisyon Listesi V3.wav" \
  -d '{
        "bucket": "transgate-audios",
        "contentType": "text/plain",
        "kind": "storage#object",
        "md5Hash": "...",
        "metageneration": "1",
        "name": "userId=69ddb2a9-0415-40ff-b66f-63baff060e9c/jobId=352/Ticari ve Sosyal Projeler Ek.5 Bilgi formu ve Ek.4 Komisyon Listesi V3.wav",
        "size": "352",
        "storageClass": "MULTI_REGIONAL",
        "timeCreated": "2022-12-31T00:00:00.0Z",
        "timeStorageClassUpdated": "2022-12-31T00:00:00.0Z",
        "updated": "2022-12-31T00:00:00.0Z"
      }' \
  -H "Authorization: bearer $(gcloud auth print-identity-token)" \
  -H "Content-Type: application/json"
