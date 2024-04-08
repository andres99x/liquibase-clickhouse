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
package liquibase.ext.clickhouse.sqlgenerator.changelog;

import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.AUTHOR;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.COMMENTS;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.CONTEXTS;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.DATEEXECUTED;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.DEPLOYMENT_ID;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.DESCRIPTION;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.EXECTYPE;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.FILENAME;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.ID;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.LABELS;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.LIQUIBASE;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.MD5SUM;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.ORDEREXECUTED;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.TAG;

import java.util.Locale;

import liquibase.ext.clickhouse.database.ClickHouseDatabase;
import liquibase.ext.clickhouse.params.ClusterConfig;
import liquibase.ext.clickhouse.params.ParamsLoader;
import liquibase.ext.clickhouse.sqlgenerator.SqlGeneratorUtil;

import liquibase.database.Database;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.CreateDatabaseChangeLogTableGenerator;
import liquibase.statement.core.CreateDatabaseChangeLogTableStatement;

public class CreateDatabaseChangeLogTableClickHouse extends CreateDatabaseChangeLogTableGenerator {

  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public boolean supports(CreateDatabaseChangeLogTableStatement statement, Database database) {
    return database instanceof ClickHouseDatabase;
  }

  @Override
  public Sql[] generateSql(
      CreateDatabaseChangeLogTableStatement statement,
      Database database,
      SqlGeneratorChain sqlGeneratorChain) {
    ClusterConfig properties = ParamsLoader.getLiquibaseClickhouseProperties();
    String tableName = database.getDatabaseChangeLogTableName();

    String createTableQuery =
        String.format(
            "CREATE TABLE IF NOT EXISTS `%s`.%s "
                + SqlGeneratorUtil.generateSqlOnClusterClause(properties)
                + "("
                + ID
                + " String,"
                + AUTHOR
                + " String,"
                + FILENAME
                + " String,"
                + DATEEXECUTED
                + " DateTime64,"
                + ORDEREXECUTED
                + " UInt64,"
                + EXECTYPE
                + " String,"
                + MD5SUM
                + " Nullable(String),"
                + DESCRIPTION
                + " Nullable(String),"
                + COMMENTS
                + " Nullable(String),"
                + TAG
                + " Nullable(String),"
                + LIQUIBASE
                + " Nullable(String),"
                + CONTEXTS
                + " Nullable(String),"
                + LABELS
                + " Nullable(String),"
                + DEPLOYMENT_ID
                + " Nullable(String)) "
                + generateSqlEngineClauseReplacing(properties, tableName),
            database.getLiquibaseCatalogName(),
            tableName);

    return SqlGeneratorUtil.generateSql(database, createTableQuery);
  }

  private static String generateSqlEngineClauseReplacing(
      ClusterConfig properties, String tableName) {
    if (properties != null) {
      return String.format(
          "ENGINE ReplicatedReplacingMergeTree('%s','%s') ORDER BY ID",
          properties.getTableZooKeeperPathPrefix() + tableName.toLowerCase(Locale.ROOT),
          properties.getTableReplicaName());
    } else {
      return String.format("ENGINE ReplacingMergeTree() ORDER BY ID");
    }
  }
}
