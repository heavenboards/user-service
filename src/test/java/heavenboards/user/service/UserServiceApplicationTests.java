package heavenboards.user.service;

import heavenboards.user.service.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Sql(
    scripts = {
        "classpath:sql/user/create.sql"
    },
    config = @SqlConfig(encoding = "UTF-8")
)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
class UserServiceApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(userRepository.findByUsername("user1").block());
    }
}
