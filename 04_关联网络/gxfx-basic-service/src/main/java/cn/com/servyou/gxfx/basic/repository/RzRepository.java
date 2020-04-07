package cn.com.servyou.gxfx.basic.repository;

import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
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
 * 2018-11-15
 */
@Repository
public class RzRepository {
    private final ArangoDatabase db;

    @Autowired
    public RzRepository(ArangoDatabase db) {
        this.db = db;
    }

    @NonNull
    public Set<String> searchRyglqy(@NonNull String id, @NonNull Collection<RzType> rzTypes) {
        if (rzTypes.isEmpty()) {
            return Collections.emptySet();
        } else {
            String rzType = allType(rzTypes);

            String aql = String.format("let rys = (for e in RZ " +
                    "filter e._to == @id " +
                    "filter %s " +
                    "return e._from) " +
                    "" +
                    "for e in RZ " +
                    "filter e._from in rys " +
                    "filter e._to != @id " +
                    "filter %s " +
                    "return e._to", rzType, rzType);

            Map<String, Object> vars = new MapBuilder()
                    .put("id", id)
                    .get();

            ArangoCursor<String> cursor = db.query(aql, vars, null, String.class);
            return Sets.newHashSet(cursor.iterator());
        }
    }

    @NonNull
    public <T extends Rz> Set<T> readRz(@NonNull Collection<String> qyIds, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass) {
        if (qyIds.isEmpty() || rzTypes.isEmpty()) {
            return Collections.emptySet();
        } else {
            String rzType = allType(rzTypes);

            String aql = String.format("let zrrs = (for e in RZ " +
                    "filter e._to in @qys " +
                    "filter %s " +
                    "collect from = e._from with count into num " +
                    "filter num > 1 " +
                    "SORT null " +
                    "return from) " +
                    "for e in RZ " +
                    "filter e._from in zrrs and e._to in @qys " +
                    "filter %s " +
                    "return e", rzType, rzType);

            Map<String, Object> vars = new MapBuilder()
                    .put("qys", qyIds)
                    .get();

            ArangoCursor<T> cursor = db.query(aql, vars, null, tClass);
            return Sets.newHashSet(cursor.iterator());
        }
    }

    @NonNull
    public <T extends Rz> Set<T> readRz(@NonNull Collection<String> newIds, @NonNull Collection<String> allIds, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass) {
        if (newIds.isEmpty() || allIds.isEmpty() || rzTypes.isEmpty()) {
            return Collections.emptySet();
        } else {
            String rzType = allType(rzTypes);

            String aql = String.format("let zrrs = (for e in RZ " +
                    "filter e._to in @aSet " +
                    "filter %s " +
                    "return e._from) " +
                    "" +
                    "let zs = (for e in RZ " +
                    "filter e._from in zrrs " +
                    "filter e._to in @bSet " +
                    "filter %s " +
                    "collect from = e._from with count into num " +
                    "filter num > 1 " +
                    "SORT null " +
                    "return from) " +
                    "" +
                    "for e in RZ " +
                    "filter e._from in zs " +
                    "filter e._to in @bSet " +
                    "filter %s " +
                    "return e", rzType, rzType, rzType);

            Map<String, Object> vars = new MapBuilder()
                    .put("aSet", newIds)
                    .put("bSet", allIds)
                    .get();

            ArangoCursor<T> cursor = db.query(aql, vars, null, tClass);
            return Sets.newHashSet(cursor.iterator());
        }
    }

    private static String allType(Collection<RzType> rzTypes) {
        return Joiner.on(" or ").join(Collections2.transform(rzTypes, new Function<RzType, String>() {
            @Override
            public String apply(RzType input) {
                return String.format("e.%s", input.name());
            }
        }));
    }
}
