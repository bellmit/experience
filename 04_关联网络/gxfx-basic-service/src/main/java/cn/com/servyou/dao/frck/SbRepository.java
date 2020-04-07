package cn.com.servyou.dao.frck;

import cn.com.servyou.gxfx.basic.model.SbQysds;
import cn.com.servyou.gxfx.basic.model.SbZzs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author wucq
 */
@Repository
public interface SbRepository {

    SbZzs getZzs(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    Double getXfsYnse(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    SbQysds getQysds(@Param("nsrdzdah") String nsrdzdah, @Param("year") String year);

    Double getRkse(@Param("nsrdzdah") String nsrdzdah, @Param("zsxmId") String zsxmId, @Param("begin") String begin, @Param("end") String end);
}
