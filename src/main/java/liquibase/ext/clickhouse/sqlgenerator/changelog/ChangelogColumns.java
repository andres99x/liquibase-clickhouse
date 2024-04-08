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
package liquibase.ext.clickhouse.sqlgenerator.changelog;

public enum ChangelogColumns {
  ID,
  AUTHOR,
  FILENAME,
  DATEEXECUTED,
  ORDEREXECUTED,
  EXECTYPE,
  MD5SUM,
  DESCRIPTION,
  COMMENTS,
  TAG,
  LIQUIBASE,
  CONTEXTS,
  LABELS,
  DEPLOYMENT_ID;

  @Override
  public String toString() {
    return name().toUpperCase();
  }
}
