data_folder=$1
mkdir -p ${data_folder}
gsutil -m cp "gs://applepen-input-1/*.csv" ${data_folder}
