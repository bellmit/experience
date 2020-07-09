package data.calculate.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Collection;
import java.util.Map;

public class SumFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object obj = FunctionUtils.getJavaObject(arg1, env);
        if (obj instanceof Collection) {
            Collection<Double> set = (Collection<Double>) obj;
            double sum = set.stream().mapToDouble(d -> d).sum();
            return new AviatorDouble(sum);
        } else {
            return new AviatorDouble(-1.0);
        }
    }
}
