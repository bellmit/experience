package cn.com.servyou.gxfx.basic.model;

import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;
import cn.com.servyou.gxfx.model.Vertex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-08-07
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Graph<Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> {
    private Map<String, Q> qyMap;
    private Map<String, Z> zrrMap;
    private Set<F> fps;
    private Set<R> rzs;

    public interface InitParams<Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> {
        /**
         * ���Ľڵ�ID
         *
         * @return Vertex/1234567890
         */
        String getCenterNode();

        /**
         * �������
         *
         * @return �������
         */
        int getSyDeep();

        /**
         * �������
         *
         * @return �������
         */
        int getXyDeep();

        /**
         * ���β���
         *
         * @return ���β���
         */
        FpFilterParams getSyParams();

        /**
         * ���β���
         *
         * @return ���β���
         */
        FpFilterParams getXyParams();

        /**
         * ��ְ����
         *
         * @return ��ְ����
         */
        Set<RzType> getRzTypes();

        /**
         * ��ҵ��
         *
         * @return ��ҵ��
         */
        Class<Q> qyClass();

        /**
         * ��������
         *
         * @return ��������
         */
        Class<Z> zrrClass();

        /**
         * ��Ʊ��
         *
         * @return ��Ʊ��
         */
        Class<F> fpClass();

        /**
         * ��ְ��
         *
         * @return ��ְ��
         */
        Class<R> rzClass();
    }

    public interface ExpandParams<Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> {

        /**
         * չ���ڵ�ID
         *
         * @return չ���ڵ�ID
         */
        String getExpandNode();

        /**
         * ǰ���Ѿ����ڵĽڵ㣬���� ��ҵ �� ������
         *
         * @return ǰ���Ѿ����ڵĽڵ㣬���� ��ҵ �� ������
         */
        Set<String> getExistNodes();

        /**
         * ���β���
         *
         * @return ���β���
         */
        FpFilterParams getSyParams();

        /**
         * ���β���
         *
         * @return ���β���
         */
        FpFilterParams getXyParams();

        /**
         * ��Ʊ���ݲ���
         *
         * @return ��Ʊ���ݲ���
         */
        FpDataParams getDataParams();

        /**
         * ��ְ����
         *
         * @return ��ְ����
         */
        Set<RzType> getRzTypes();

        /**
         * ��ҵ��
         *
         * @return ��ҵ��
         */
        Class<Q> qyClass();

        /**
         * ��������
         *
         * @return ��������
         */
        Class<Z> zrrClass();

        /**
         * ��Ʊ��
         *
         * @return ��Ʊ��
         */
        Class<F> fpClass();

        /**
         * ��ְ��
         *
         * @return ��ְ��
         */
        Class<R> rzClass();
    }
}
