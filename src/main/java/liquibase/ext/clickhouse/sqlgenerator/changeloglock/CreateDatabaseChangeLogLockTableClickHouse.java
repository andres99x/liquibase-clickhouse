/*-
 * #%L
 * Liquibase extension for Clickhouse
 * %%
 * Copyright (C) 2020 - 2024 Genestack LTD
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
package liquibase.ext.clickhouse.sqlgenerator.changeloglock;

import java.util.Locale;

import liquibase.ext.clickhouse.database.ClickHouseDatabase;
import liquibase.ext.clickhouse.params.ClusterConfig;
import liquibase.ext.clickhouse.params.ParamsLoader;
import liquibase.ext.clickhouse.sqlgenerator.SqlGeneratorUtil;

import liquibase.database.Database;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.CreateDatabaseChangeLogLockTableGenerator;
import liquibase.statement.core.CreateDatabaseChangeLogLockTableStatement;

public class CreateDatabaseChangeLogLockTableClickHouse
    extends CreateDatabaseChangeLogLockTableGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public boolean supports(CreateDatabaseChangeLogLockTableStatement statement, Database database) {
        return database instanceof ClickHouseDatabase;
    }

    @Override
    public Sql[] generateSql(
        CreateDatabaseChangeLogLockTableStatement statement,
        Database database,
        SqlGeneratorChain sqlGeneratorChain
    ) {
        ClusterConfig properties = ParamsLoader.getLiquibaseClickhouseProperties();
        String tableName = database.getDatabaseChangeLogLockTableName();

        String createTableQuery =
            String.format(
                "CREATE TABLE IF NOT EXISTS `%s`.%s "
                    + SqlGeneratorUtil.generateSqlOnClusterClause(properties)
                    + "("
                    + "ID Int64,"
                    + "SIGN Int8,"
                    + "LOCKED UInt8,"
                    + "LOCKGRANTED Nullable(DateTime64),"
                    + "LOCKEDBY Nullable(String)"
                    + ") "
                    + generateSqlEngineClauseCollapsing(properties, tableName),
                database.getLiquibaseCatalogName(),
                tableName
            );

        return SqlGeneratorUtil.generateSql(database, createTableQuery);
    }

    private static String generateSqlEngineClauseCollapsing(
        ClusterConfig properties, String tableName
    ) {
        if (properties != null) {
            return String.format(
                "ENGINE ReplicatedCollapsingMergeTree('%s','%s', SIGN) ORDER BY (ID, LOCKED)",
                properties.getTableZooKeeperPathPrefix() + tableName.toLowerCase(Locale.ROOT),
                properties.getTableReplicaName()
            );
        }
        return "ENGINE CollapsingMergeTree(SIGN) ORDER BY (ID, LOCKED)";
    }
}
