package hu.zsdoma.e4.logstream.service.table;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import hu.zsdoma.e4.logstream.service.LoggerLineDTO;

public interface LogTableDescriptor {
  TableItem createTableItem(Table table, LoggerLineDTO loggerLineDTO);

  String[] getColumns();
}
