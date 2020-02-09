use odm;
drop table if exitsts odm. o_ldm_dsr_${hiveconf:yyyymmdd};
ceate external table if not exists odm. o_ldm_dsr_${hiveconf:yyyymmdd}(
Dsrbh string COMMENT '当事人编号',
dsrlxdm string COMMENT '当事人类型代码'
cif2khh string COMMENT 'CIF2客户号',
ecifh string COMMENT 'ECIF号',
zkhbz string COMMENT '主客户标志',
sxrq date COMMENT '生效日期',
sxrq date COMMENT '失效日期',
)
row format delimited
fields by '/001'
file_format TEXTFILE
LOCATION '/user/hive/data/etl/${hiveconf:yyyymmdd}/o_ldm_dsr/'
COMMENT '当事人';
