/*-
 * #%L
 * Liquibase extension for ClickHouse
 * %%
 * Copyright (C) 2020 - 2023 Mediarithmics
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

import com.clickhouse.jdbc.JdbcConfig;
import liquibase.ext.clickhouse.params.StandaloneConfig;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("JUnitTestCaseWithNoTests")
@Testcontainers
public class ClickHouseTest extends BaseClickHouseTestCase {


    @BeforeAll
    static void config() throws Exception {
        setConfig(new StandaloneConfig());
    }

    @Container
    private static final ClickHouseContainer clickHouseContainer = new ClickHouseContainer(Images.CLICKHOUSE);

    @Override
    protected void doWithConnection(BaseClickHouseTestCase.ThrowingConsumer<Connection> consumer) {
        String queryString = "?clickhouse.jdbc.v1=true&" + JdbcConfig.PROP_EXTERNAL_DATABASE + "=false";
        try (Connection connection = clickHouseContainer.createConnection(queryString)) {
            consumer.accept(connection);
        } catch (Exception e) {
            fail(e);
        }
    }
}
