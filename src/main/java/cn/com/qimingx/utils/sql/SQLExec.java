package cn.com.qimingx.utils.sql;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Unmi Executes a series of SQL statements or Sql File on a database
 *         using JDBC.
 */
public class SQLExec extends JDBCTask {
	
	private static final Log log = LogFactory
	.getLog(SQLExec.class);

//	private static Logger log = Logger.getLogger(SQLExec.class.getName());
//	static {
//		log.setLevel(Level.WARNING);
//	}

	// ���ָ�����
	public static final String DELIMITER_TYPE_NORMAL = "normal";
	public static final String DELIMITER_TYPE_ROW = "row";

	// ���ʱ������δ���
	public static final String ON_ERROR_ABORT = "abort";
	public static final String ON_ERROR_CONTINUE = "continue";
	public static final String ON_ERROR_STOP = "stop";

	private int goodSql = 0;

	private int totalSql = 0;

	/**
	 * Database connection
	 */
	private Connection conn = null;

	/**
	 * files to load
	 */
	private List<File> resources = new ArrayList<File>();

	/**
	 * SQL statement
	 */
	private Statement statement = null;

	/**
	 * SQL input file
	 */
	private File srcFile = null;

	/**
	 * SQL input command
	 */
	private String sqlCommand = "";

	/**
	 * SQL transactions to perform
	 */
	private Vector<Transaction> transactions = new Vector<Transaction>();

	/**
	 * SQL Statement delimiter
	 */
	private String delimiter = ";";

	/**
	 * The delimiter type indicating whether the delimiter will only be
	 * recognized on a line by itself
	 */
	private String delimiterType = DELIMITER_TYPE_NORMAL;

	/**
	 * Print SQL results.
	 */
	private boolean print = false;

	/**
	 * Print header columns.
	 */
	private boolean showheaders = true;

	/**
	 * Print SQL stats (rows affected)
	 */
	private boolean showtrailers = true;

	/**
	 * Results Output file.
	 */
	private File output = null;

	/**
	 * Action to perform if an error is found
	 */
	private String onError = "abort";

	/**
	 * Encoding to use when reading SQL statements from a file
	 */
	private String encoding = null;

	/**
	 * Append to an existing file or overwrite it?
	 */
	private boolean append = false;

	/**
	 * Keep the format of a sql block?
	 */
	private boolean keepformat = false;

	/**
	 * Argument to Statement.setEscapeProcessing
	 */
	private boolean escapeProcessing = true;

	/**
	 * should properties be expanded in text? false for backwards compatibility
	 */
	private boolean expandProperties = false;

	/**
	 * Set the name of the SQL file to be run. Required unless statements are
	 * enclosed in the build file
	 * 
	 * @param srcFile
	 *            the file containing the SQL command.
	 */
	public void setSrc(File srcFile) {
		this.srcFile = srcFile;
	}

	
	public String getResult(){
		return  String.valueOf(this.goodSql)+"  OF  "+String.valueOf(this.totalSql)+"条记录被成功执行";
	}
	
	/**
	 * Set an inline SQL command to execute. NB: Properties are not expanded in
	 * this text unless {@link #expandProperties} is set.
	 * 
	 * @param sql
	 *            an inline string containing the SQL command.
	 */
	public void addText(String sql) {
		// there is no need to expand properties here as that happens when
		// Transaction.addText is
		// called; to do so here would be an error.
		this.sqlCommand += sql;
	}

	/**
	 * Adds a collection of resources (nested element).
	 * 
	 * @param rc
	 *            a collection of resources containing SQL commands, each
	 *            resource is run in a separate transaction.
	 */
	public void add(File srcFile) {
		resources.add(srcFile);
	}

	/**
	 * Add a SQL transaction to execute
	 * 
	 * @return a Transaction to be configured.
	 */
	public Transaction createTransaction() {
		Transaction t = new Transaction();
		transactions.addElement(t);
		return t;
	}

	/**
	 * Set the file encoding to use on the SQL files read in
	 * 
	 * @param encoding
	 *            the encoding to use on the files
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Set the delimiter that separates SQL statements. Defaults to
	 * &quot;;&quot;; optional
	 * 
	 * <p>
	 * For example, set this to "go" and delimitertype to "ROW" for Sybase ASE
	 * or MS SQL Server.
	 * </p>
	 * 
	 * @param delimiter
	 *            the separator.
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Set the delimiter type: "normal" or "row" (default "normal").
	 * 
	 * <p>
	 * The delimiter type takes two values - normal and row. Normal means that
	 * any occurrence of the delimiter terminate the SQL command whereas with
	 * row, only a line containing just the delimiter is recognized as the end
	 * of the command.
	 * </p>
	 * 
	 * @param delimiterType
	 *            the type of delimiter - "normal" or "row".
	 */
	public void setDelimiterType(String delimiterType) {
		this.delimiterType = delimiterType;
	}

