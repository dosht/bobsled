data_folder=$1
gsutil hash -mh "gs://applepen-input-1/*.csv" | scripts/checksum.sh ${data_folder}
