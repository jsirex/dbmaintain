/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbmaintain.clear.impl;

import org.dbmaintain.dbsupport.DbItemIdentifier;
import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.dbsupport.DbSupports;
import org.dbmaintain.executedscriptinfo.ExecutedScriptInfoSource;
import org.dbmaintain.structure.ConstraintsDisabler;
import org.dbmaintain.structure.impl.DefaultConstraintsDisabler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.dbmaintain.util.SQLTestUtils.executeUpdate;
import static org.dbmaintain.util.SQLTestUtils.executeUpdateQuietly;
import static org.dbmaintain.util.TestUtils.getDbSupports;
import static org.dbmaintain.util.TestUtils.getDefaultExecutedScriptInfoSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Test class for the {@link org.dbmaintain.clear.DBClearer} using multiple database schemas. <p/> This test is currenlty only implemented
 * for HsqlDb
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearerMultiSchemaTest {

    /* Tested object */
    private DefaultDBClearer defaultDBClearer;

    private DataSource dataSource;
    private DbSupports dbSupports;
    private DbSupport defaultDbSupport;


    @Before
    public void setUp() throws Exception {
        dbSupports = getDbSupports("PUBLIC", "SCHEMA_A", "SCHEMA_B");
        defaultDbSupport = dbSupports.getDefaultDbSupport();
        dataSource = defaultDbSupport.getDataSource();

        ConstraintsDisabler constraintsDisabler = new DefaultConstraintsDisabler(dbSupports);
        ExecutedScriptInfoSource executedScriptInfoSource = getDefaultExecutedScriptInfoSource(defaultDbSupport, true);

        defaultDBClearer = new DefaultDBClearer(dbSupports, new HashSet<DbItemIdentifier>(), constraintsDisabler, executedScriptInfoSource);

        dropTestDatabase();
        createTestDatabase();
    }

    @After
    public void tearDown() throws Exception {
        dropTestDatabase();
    }


    @Test
    public void clearTables() throws Exception {
        assertEquals(1, defaultDbSupport.getTableNames("PUBLIC").size());
        assertEquals(1, defaultDbSupport.getTableNames("SCHEMA_A").size());
        assertEquals(1, defaultDbSupport.getTableNames("SCHEMA_B").size());
        defaultDBClearer.clearDatabase();
        assertReflectionEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("dbmaintain_scripts")), defaultDbSupport.getTableNames("PUBLIC"));
        assertTrue(defaultDbSupport.getTableNames("SCHEMA_A").isEmpty());
        assertTrue(defaultDbSupport.getTableNames("SCHEMA_B").isEmpty());
    }

    @Test
    public void clearViews() throws Exception {
        assertEquals(1, defaultDbSupport.getViewNames("PUBLIC").size());
        assertEquals(1, defaultDbSupport.getViewNames("SCHEMA_A").size());
        assertEquals(1, defaultDbSupport.getViewNames("SCHEMA_B").size());
        defaultDBClearer.clearDatabase();
        assertTrue(defaultDbSupport.getViewNames("PUBLIC").isEmpty());
        assertTrue(defaultDbSupport.getViewNames("SCHEMA_A").isEmpty());
        assertTrue(defaultDbSupport.getViewNames("SCHEMA_B").isEmpty());
    }

    @Test
    public void clearSequences() throws Exception {
        assertEquals(1, defaultDbSupport.getSequenceNames("PUBLIC").size());
        assertEquals(1, defaultDbSupport.getSequenceNames("SCHEMA_A").size());
        assertEquals(1, defaultDbSupport.getSequenceNames("SCHEMA_B").size());
        defaultDBClearer.clearDatabase();
        assertTrue(defaultDbSupport.getSequenceNames("PUBLIC").isEmpty());
        assertTrue(defaultDbSupport.getSequenceNames("SCHEMA_A").isEmpty());
        assertTrue(defaultDbSupport.getSequenceNames("SCHEMA_B").isEmpty());
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase() throws Exception {
        // create schemas
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        // create tables
        executeUpdate("create table TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_A.TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_B.TEST_TABLE (col1 varchar(100))", dataSource);
        // create views
        executeUpdate("create view TEST_VIEW as select col1 from TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_A.TEST_VIEW as select col1 from SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_B.TEST_VIEW as select col1 from SCHEMA_B.TEST_TABLE", dataSource);
        // create sequences
        executeUpdate("create sequence TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void dropTestDatabase() throws Exception {
        // drop sequences
        executeUpdateQuietly("drop sequence TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
        // drop views
        executeUpdateQuietly("drop view TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_A.TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_B.TEST_VIEW", dataSource);
        // drop tables
        executeUpdateQuietly("drop table dbmaintain_scripts", dataSource);
        executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
        // drop schemas
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }
}