	/**
	 * Print result sets from the statements; optional, default false
	 * 
	 * @param print
	 *            if true print result sets.
	 */
	public void setPrint(boolean print) {
		this.print = print;
	}

	/**
	 * Print headers for result sets from the statements; optional, default
	 * true.
	 * 
	 * @param showheaders
	 *            if true print headers of result sets.
	 */
	public void setShowheaders(boolean showheaders) {
		this.showheaders = showheaders;
	}

	/**
	 * Print trailing info (rows affected) for the SQL Addresses Bug/Request
	 * #27446
	 * 
	 * @param showtrailers
	 *            if true prints the SQL rows affected
	 */
	public void setShowtrailers(boolean showtrailers) {
		this.showtrailers = showtrailers;
	}

	/**
	 * Set the output file; optional, defaults to the System.out log.
	 * 
	 * @param output
	 *            the output file to use for logging messages.
	 */
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * whether output should be appended to or overwrite an existing file.
	 * Defaults to false.
	 * 
	 * @param append
	 *            if true append to an existing file.
	 */
	public void setAppend(boolean append) {
		this.append = append;
	}

	/**
	 * Action to perform when statement fails: continue, stop, or abort
	 * optional; default &quot;abort&quot;
	 * 
	 * @param action
	 *            the action to perform on statement failure.
	 */
	public void setOnerror(String onError) {
		this.onError = onError;
	}

	/**
	 * whether or not format should be preserved. Defaults to false.
	 * 
	 * @param keepformat
	 *            The keepformat to set
	 */
	public void setKeepformat(boolean keepformat) {
		this.keepformat = keepformat;
	}

	/**
	 * Set escape processing for statements.
	 * 
	 * @param enable
	 *            if true enable escape processing, default is true.
	 */
	public void setEscapeProcessing(boolean enable) {
		escapeProcessing = enable;
	}

