package data.util;

import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import data.calculate.function.SumFunction;
import org.assertj.core.util.Lists;

import java.util.HashMap;

public class AviatorTest {
    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new SumFunction());

        HashMap<String, Object> env = Maps.newHashMap();
        env.put("_666997336584622080__zxyg__INSTANT", Lists.newArrayList(1.0, 2.0, 3.0));
        Object val = AviatorEvaluator.execute("sum(_666997336584622080__zxyg__INSTANT)", env);
        System.out.println(val);
    }
}
