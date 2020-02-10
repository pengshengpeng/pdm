date=$1
echo ${date}
hive --hiveconf yyyymmdd='${date}' -f create_O_LDM_DSR_${date}.sql