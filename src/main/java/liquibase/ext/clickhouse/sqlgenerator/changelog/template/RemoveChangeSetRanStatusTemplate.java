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
package liquibase.ext.clickhouse.sqlgenerator.changelog.template;

import liquibase.ext.clickhouse.params.LiquibaseClickHouseConfig;
import liquibase.ext.clickhouse.sqlgenerator.LiquibaseSqlTemplate;
import liquibase.ext.clickhouse.sqlgenerator.OnClusterTemplate;

import liquibase.database.Database;
import liquibase.statement.core.RemoveChangeSetRanStatusStatement;

public class RemoveChangeSetRanStatusTemplate extends LiquibaseSqlTemplate<String> {

    private final Database database;
    private final RemoveChangeSetRanStatusStatement statement;
    private final OnClusterTemplate onClusterTemplate;

    public RemoveChangeSetRanStatusTemplate(Database database, RemoveChangeSetRanStatusStatement statement) {
        this.database = database;
        this.statement = statement;
        this.onClusterTemplate = new OnClusterTemplate();
    }


    @Override
    public String visitDefault(LiquibaseClickHouseConfig clickHouseConfig) {
        var changeSet = statement.getChangeSet();
        return String.format(
            "DELETE FROM `%s`.%s "
                + clickHouseConfig.accept(onClusterTemplate)
                + " WHERE ID = '%s' AND AUTHOR = '%s' AND FILENAME = '%s'",
            database.getLiquibaseCatalogName(),
            database.getDatabaseChangeLogTableName(),
            changeSet.getId(),
            changeSet.getAuthor(),
            changeSet.getFilePath());
    }
}
