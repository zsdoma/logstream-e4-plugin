package hu.zsdoma.e4.logstream.service.table;

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

  public SplitterLogTableDescriptor(String delimiter, int limit, String[] columns) {
    super();
    this.delimiter = delimiter;
    this.columns = columns;
    this.limit = limit;
  }

  public String[] getColumns() {
    return columns;
  }

  @Override
  public TableItem createTableItem(Table table, LoggerLineDTO loggerLineDTO) {
    String message = loggerLineDTO.getMessage();
    String[] fragments = message.split(delimiter, limit);
    TableItem tableItem = new TableItem(table, SWT.NONE);
    for (int i = 0; i < fragments.length; i++) {
      tableItem.setText(i, fragments[i]);
    }
    return tableItem;
  }

}
