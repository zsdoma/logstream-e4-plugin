package hu.zsdoma.e4.logstream.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class LogStreamView extends ViewPart {
	private Table table;

	public LogStreamView() {
	}

	@Override
	public void createPartControl(final Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

		TabItem firstTabItem = new TabItem(tabFolder, SWT.NONE);
		firstTabItem.setText("First Tab");

		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		firstTabItem.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] array = new String[] { "hello", "mikor", "csak", "yee" };
		for (int i = 0; i < array.length; i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(array[i]);
		}

		table.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TableItem) {
					TableItem selectedTableItem = (TableItem) e.item;
					System.out.println(selectedTableItem.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	@Override
	public void setFocus() {
		// table.getDisplay().asyncExec(() -> {
		// System.out.println("Focus");
		// });
	}

}
