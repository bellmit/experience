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
     * ������˰�˵��ӵ����ţ���ȡ��˰���������
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @return ��˰�˻�����Ϣ
     */
    Nsr getNsr(@Param("nsrdzdah") String nsrdzdah);

    /**
     * ������ȡ��˰�˻�����Ϣ
     *
     * @param nsrdzdahs ��˰�˵��ӵ����ż���
     * @return ��˰�˻�����Ϣ
     */
    @MapKey("nsrdzdah")
    Map<String, Nsr> getNsrBatch(@Param("nsrdzdahs") Collection<String> nsrdzdahs);

    /**
     * ������˰�ˣ�ģ�������������� ���ơ�ʶ��š�������ô��룻
     * ����ǰ���ȵ��� PageHelper.startPage(pageIndex, pageSize);
     *
     * @param search ��������
     * @return ������������˰�ˣ���ҳ��ѯ
     */
    Page<Nsr> search(@Param("search") Nsr.SearchCondition search);
}
