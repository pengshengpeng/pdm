package pdm.bdpj.hivetools.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pdm.bdpj.hivetools.model.po.DemoPo;

/**
 * @Author pengshengpeng
 * @Date 2020/2/9 22:20
 **/
@Mapper
public interface DemoMapper {

    int insert(@Param("user") DemoPo user);
}
