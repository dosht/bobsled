#!/bin/bash

# example:
#Hashes [hex] for WY-s5-supply.csv:
 #	Hash (md5):		c54b45b91da32cbd7c29892a4c21f7a9
 #	Hash (crc32c):		b1fad895
EXIT=0
data_folder=$1
while IFS= read -r line1 && IFS= read -r line2 && IFS= read -r line3; do
  file_name=$(echo "$line1" | cut -f4 -d' ' | cut -f1 -d':')
  md5_hash=$(echo "$line2" | cut -d ':' -f 2 | awk '{$1=$1};1')
  md5_hash_downloaded=$(md5sum ${data_folder}/${file_name} | cut -f1 -d' ')
  if [[ "$md5_hash" != "$md5_hash_downloaded" ]]; then
    printf '%-25s %s\n' ${file_name} "[FAIL]"
    EXIT=1
  else
    printf '%-25s %s\n' ${file_name} "[OK]"
  fi
done

exit $EXIT
