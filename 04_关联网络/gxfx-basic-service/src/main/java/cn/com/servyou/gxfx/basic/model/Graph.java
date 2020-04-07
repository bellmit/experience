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
         * 中心节点ID
         *
         * @return Vertex/1234567890
         */
        String getCenterNode();

        /**
         * 上游深度
         *
         * @return 上游深度
         */
        int getSyDeep();

        /**
         * 下游深度
         *
         * @return 下游深度
         */
        int getXyDeep();

        /**
         * 上游参数
         *
         * @return 上游参数
         */
        FpFilterParams getSyParams();

        /**
         * 下游参数
         *
         * @return 下游参数
         */
        FpFilterParams getXyParams();

        /**
         * 任职类型
         *
         * @return 任职类型
         */
        Set<RzType> getRzTypes();

        /**
         * 企业类
         *
         * @return 企业类
         */
        Class<Q> qyClass();

        /**
         * 责任人类
         *
         * @return 责任人类
         */
        Class<Z> zrrClass();

        /**
         * 发票类
         *
         * @return 发票类
         */
        Class<F> fpClass();

        /**
         * 任职类
         *
         * @return 任职类
         */
        Class<R> rzClass();
    }

    public interface ExpandParams<Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> {

        /**
         * 展开节点ID
         *
         * @return 展开节点ID
         */
        String getExpandNode();

        /**
         * 前端已经存在的节点，包括 企业 和 责任人
         *
         * @return 前端已经存在的节点，包括 企业 和 责任人
         */
        Set<String> getExistNodes();

        /**
         * 上游参数
         *
         * @return 上游参数
         */
        FpFilterParams getSyParams();

        /**
         * 下游参数
         *
         * @return 下游参数
         */
        FpFilterParams getXyParams();

        /**
         * 发票数据参数
         *
         * @return 发票数据参数
         */
        FpDataParams getDataParams();

        /**
         * 任职类型
         *
         * @return 任职类型
         */
        Set<RzType> getRzTypes();

        /**
         * 企业类
         *
         * @return 企业类
         */
        Class<Q> qyClass();

        /**
         * 责任人类
         *
         * @return 责任人类
         */
        Class<Z> zrrClass();

        /**
         * 发票类
         *
         * @return 发票类
         */
        Class<F> fpClass();

        /**
         * 任职类
         *
         * @return 任职类
         */
        Class<R> rzClass();
    }
}
