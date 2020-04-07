package cn.com.servyou.dao.sjck;

import cn.com.servyou.gxfx.basic.model.FphwJyje;
import cn.com.servyou.gxfx.basic.model.HwJe;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-19
 */
@Repository
public interface FphwRepository {

    /**
     * רƱ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyJxTopZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��Ʊ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyJxTopPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ר��Ʊ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyJxTopAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * רƱ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyXxTopZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��Ʊ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyXxTopPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ר��Ʊ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> qyXxTopAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * רƱ����Top
     *
     * @param xfnsrdzdah ������˰�˵��ӵ�����
     * @param gfnsrdzdah ������˰�˵��ӵ�����
     * @param begin      ��ʼʱ��
     * @param end        ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> jyTopZp(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��Ʊ����Top
     *
     * @param xfnsrdzdah ������˰�˵��ӵ�����
     * @param gfnsrdzdah ������˰�˵��ӵ�����
     * @param begin      ��ʼʱ��
     * @param end        ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> jyTopPp(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ר��Ʊ����Top
     *
     * @param xfnsrdzdah ������˰�˵��ӵ�����
     * @param gfnsrdzdah ������˰�˵��ӵ�����
     * @param begin      ��ʼʱ��
     * @param end        ����ʱ��
     * @return ���ｻ�׽��
     */
    List<HwJe> jyTopAll(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��������-רƱ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> gjhwZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��������-��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> gjhwPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ��������-ר��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> gjhwAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���ۻ���-רƱ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> xshwZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���ۻ���-��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> xshwPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���ۻ���-ר��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return �������б�
     */
    List<HwJe> xshwAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-רƱ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> syHwFpZp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> syHwFpPp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-ר��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> syHwFpAll(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-רƱ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> xyHwFpZp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> xyHwFpPp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * ���λ��﷢Ʊ-ר��Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param begin    ��ʼʱ��
     * @param end      ����ʱ��
     * @return ��Ʊ���Ｏ��
     */
    List<FphwJyje> xyHwFpAll(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);
}
