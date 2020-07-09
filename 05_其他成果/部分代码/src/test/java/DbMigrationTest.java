import org.grape.GrapeDbMigration;
import org.junit.Test;

import java.io.IOException;

public class DbMigrationTest {

    @Test
    public void test() throws IOException {
        GrapeDbMigration dbm = new GrapeDbMigration();
        dbm.generate("data", "1.0.0");
    }
}
