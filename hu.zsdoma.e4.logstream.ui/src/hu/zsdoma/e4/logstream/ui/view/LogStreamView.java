package hu.zsdoma.e4.logstream.ui.view;

import hu.zsdoma.e4.logstream.service.LogStreamCallback;
import hu.zsdoma.e4.logstream.service.LogStreamService;
import hu.zsdoma.e4.logstream.service.LogStreamServiceImpl;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class LogStreamView extends ViewPart implements LogStreamCallback {
    private final String LOG_FILE = "/home/zsoltdoma/svn/cams-raca/offline/trunk/production/core/target/eosgi-dist/racaof-production-main/log/wrapper.log";

    private LogStreamService logStreamService;
    private Table table;
    private IAction clearAction;
    private IAction openFileAction;

	public LogStreamView() {
	    logStreamService = new LogStreamServiceImpl(LOG_FILE);
	    logStreamService.registerCallback(this);
	    logStreamService.start();
	}

	@Override
	public void createPartControl(final Composite parent) {
	    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    clearAction = createClearMenu();
	    openFileAction = createOpenFileAction();

	    IActionBars bars = getViewSite().getActionBars();

	    IToolBarManager toolBarManager = bars.getToolBarManager();
	    createToolBar(toolBarManager);

	    IMenuManager menuManager = bars.getMenuManager();
	    createMenu(menuManager);
	}

	private IAction createOpenFileAction() {
	    IAction openFileAction = new Action() {
	        @Override
	        public void run() {
	            System.out.println("open file action");
	        }
        };
        openFileAction.setText("Open file");
        openFileAction.setToolTipText("Open file for follow as log");
        openFileAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
            getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

        return openFileAction;
    }

    public void createMenu(final IMenuManager menuManager) {
	    menuManager.add(clearAction);
	    menuManager.add(new Separator());
	    menuManager.add(openFileAction);
	}

    public void createToolBar(final IToolBarManager toolBarManager) {
        toolBarManager.add(clearAction);
    }

    public IAction createClearMenu() {
        IAction action1 = new Action() {
            @Override
            public void run() {
                table.getDisplay().syncExec(() -> {
                    table.clearAll();
                });
            }
        };;
        action1.setText("Clear");
        action1.setToolTipText("Clear console log");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
            getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
        return action1;
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
