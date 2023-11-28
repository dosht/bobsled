
#!/bin/bash

CLOUD_URL="https://europe-west4-my-projct.cloudfunctions.net/gcf_get_upload_url"
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
  -H "Authorization: bearer $(gcloud auth print-identity-token)" \
  -H "Content-Type: application/json" \
  -d '{"fileName": "x.wav"}'
