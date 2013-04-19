package org.dbmaintain.script.executedscriptinfo.impl;

import org.apache.commons.dbutils.DbUtils;
import org.dbmaintain.database.Database;
import org.dbmaintain.database.SQLHandler;
import org.dbmaintain.script.InvalidObject;
import org.dbmaintain.script.ScriptFactory;
import org.dbmaintain.script.executedscriptinfo.DatabaseStatusInfoSource;
import org.dbmaintain.util.DbMaintainException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrei_Akatsyeu
 * Date: 4/18/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDatabaseStatusInfoSource extends DefaultExecutedScriptInfoSource implements DatabaseStatusInfoSource {

    public static final int COLUMN_OWNER = 1;

    public static final int COLUMN_OBJECT_NAME = 2;

    public static final int COLUMN_OBJECT_TYPE = 3;

    public AbstractDatabaseStatusInfoSource(boolean autoCreateExecutedScriptsTable,
                                            String executedScriptsTableName,
                                            String fileNameColumnName,
                                            int fileNameColumnSize,
                                            String fileLastModifiedAtColumnName,
                                            String checksumColumnName,
                                            int checksumColumnSize,
                                            String executedAtColumnName,
                                            int executedAtColumnSize,
                                            String succeededColumnName,
                                            DateFormat timestampFormat,
                                            Database defaultSupport,
                                            SQLHandler sqlHandler,
                                            ScriptFactory scriptFactory) {
        super(autoCreateExecutedScriptsTable,
                executedScriptsTableName,
                fileNameColumnName,
                fileNameColumnSize,
                fileLastModifiedAtColumnName,
                checksumColumnName,
                checksumColumnSize,
                executedAtColumnName,
                executedAtColumnSize,
                succeededColumnName,
                timestampFormat,
                defaultSupport,
                sqlHandler,
                scriptFactory);
    }

    /**
     * Get query for look invalid objects
     *
     * @return query string
     */
    public abstract String getQueryString();

    /**
     * Get information about invalid objects from
     *
     */
    public List<InvalidObject> getIvalidObjectsInfo() {
        List<InvalidObject> invalidObjects = new ArrayList<InvalidObject>();
        InvalidObject invalidObject;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = defaultDatabase.getDataSource().getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery(getQueryString());

            while (resultSet.next()) {
                invalidObject = new InvalidObject();
                invalidObject.setOwner(resultSet.getString(COLUMN_OWNER));
                invalidObject.setObjectName(resultSet.getString(COLUMN_OBJECT_NAME));
                invalidObject.setObjectType(resultSet.getString(COLUMN_OBJECT_TYPE));
                invalidObjects.add(invalidObject);
            }
        } catch (SQLException e) {
            throw new DbMaintainException("Error while retrieving database version", e);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        return invalidObjects;
    }
}
