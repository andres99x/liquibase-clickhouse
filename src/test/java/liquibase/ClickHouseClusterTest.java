/*-
 * #%L
 * Liquibase extension for ClickHouse
 * %%
 * Copyright (C) 2024 - 2025 Genestack Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package liquibase;

import com.clickhouse.jdbc.ClickHouseDriver;
import liquibase.ext.clickhouse.params.ClusterConfig;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ClickHouseClusterTest extends BaseClickHouseTestCase {

    private static final DockerComposeContainer<?> container = withClickHouseCluster();

    @BeforeAll
    static void config() throws Exception {
        setConfig(new ClusterConfig("default", "/liquibase"));
    }

    @Override
    void canTagDatabase() {
        runLiquibase(
            "changelog-cluster.xml",
            (liquibase, connection) -> {
                liquibase.update("");
                liquibase.tag("testTag");
            }
        );
    }

    @Override
    void canRollbackChangelog() {
        runLiquibase(
            "changelog-cluster.xml",
            (liquibase, connection) -> {
                liquibase.update("");
                liquibase.rollback(2, "");
            }
        );
    }

    @Test
    void canPerformCustomMigration() {
        @Language("ClickHouse")
        final String checkStatement = "select count(*) from DataByRowDist where item = 'custom'";
        runLiquibase(
            "changelog-cluster.xml",
            (liquibase, connection) -> {
                liquibase.update("");
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(checkStatement);
                    try (var rs = stmt.getResultSet()) {
                        rs.next();
                        assertEquals(1, rs.getInt(1));
                    } catch (AssertionError e) {
                        // poor man's backoff
                        TimeUnit.SECONDS.sleep(5);
                        try (Statement stmt2 = connection.createStatement()) {
                            stmt2.execute(checkStatement);
                            try (var rs2 = stmt2.getResultSet()) {
                                rs2.next();
                                assertEquals(1, rs2.getInt(1));
                            }
                        }
                    }
                }
            }
        );
    }


    @RepeatedTest(30)
    void canRunComplexMigrationsWithInsertions() {
        runLiquibase(
            "changelog-cluster.xml",
            (liquibase, connection) -> {
                liquibase.update("");
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(
                        "select fileId, _shard_num from DataByRowDist as D join IndexDict as I on D.rowId = I.rowId order by fileId");
                    try (var rs = stmt.getResultSet()) {
                        Map<Integer, Integer> buffer = new HashMap<>();
                        while (rs.next()) {
                            var fileId = rs.getInt(1);
                            var shardNum = rs.getInt(2);
                            if (buffer.containsKey(fileId)) {
                                assertEquals(
                                    buffer.get(fileId),
                                    shardNum,
                                    "fileId="
                                    + fileId
                                    + " found @ shardNum="
                                    + shardNum
                                    + " and @ shardNum="
                                    + buffer.get(fileId)
                                );
                            } else {
                                buffer.put(fileId, shardNum);
                            }
                        }
                    }
                }
            }
        );
    }

    @AfterAll
    static void stop() {
        container.close();
    }

    @Override
    protected void doWithConnection(ThrowingConsumer<Connection> callback) {
        try {
            Driver driver = new ClickHouseDriver();
            String url =
                "jdbc:clickhouse://localhost:" + container.getServicePort("nginx", 8123) + "/default";
            Properties properties = new Properties();
            properties.put("user", "default");
            properties.put("password", "");
            try (Connection con = driver.connect(url, properties)) {
                callback.accept(con);
            }
        } catch (Exception e) {
            fail(e);
        }
    }

    protected static DockerComposeContainer<?> withClickHouseCluster() {
        URI uri;
        try {
            uri =
                ClickHouseClusterTest.class
                    .getClassLoader()
                    .getResource("clickhouse-cluster/clickhouse-s2r2-compose.yml")
                    .toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        var container =
            new DockerComposeContainer(new File(uri))
                .withExposedService("nginx", 8123)
                // for debug
                .withExposedService("clickhouse-s2r2", 9000);
        container.start();
        return container;
    }
}
