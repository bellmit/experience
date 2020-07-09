package data.domain;

import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.grape.BaseDomain;
import org.springframework.util.Base64Utils;

import javax.persistence.Entity;
import java.nio.charset.StandardCharsets;

@ApiModel(value = "参数标签", parent = BaseDomain.class)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DataParameterTag extends BaseDomain {
    public DataParameterTag(String name) {
        setName(name);
    }

    private String nameToId(String name) {
        return Base64Utils.encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        super.setId(nameToId(name));
    }
}
