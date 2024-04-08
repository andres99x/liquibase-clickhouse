/*-
 * #%L
 * Liquibase extension for ClickHouse
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

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import liquibase.change.ColumnConfig;
import liquibase.database.Database;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.SelectFromDatabaseChangeLogLockGenerator;
import liquibase.statement.core.SelectFromDatabaseChangeLogLockStatement;
import org.apache.commons.lang3.StringUtils;

public class SelectFromDatabaseChangeLogLockGeneratorClickHouse
    extends SelectFromDatabaseChangeLogLockGenerator {

  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public Sql[] generateSql(
      SelectFromDatabaseChangeLogLockStatement statement,
      final Database database,
      SqlGeneratorChain sqlGeneratorChain) {

    String selector =
        Arrays.stream(statement.getColumnsToSelect())
            .map(ColumnConfig::getName)
            .map(it -> it.equals("LOCKED") ? "max(LOCKED)" : it)
            .collect(Collectors.joining(", "));
    String groupBy = getGroupBy(selector);
    String query =
        String.format(
            "SELECT %s FROM %s.%s FINAL WHERE ID = 1 %s;",
            selector,
            database.getLiquibaseCatalogName(),
            database.getDatabaseChangeLogLockTableName(),
            groupBy);

    return new Sql[] {new UnparsedSql(query)};
  }

  private String getGroupBy(String selector) {
    String args =
        Stream.of(selector.split(", "))
            .filter(it -> !it.equals("max(LOCKED)"))
            .collect(Collectors.joining(", "));
    if (StringUtils.isBlank(args)) {
      return "";
    }
    return "GROUP BY " + args;
  }
}
