package cn.com.servyou.dao.sjck;

import cn.com.servyou.gxfx.basic.model.Nsr;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-12
 */
@Repository
public interface NsrRepository {
    /**
     * 根据纳税人电子档案号，获取纳税人相关属性
     *
     * @param nsrdzdah 纳税人电子档案号
     * @return 纳税人基本信息
     */
    Nsr getNsr(@Param("nsrdzdah") String nsrdzdah);

    /**
     * 批量获取纳税人基本信息
     *
     * @param nsrdzdahs 纳税人电子档案号集合
     * @return 纳税人基本信息
     */
    @MapKey("nsrdzdah")
    Map<String, Nsr> getNsrBatch(@Param("nsrdzdahs") Collection<String> nsrdzdahs);

    /**
     * 搜索纳税人，模糊搜索，可以是 名称、识别号、社会信用代码；
     * 调用前，先调用 PageHelper.startPage(pageIndex, pageSize);
     *
     * @param search 搜索条件
     * @return 符合条件的纳税人，分页查询
     */
    Page<Nsr> search(@Param("search") Nsr.SearchCondition search);
}
