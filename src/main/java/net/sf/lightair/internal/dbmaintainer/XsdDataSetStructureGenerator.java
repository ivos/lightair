package net.sf.lightair.internal.dbmaintainer;

import static org.unitils.core.dbsupport.DbSupportFactory.*;
import static org.unitils.core.util.ConfigUtils.*;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.*;
import static org.unitils.util.PropertyUtils.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.util.PropertyUtils;

public class XsdDataSetStructureGenerator extends
		org.unitils.dbmaintainer.structure.impl.XsdDataSetStructureGenerator {

	private String complexTypeSuffix;
	private final String profile;

	public XsdDataSetStructureGenerator(String profile) {
		this.profile = profile;
	}

	@Override
	protected void doInit(Properties configuration) {
		super.doInit(configuration);
		complexTypeSuffix = PropertyUtils.getString(
				PROPKEY_XSD_COMPLEX_TYPE_SUFFIX, configuration);
		dbSupports = getDbSupports(configuration, sqlHandler);
		defaultDbSupport = getDefaultDbSupport(configuration, sqlHandler);
	}

	private List<DbSupport> getDbSupports(Properties configuration,
			SQLHandler sqlHandler) {
		List<DbSupport> result = new ArrayList<DbSupport>();
		List<String> schemaNames = getStringList(
				DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES, configuration,
				true);
		Collections.sort(schemaNames);
		for (String schemaName : schemaNames) {
			DbSupport dbSupport = getDbSupport(configuration, sqlHandler,
					schemaName);
			result.add(dbSupport);
		}
		return result;
	}

	private DbSupport getDbSupport(Properties configuration,
			SQLHandler sqlHandler, String schemaName) {
		DbSupport dbSupport;
		String databaseDialect = getString(
				DbSupportFactory.PROPKEY_DATABASE_DIALECT, configuration);
		dbSupport = getInstanceOf(DbSupport.class, configuration,
				databaseDialect);
		dbSupport.init(configuration, sqlHandler, schemaName);
		return dbSupport;
	}

	public DbSupport getDefaultDbSupport(Properties configuration,
			SQLHandler sqlHandler) {
		String defaultSchemaName = getStringList(PROPKEY_DATABASE_SCHEMA_NAMES,
				configuration, true).get(0);
		return getDbSupport(configuration, sqlHandler, defaultSchemaName);
	}

	private String getProfileSuffix() {
		String suffix = "";
		if (!StringUtils.isBlank(profile)) {
			suffix = "-" + profile;
		}
		return suffix;
	}

	@Override
	protected void generateDataSetXsd(File xsdDirectory) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(xsdDirectory,
					"dataset" + getProfileSuffix() + ".xsd")));

			String defaultSchemaName = defaultDbSupport.getSchemaName();
			writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns:dflt=\""
					+ defaultSchemaName + "\">\n");

			for (DbSupport dbSupport : dbSupports) {
				String schemaName = dbSupport.getSchemaName();
				writer.write("\t<xsd:import namespace=\"" + schemaName
						+ "\" schemaLocation=\"" + schemaName
						+ getProfileSuffix() + ".xsd\" />\n");
			}

			writer.write("\t<xsd:element name=\"dataset\">\n");
			writer.write("\t\t<xsd:complexType>\n");
			writer.write("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");

			List<String> defaultSchemaTableNames = new ArrayList<String>(
					defaultDbSupport.getTableNames());
			List<String> defaultSchemaViewNames = new ArrayList<String>(
					defaultDbSupport.getViewNames());
			defaultSchemaTableNames.addAll(defaultSchemaViewNames);
			Collections.sort(defaultSchemaTableNames);

			for (String tableName : defaultSchemaTableNames) {
				writer.write("\t\t\t\t<xsd:element name=\""
						+ tableName.toLowerCase() + "\" type=\"dflt:"
						+ tableName.toLowerCase() + complexTypeSuffix
						+ "\" />\n");
			}

			// FIX START allow any element from any DB schema namespace
			for (DbSupport dbSupport : dbSupports) {
				String schemaName = dbSupport.getSchemaName();
				writer.write("\t\t\t\t<xsd:any namespace=\"" + schemaName
						+ "\" />\n");
			}
			// FIX END

			writer.write("\t\t\t</xsd:choice>\n");
			writer.write("\t\t</xsd:complexType>\n");
			writer.write("\t</xsd:element>\n");
			writer.write("</xsd:schema>\n");

		} catch (Exception e) {
			throw new UnitilsException("Error generating xsd file: "
					+ xsdDirectory, e);
		} finally {
			closeQuietly(writer);
		}
	}

	@Override
	protected void generateDatabaseSchemaXsd(DbSupport dbSupport,
			File xsdDirectory) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(xsdDirectory,
					dbSupport.getSchemaName() + getProfileSuffix() + ".xsd")));

			writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			writer.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns=\""
					+ dbSupport.getSchemaName()
					+ "\" targetNamespace=\""
					+ dbSupport.getSchemaName() + "\"\n");
			writer.write("\txmlns:la=\"http://lightair.sourceforge.net/\">\n");
			writer.write("\t<xsd:import namespace=\"http://lightair.sourceforge.net/\" schemaLocation=\"light-air-types.xsd\" />\n");

			List<String> tableNames = new ArrayList<String>(
					dbSupport.getTableNames());
			tableNames.addAll(new ArrayList<String>(dbSupport.getViewNames()));
			Collections.sort(tableNames);

			for (String tableName : tableNames) {
				writer.write("\t<xsd:element name=\"" + tableName.toLowerCase()
						+ "\" type=\"" + tableName.toLowerCase()
						+ complexTypeSuffix + "\" />\n");
			}

			for (String tableName : tableNames) {
				writer.write("\t<xsd:complexType name=\""
						+ tableName.toLowerCase() + complexTypeSuffix + "\">\n");

				List<String> columnNames = new ArrayList<String>(
						dbSupport.getColumnNames(tableName));
				Collections.sort(columnNames);
				for (String columnName : columnNames) {
					writer.write("\t\t<xsd:attribute name=\""
							+ columnName.toLowerCase()
							+ "\" use=\"optional\" type=\"la:ColumnType\" />\n");
				}
				writer.write("\t</xsd:complexType>\n");
			}
			writer.write("</xsd:schema>\n");

		} catch (Exception e) {
			throw new UnitilsException("Error generating xsd file: "
					+ xsdDirectory, e);
		} finally {
			closeQuietly(writer);
		}
	}

	public String getComplexTypeSuffix() {
		return complexTypeSuffix;
	}

}
