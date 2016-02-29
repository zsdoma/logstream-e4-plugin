package hu.zsdoma.e4.logstream.service.table;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import hu.zsdoma.e4.logstream.service.LoggerLineDTO;

//String regexp = "(\\|)(.*?)(\\|)(.*?)(\\|)(.*?)(\\|)(.*?)(.*)";
//String message = "WARNING|wrapper|racaof-production-main|15-05-18 13:25:34|YAJSW:yajsw-stable-11.07";

public class SplitterLogTableDescriptor implements LogTableDescriptor {
  private String delimiter;
  private String[] columns;
  private int limit;
  private String firstLineMask;

  public SplitterLogTableDescriptor(String delimiter, int limit) {
    super();
    Objects.requireNonNull("'delimiter' must be not null", delimiter);
    this.delimiter = delimiter;
    this.limit = limit;
    this.firstLineMask = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.*";
  }

  public SplitterLogTableDescriptor columns(final String... columns) {
    this.columns = columns;
    return this;
  }

  public SplitterLogTableDescriptor firstLineMask(final String firstLineMask) {
    this.firstLineMask = firstLineMask;
    return this;
  }

  public String[] getColumns() {
    return columns;
  }

  @Override
  public TableItem createTableItem(Table table, LoggerLineDTO loggerLineDTO) {
    TableItem tableItem = new TableItem(table, SWT.NONE);
    if (isFirstLine(loggerLineDTO.message)) {
      String message = loggerLineDTO.message;
      String[] fragments = message.split(delimiter, limit);
      for (int i = 0; i < fragments.length; i++) {
        tableItem.setText(i, fragments[i]);
      }
    } else {
      String message = loggerLineDTO.message;
      tableItem.setText(limit - 1, message);
    }
    return tableItem;
  }

  private boolean isFirstLine(String message) {
    if (firstLineMask == null) {
      return true;
    } else {
      return message.matches(firstLineMask);
    }
  }

}
