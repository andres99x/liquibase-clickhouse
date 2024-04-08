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
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.FILENAME;
import static liquibase.ext.clickhouse.sqlgenerator.changelog.ChangelogColumns.MD5SUM;

import liquibase.ext.clickhouse.database.ClickHouseDatabase;

import liquibase.ChecksumVersion;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.UpdateChangeSetChecksumGenerator;
import liquibase.statement.core.UpdateChangeSetChecksumStatement;

public class UpdateChangeSetChecksumClickHouse extends UpdateChangeSetChecksumGenerator {

  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public boolean supports(UpdateChangeSetChecksumStatement statement, Database database) {
    return database instanceof ClickHouseDatabase;
  }

  @Override
  public Sql[] generateSql(
      UpdateChangeSetChecksumStatement statement,
      Database database,
      SqlGeneratorChain sqlGeneratorChain) {

    ChangeSet changeSet = statement.getChangeSet();

    return new Sql[] {
      ChangelogEntries.updateStatement(
          database,
          changeSet.getId(),
          map -> {
            map.put(MD5SUM, changeSet.generateCheckSum(ChecksumVersion.latest()).toString());
            map.put(AUTHOR, changeSet.getAuthor());
            map.put(FILENAME, changeSet.getFilePath());
          })
    };
  }
}
