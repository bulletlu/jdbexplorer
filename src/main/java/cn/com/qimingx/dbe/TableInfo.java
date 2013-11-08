package cn.com.qimingx.dbe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.service.WorkDirectory;
import cn.com.qimingx.utils.ExtTypeInfo;
import cn.com.qimingx.utils.PDFUtils;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author inc062805
 * 
 * Table 信息描述对象
 */
public class TableInfo {
	// logger
	private static final Log log = LogFactory.getLog(TableDataInfo.class);

	// 表名称
	private String tableName;
	// 主键列名称
	private String pkColumnName;
	// 列信息
	private List<TableColumnInfo> columns;
	// 表数据信息
	private TableDataInfo data;
	// 是否只读
	private boolean readOnly = true;

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	//
	public TableInfo() {
	}

	//
	public TableInfo(String pkColumnName, List<TableColumnInfo> columns) {
		this.pkColumnName = pkColumnName;
		this.columns = columns;
	}

	// 生成指定格式的数据文件
	public ProcessResult<File> makeDataFile(String fileType, WorkDirectory wd) {
		ProcessResult<File> pr = new ProcessResult<File>();
		// 检查类型格式
		if (!isSupportType(fileType)) {
			pr.setMessage("不支持的格式类型：" + fileType);
			return pr;
		}

		// 取得临时文件
		File file = wd.newFile(fileType, ".tmp");
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8");
			BufferedWriter buffer = new BufferedWriter(writer);

			if (fileType.equalsIgnoreCase("CSV")) {
				// 生成文件内容..
				makeCSVContent(buffer);
			} else if (fileType.equalsIgnoreCase("HTML")) {
				// 生成HTML文件
				makeHTMLContent(buffer);
			} else if (fileType.equalsIgnoreCase("PDF")) {
				// 生成PDF格式的文件
				makePDFContent(stream);
			} else {
				// 生成sql语句文件
				makeSQLContent(buffer);
			}

			pr.setSuccess(true);
			pr.setData(file);
		} catch (IOException e) {
			pr.setMessage("写文件出错：" + e.getMessage());
			log.error(pr.getMessage());
		} finally {
			IOUtils.closeQuietly(stream);
		}

		// done
		return pr;
	}

	// 生成sql语句文件
	private void makeSQLContent(BufferedWriter writer) throws IOException {
		List<Map<String, Object>> rows = data.getRows();
		if (rows.size() < 1) {
			return;
		}

		// add table data header
		String insert = "";
		for (TableColumnInfo column : getColumns()) {
			String name = column.getName();
			if (insert.length() > 0) {
				insert += ",";
			}
			insert += name;
		}
		insert = "insert to " + getTableName() + "(" + insert + ") values(";

		for (Map<String, Object> map : rows) {
			String row = "";
			for (TableColumnInfo column : getColumns()) {
				Object value = map.get(column.getName());
				String col = "NULL";
				if (value != null) {
					col = value.toString();
					ExtTypeInfo type = column.getExtType();
					if (type.isDateType()) {
						col = "toDate('" + col + "' HH24:mm)";
					} else if (type.isBooleanType() && type.isNumberType()) {

					} else {
						col = "'" + col + "'";
					}
				}
				if (row.length() > 0) {
					row += ",";
				}
				row += col;
			}
			writer.write(insert + row + ")");
			writer.newLine();
		}

		writer.flush();
	}

	// 生成PDF格式的文件内容
	private void makePDFContent(OutputStream stream) throws IOException {
		List<Map<String, Object>> rows = data.getRows();
		if (rows.size() < 1) {
			return;
		}

		Document pdfDoc = new Document();
		try {
			PdfWriter.getInstance(pdfDoc, stream);

			// create table
			Map<String, Object> row = rows.get(0);
			Set<String> columns = row.keySet();
			Table table = new Table(columns.size());
			table.setWidth(90);
			table.setAutoFillEmptyCells(true);
			table.setPadding(3);
			table.setSpacing(0);
			table.setBorder(1);

			// add table data header
			for (TableColumnInfo column : getColumns()) {
				String name = column.getName();
				Phrase phe = new Phrase(name, PDFUtils.createChineseFont());
				Cell cell = new Cell(phe);
				cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
				cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
				table.addCell(new Cell(phe));
			}
			for (Map<String, Object> map : rows) {
				for (TableColumnInfo column : getColumns()) {
					Object value = map.get(column.getName());
					String col = "　";
					if (value != null) {
						col = value.toString();
					}
					Phrase phe = new Phrase(col, PDFUtils.createChineseFont());
					Cell cell = new Cell(phe);
					cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
					cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
					table.addCell(new Cell(phe));
				}
			}

			// add to pdf Document
			pdfDoc.open();
			pdfDoc.add(table);
		} catch (DocumentException e) {
			log.error("create PDFDocument error:" + e.getMessage());
			throw new IOException("create PDFDocument Error");
		} finally {
			if (pdfDoc.isOpen()) {
				pdfDoc.close();
			}
		}
	}

	// 生成html格式的文件内容
	private void makeHTMLContent(BufferedWriter writer) throws IOException {
		List<Map<String, Object>> rows = data.getRows();

		writer.write("<html><head>");
		writer.write("<meta http-equiv='Content-Type' "
				+ "content='text/html;charset=UTF-8'/>");
		writer.write("</head><body><table border='1' width='95%'>");

		String header = "";
		for (TableColumnInfo column : getColumns()) {
			String name = column.getName();
			header += "<td>" + name + "</td>";
		}
		writer.write("<tr>" + header + "</tr>");

		for (Map<String, Object> map : rows) {
			String row = "";
			for (TableColumnInfo column : getColumns()) {
				Object value = map.get(column.getName());
				String col = value == null ? "<NULL>" : value.toString();
				row += "<td>" + col + "</td>";
			}
			writer.write("<tr>" + row + "</tr>");
		}
		writer.write("</table></body></html>");
		writer.flush();
	}

	// 生成 分号 ； 分隔的文件内容
	private void makeCSVContent(BufferedWriter writer) throws IOException {
		List<Map<String, Object>> rows = data.getRows();
		for (Map<String, Object> map : rows) {
			String row = "";
			for (TableColumnInfo column : getColumns()) {
				row += map.get(column.getName()) + ";";
			}
			writer.write(row);
			writer.newLine();
		}
		writer.flush();
	}

	// 判断是否是受支持的类型
	private boolean isSupportType(String type) {
		type = type.toUpperCase();
		if (type.equals("CSV") || type.equals("HTML") || type.equals("PDF")
				|| type.equals("SQL")) {
			return true;
		}
		return false;
	}

	public String getPkColumnName() {
		return pkColumnName;
	}

	public void setPkColumnName(String pkColumnName) {
		this.pkColumnName = pkColumnName;
	}

	public List<TableColumnInfo> getColumns() {
		if (columns == null) {
			columns = new ArrayList<TableColumnInfo>();
		}
		return columns;
	}

	public void setColumns(List<TableColumnInfo> columns) {
		this.columns = columns;
	}

	public TableDataInfo getData() {
		return data;
	}

	public void setData(TableDataInfo data) {
		this.data = data;
	}
}
