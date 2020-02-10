originPath=$1
targetPath=$2
echo ${originPath}
echo ${targetPath}
hdfs dfs -put ${originPath} ${targetPath}