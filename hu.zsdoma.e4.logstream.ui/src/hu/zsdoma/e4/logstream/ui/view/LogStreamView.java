package hu.zsdoma.e4.logstream.ui.view;

import hu.zsdoma.e4.logstream.service.LogStreamCallback;
import hu.zsdoma.e4.logstream.service.LogStreamService;
import hu.zsdoma.e4.logstream.service.LogStreamServiceImpl;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class LogStreamView extends ViewPart implements LogStreamCallback {
    private LogStreamService logStreamService;
    private Table table;

	public LogStreamView() {
	    logStreamService = new LogStreamServiceImpl("/tmp/test.txt");
	    logStreamService.registerCallback(this);
	    logStreamService.start();
	}

	@Override
	public void createPartControl(final Composite parent) {
	    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	}

    @Override
    public void setFocus() {
        System.out.println("setFocus");
    }

    @Override
    public void onStreamChanged(final List<String> pufferedLines) {
        table.getDisplay().syncExec(() -> {
            TableItem tableItem = null;
            for (String string : pufferedLines) {
                tableItem = new TableItem(table, SWT.NONE);
                tableItem.setText(string);
            }
            if (tableItem != null) {
                table.showItem(tableItem);
            }
        });
    }

    @Override
    public void dispose() {
        logStreamService.stop();
    }

}
