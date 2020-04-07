package cn.com.servyou.gxfx.basic.repository;

import cn.com.servyou.gxfx.basic.model.SxyType;
import cn.com.servyou.gxfx.model.Fp;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-14
 */
@Repository
public class FpRepository {
    private final ArangoDatabase db;

    @Autowired
    public FpRepository(ArangoDatabase db) {
        this.db = db;
    }

    @NonNull
    public Set<String> searchTop(@NonNull Collection<String> vIds, @NonNull SxyType sxyType, @NonNull Collection<String> cols, int limitVal) {
        Preconditions.checkArgument(limitVal >= 0, "limitVal must >= 0.");

        if (vIds.isEmpty() || cols.isEmpty() || limitVal == 0) {
            return Collections.emptySet();
        } else {
            String aql = String.format("return flatten(for id in @vIds " +
                    "return ( " +
                    "for v,e,p in 1..1 %s id PL  " +
                    "OPTIONS {bfs: true} " +
                    "let je = %s " +
                    "filter je > 0 " +
                    "sort je desc " +
                    "limit @limitVal " +
                    "return v._id))", sxyType.bound(), sumJe(cols));

            Map<String, Object> vars = new MapBuilder()
                    .put("vIds", vIds)
                    .put("limitVal", limitVal)
                    .get();

            ArangoCursor<String[]> cursor = db.query(aql, vars, null, String[].class);
            return Sets.newHashSet(cursor.iterator().next());
        }
    }

    @NonNull
    public Set<String> searchJyzb(@NonNull Collection<String> vIds, @NonNull SxyType sxyType, @NonNull Collection<String> cols, double limitVal) {
        if (vIds.isEmpty() || cols.isEmpty()) {
            return Collections.emptySet();
        } else {
            String sumJe = sumJe(cols);
            String aql = String.format("return flatten(for id in @vIds " +
                    "return ( " +
                    "let zje = ( " +
                    "for v,e,p in 1..1 %s id PL  " +
                    "OPTIONS {bfs: true} " +
                    "let je = %s " +
                    "collect aggregate zje = sum(je) " +
                    "return zje) " +
                    "" +
                    "for v,e,p in 1..1 %s id PL  " +
                    "OPTIONS {bfs: true} " +
                    "let je = %s " +
                    "filter je > 0 " +
                    "filter je/zje > @limitVal " +
                    "return v._id))", sxyType.bound(), sumJe, sxyType.bound(), sumJe);

            Map<String, Object> vars = new MapBuilder()
                    .put("vIds", vIds)
                    .put("limitVal", limitVal / 100)
                    .get();

            ArangoCursor<String[]> cursor = db.query(aql, vars, null, String[].class);
            return Sets.newHashSet(cursor.iterator().next());
        }
    }

    @NonNull
    public Map<String, Double> readZe(@NonNull Collection<String> vIds, @NonNull Collection<String> cols, @NonNull SxyType sxyType) {
        if (vIds.isEmpty() || cols.isEmpty()) {
            return Collections.emptyMap();
        } else {
            String aql = String.format("return flatten(for id in @vIds " +
                    "return ( " +
                    "for v,e,p in 1..1 %s id PL  " +
                    "OPTIONS {bfs: true} " +
                    "let je = %s " +
                    "collect aggregate ze = sum(je) " +
                    "return {id: id, ze: ze}))", sxyType.bound(), sumJe(cols));

            Map<String, Object> vars = new MapBuilder()
                    .put("vIds", vIds)
                    .get();

            ArangoCursor<IdJe[]> cursor = db.query(aql, vars, null, IdJe[].class);

            Map<String, Double> result = Maps.newHashMap();
            for (IdJe idJe : cursor.iterator().next()) {
                result.put(idJe.id, idJe.ze);
            }

            return result;
        }
    }

    @NonNull
    public <T extends Fp> Set<T> pl(@NonNull Collection<String> fromSet, @NonNull Collection<String> toSet, @NonNull Collection<String> cols, @NonNull Class<T> tClass) {
        if (fromSet.isEmpty() || toSet.isEmpty() || cols.isEmpty()) {
            return Collections.emptySet();
        } else {
            String aql = String.format("for e in PL  " +
                    "filter e._from in @froms and e._to in @tos " +
                    "let je = %s " +
                    "filter je > 0 " +
                    "return {_id: e._id, _key: e._key, _from: e._from, _to: e._to, jyje: je}", sumJe(cols));

            Map<String, Object> vars = new MapBuilder()
                    .put("froms", fromSet)
                    .put("tos", toSet)
                    .get();

            ArangoCursor<T> cursor = db.query(aql, vars, null, tClass);
            return Sets.newHashSet(cursor.iterator());
        }
    }

    private static String sumJe(Collection<String> cols) {
        return Joiner.on("+").join(Collections2.transform(cols, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return String.format("e.%s", input);
            }
        }));
    }

    public static class IdJe {
        private String id;
        private Double ze;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getZe() {
            return ze;
        }

        public void setZe(Double ze) {
            this.ze = ze;
        }
    }
}
