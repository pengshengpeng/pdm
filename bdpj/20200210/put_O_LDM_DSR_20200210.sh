hive -e 'load data local inpath '/user/hive/data/etl/' overwrite into table PDM.O_LDM_DSR_${hiveconf:yyyymmdd}'