	/**
	 * Load the sql file and then execute it
	 * 
	 * @throws BuildException
	 *             on error.
	 */
	@SuppressWarnings("unchecked")
	public void execute(Connection cnn, boolean isclose) throws Exception {
		Vector<Transaction> savedTransaction = (Vector) transactions.clone();
		String savedSqlCommand = sqlCommand;

		sqlCommand = sqlCommand.trim();

		try {
			if (srcFile == null && sqlCommand.length() == 0
					&& resources.size() == 0) {
				if (transactions.size() == 0) {
					throw new Exception("Source file or resource "
							+ "collection, " + "transactions or sql statement "
							+ "must be set!");
				}
			}

			if (srcFile != null && !srcFile.exists()) {
				throw new Exception("Source file does not exist!");
			}

			// deal with the resources
			Iterator iter = resources.iterator();
			while (iter.hasNext()) {
				File file = (File) iter.next();
				// Make a transaction for each resource
				Transaction t = createTransaction();
				t.setSrcResource(file);
			}

			// Make a transaction group for the outer command
			Transaction t = createTransaction();
			t.setSrc(srcFile);
			t.addText(sqlCommand);
			conn = cnn;
			try {
				statement = conn.createStatement();
				statement.setEscapeProcessing(escapeProcessing);

				PrintStream out = System.out;
				try {
					if (output != null) {
						log
								.info("Opening PrintStream to output file "
										+ output);
						out = new PrintStream(new BufferedOutputStream(
								new FileOutputStream(output.getAbsolutePath(),
										append)));
					}

					// Process all transactions
					for (Enumeration e = transactions.elements(); e
							.hasMoreElements();) {

						((Transaction) e.nextElement()).runTransaction(out);
						if (!isAutocommit()) {
							log.info("Committing transaction");
							conn.commit();
						}
					}
				} finally {
					if (out != null && out != System.out) {
						out.close();
					}
				}
			} catch (IOException e) {
				closeQuietly();
				throw new Exception(e);
			} catch (SQLException e) {
				closeQuietly();
				throw new Exception(e);
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null && isclose) {
						conn.close();
					}
				} catch (SQLException ex) {
					// ignore
				}
			}
			log.info(goodSql + " of " + totalSql
					+ " SQL statements executed successfully");
		} finally {
			transactions = savedTransaction;
			sqlCommand = savedSqlCommand;
		}
	}

	/**
	 * read in lines and execute them
	 * 
	 * @param reader
	 *            the reader contains sql lines.
	 * @param out
	 *            the place to output results.
	 * @throws SQLException
	 *             on sql problems
	 * @throws IOException
	 *             on io problems
	 */
	protected void runStatements(Reader reader, PrintStream out)
			throws SQLException, IOException {
		StringBuffer sql = new StringBuffer();
		String line;

		BufferedReader in = new BufferedReader(reader);

		// 多行注释结束标志 true 结束 false 没结束
		boolean isend = true;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			if (!keepformat) {
				line = line.trim();
			}

			// 处理注释
			if (!keepformat) {

				// 过滤掉字符串 line中 /**/的所有内容
				String regEx = "/\\*[\\w\\s\\*@@-]*\\*/";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(line);
				line = m.replaceAll("");
				//处理sql语句中的go关键字
				if(line.startsWith("GO")||line.startsWith("go")){
					line = "; " +line.substring(2);
				}

				// 处理以--开头的单行注释
				if (line.startsWith("--")) {
					line = "";
					continue;
				} else {
					// 处理多行注释
					regEx = "\\*/[\\w\\s\\*@@-]*/\\*";
					int begin = line.indexOf("*/");
					int end = line.indexOf("/*");
					p = Pattern.compile(regEx);
					m = p.matcher(line);
					if (m.find()) {
						line = line.substring(begin + 2, end);
						isend = false;
						continue;
					} else if (line.indexOf("*/") > -1) {
						int index = line.indexOf("*/");
						line.substring(index + 1);
						isend = true;
						continue;
					} else if (line.indexOf("/*") > -1) {
						int index = line.indexOf("/*");
						line.substring(0, index);
						isend = false;
						continue;
					} else if (!isend) {
						line = "";
					}
					// 处理 /* 之后的单行注释
					if (line.indexOf("--") > -1) {
						int index = line.indexOf("--");
						line = line.substring(0, index - 1);
					}
				}
				line = line.trim();

				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					String token = st.nextToken();
					if ("REM".equalsIgnoreCase(token)) {
						continue;
					}
				}
			}

			if (!keepformat) {
				sql.append(" ");
				sql.append(line);
			} else {
				sql.append("\n");
				sql.append(line);
			}

			// SQL defines "--" as a comment to EOL
			// and in Oracle it may contain a hint
			// so we cannot just remove it, instead we must end it
			if (!keepformat) {
				if (line.indexOf("--") >= 0) {
					sql.append("\n");
				}
			}
			if ((delimiterType.equals(DELIMITER_TYPE_NORMAL)&& sql.toString().endsWith(delimiter) && line.length() > 0)
					|| (delimiterType.equals(DELIMITER_TYPE_ROW) && line
							.equals(delimiter))) {
				execSQL(sql.substring(0, sql.length() - delimiter.length()),
						out);
				sql.replace(0, sql.length(), "");
			}

		}
		// Catch any statements not followed by ;
		if (sql.length() > 0) {
			log.debug("SQLExec.runStatements.sql"+sql);
			execSQL(sql.toString(), out);
		}
	}

	/**
	 * Exec the sql statement.
	 * 
	 * @param sql
	 *            the SQL statement to execute
	 * @param out
	 *            the place to put output
	 * @throws SQLException
	 *             on SQL problems
	 */
	protected void execSQL(String sql, PrintStream out) throws SQLException {
		// Check and ignore empty statements
		if ("".equals(sql.trim())) {
			return;
		}

		ResultSet resultSet = null;
		try {
			totalSql++;
			log.info("SQL: " + sql);

			boolean ret;
			int updateCount = 0, updateCountTotal = 0;

			ret = statement.execute(sql);
			updateCount = statement.getUpdateCount();
			log.debug("@@@@@@@@@@@UpdateCount"+updateCount);
			resultSet = statement.getResultSet();
			do {
				if (!ret) {
					if (updateCount != -1) {
						updateCountTotal += updateCount;
					}
				} else {
					if (print) {
						printResults(resultSet, out);
					}
				}
				ret = statement.getMoreResults();
				if (ret) {
					updateCount = statement.getUpdateCount();
					resultSet = statement.getResultSet();
				}
			} while (ret);

			log.info(updateCountTotal + " rows affected");

			if (print && showtrailers) {
				out.println(updateCountTotal + " rows affected");
			}

			SQLWarning warning = conn.getWarnings();
			while (warning != null) {
				log.debug(" sql warning " + warning.getLocalizedMessage());
				warning = warning.getNextWarning();
			}
			conn.clearWarnings();
			goodSql++;
		} catch (SQLException e) {
			log.debug("Failed to execute: " + sql);
			if (!onError.equals("continue")) {
				throw e;
			}
			log.debug(e.toString());
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	/**
	 * print any results in the statement
	 * 
	 * @deprecated since 1.6.x. Use
	 *             {@link #printResults(java.sql.ResultSet, java.io.PrintStream)
	 *             the two arg version} instead.
	 * @param out
	 *            the place to print results
	 * @throws SQLException
	 *             on SQL problems.
	 */
	protected void printResults(PrintStream out) throws SQLException {
		ResultSet rs = statement.getResultSet();
		try {
			printResults(rs, out);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	/**
	 * print any results in the result set.
	 * 
	 * @param rs
	 *            the resultset to print information about
	 * @param out
	 *            the place to print results
	 * @throws SQLException
	 *             on SQL problems.
	 */
	protected void printResults(ResultSet rs, PrintStream out)
			throws SQLException {
		if (rs != null) {
			log.info("Processing new result set.");
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			StringBuffer line = new StringBuffer();
			if (showheaders) {
				for (int col = 1; col < columnCount; col++) {
					line.append(md.getColumnName(col));
					line.append(",");
				}
				line.append(md.getColumnName(columnCount));
				out.println(line);
				line = new StringBuffer();
			}
			while (rs.next()) {
				boolean first = true;
				for (int col = 1; col <= columnCount; col++) {
					String columnValue = rs.getString(col);
					if (columnValue != null) {
						columnValue = columnValue.trim();
					}

					if (first) {
						first = false;
					} else {
						line.append(",");
					}
					line.append(columnValue);
				}
				out.println(line);
				line = new StringBuffer();
			}
		}
		out.println();
	}

	/*
	 * Closes an unused connection after an error and doesn't rethrow a possible
	 * SQLException
	 */
	private void closeQuietly() {
		if (!isAutocommit() && conn != null && onError.equals("abort")) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				// ignore
			}
		}
	}

	/**
	 * Contains the definition of a new transaction element. Transactions allow
	 * several files or blocks of statements to be executed using the same JDBC
	 * connection and commit operation in between.
	 */
	public class Transaction {
		private File tSrcResource = null;
		private String tSqlCommand = "";

		/**
		 * Set the source file attribute.
		 * 
		 * @param src
		 *            the source file
		 */
		public void setSrc(File src) {
			// there are places (in this file, and perhaps elsewhere, where it
			// is assumed
			// that null is an acceptable parameter.
			if (src != null) {
				setSrcResource(src);
			}
		}

		/**
		 * Set the source resource attribute.
		 * 
		 * @param src
		 *            the source file
		 */
		public void setSrcResource(File src) {
			if (tSrcResource != null) {
				throw new IllegalArgumentException(
						"only one resource per transaction");
			}
			tSrcResource = src;
		}

		/**
		 * Set inline text
		 * 
		 * @param sql
		 *            the inline text
		 */
		public void addText(String sql) {
			if (sql != null) {
				this.tSqlCommand += sql;
			}
		}

		/**
		 * Set the source resource.
		 * 
		 * @param a
		 *            the source resource collection.
		 */
		public void addConfigured(Collection<File> files) {
			if (files.size() != 1) {
				throw new IllegalArgumentException(
						"only single argument resource "
								+ "collections are supported.");
			}
			setSrcResource((File) files.iterator().next());
		}

		/**
		 * 
		 */
		private void runTransaction(PrintStream out) throws IOException,
				SQLException {
			System.out.println("tSqlCommand.length():" + tSqlCommand.length());
			if (tSqlCommand.length() != 0) {
				log.info("Executing commands");
				runStatements(new StringReader(tSqlCommand), out);
			}

			if (tSrcResource != null) {
				log.info("Executing resource: " + tSrcResource.toString());
				InputStream is = null;
				Reader reader = null;
				try {
					is = new FileInputStream((File) tSrcResource);
					reader = (encoding == null) ? new InputStreamReader(is)
							: new InputStreamReader(is, encoding);
					runStatements(reader, out);
				} finally {
					is.close();
					reader.close();
				}
			}
		}
	}
}
