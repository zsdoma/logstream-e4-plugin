package hu.zsdoma.e4.logstream.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import hu.zsdoma.e4.logstream.service.Constants;
import hu.zsdoma.e4.logstream.service.LogStreamCallback;
import hu.zsdoma.e4.logstream.service.LogStreamService;
import hu.zsdoma.e4.logstream.service.LogStreamServiceImpl;
import hu.zsdoma.e4.logstream.service.LoggerLineDTO;

public class LogStreamView extends ViewPart implements LogStreamCallback {
  private static final int COLUMN_INDEX_MESSAGE = 1;

  private static final int COLUMN_INDEX_TIME = 0;

  private LogStreamService logStreamService;

  private Table table;

  private IAction clearAction;

  private IAction openFileAction;

  private IAction closeAction;

  private static final String[] tableTitles = new String[] { "Time", "Message" };

  private SimpleDateFormat simpleDateFormat;

  private Composite defaultCompositeView;

  private Composite parent;

  public LogStreamView() {
    simpleDateFormat = new SimpleDateFormat();
  }

  private void createClearMenu() {
    clearAction = new Action() {
      @Override
      public void run() {
        clearTable();
      }
    };

    clearAction.setText("Clear");
    clearAction.setToolTipText("Clear console log");
    clearAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
  }

  private void createMenu(final IMenuManager menuManager) {
    menuManager.add(clearAction);
    menuManager.add(new Separator());
    menuManager.add(openFileAction);
  }

  private void createOpenFileAction() {
    openFileAction = new Action() {
      @Override
      public void run() {
        showFileChooser();
      }
    };
    openFileAction.setText("Open file");
    openFileAction.setToolTipText("Open file for follow as log");
    openFileAction.setImageDescriptor(
        PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
  }

  private void createCloseAction() {
    closeAction = new Action() {
      @Override
      public void run() {
        fileFollowingStop();
      }
    };
    closeAction.setText("Close");
    closeAction.setToolTipText("Stop following and close file.");
    closeAction.setImageDescriptor(
        PlatformUI.getWorkbench().getSharedImages()
            .getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.parent = parent;
    createDefaultView();
    createActions();
    createActionBar();
  }

  private void createTableView() {
    if (!parent.isDisposed()) {
      createTable();
    }
  }

  private void createDefaultView() {
    if (!parent.isDisposed() && !hasDefaultCompositeComponent()) {
      defaultCompositeView = new Composite(parent, SWT.NONE);
      defaultCompositeView.setLayout(new FillLayout());
      Label titleLabel = new Label(defaultCompositeView, SWT.CENTER);
      titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      titleLabel.setText("No file opened");
      titleLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
    }
  }

  private void createTable() {
    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    for (String title : tableTitles) {
      addTableColumn(title);
    }

    for (int i = 0; i < tableTitles.length; i++) {
      table.getColumn(i).pack();
    }
  }

  private void createActionBar() {
    IActionBars bars = getViewSite().getActionBars();
    IToolBarManager toolBarManager = bars.getToolBarManager();
    createToolBar(toolBarManager);
    // IMenuManager menuManager = bars.getMenuManager();
    // createMenu(menuManager);
  }

  private void createActions() {
    createCloseAction();
    createOpenFileAction();
    createCloseAction();
    createClearMenu();
  }

  private void addTableColumn(final String title) {
    TableColumn column = new TableColumn(table, SWT.NONE);
    column.setText(title);
  }

  private void createToolBar(final IToolBarManager toolBarManager) {
    toolBarManager.add(openFileAction);
    toolBarManager.add(clearAction);
    toolBarManager.add(closeAction);
  }

  @Override
  public void dispose() {
    fileFollowingStop();
  }

  @Override
  public void onStreamChanged(final List<LoggerLineDTO> pufferedLines) {
    if (hasTable()) {
      table.getDisplay().syncExec(() -> {
        TableItem tableItem = null;
        for (LoggerLineDTO loggerLineDTO : pufferedLines) {
          tableItem = createRow(loggerLineDTO);
        }
        if (tableItem != null) {
          table.showItem(tableItem);
        }
      } );
    }
  }

  private TableItem createRow(LoggerLineDTO loggerLineDTO) {
    TableItem tableItem;
    tableItem = new TableItem(table, SWT.NONE);
    System.out.println(loggerLineDTO.getMessage());
    tableItem.setText(COLUMN_INDEX_TIME,
        simpleDateFormat.format(new Date(loggerLineDTO.getTimestamp())));
    tableItem.setText(COLUMN_INDEX_MESSAGE, loggerLineDTO.getMessage());
    return tableItem;
  }

  @Override
  public void setFocus() {
    System.out.println("setFocus");
  }

  private void showFileChooser() {
    Display currentDisplay = Display.getCurrent();
    Shell shell = new Shell(currentDisplay);
    FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
    fileDialog.setFilterExtensions(Constants.LOG_FILE_MASKS);
    fileDialog.setFilterPath("/tmp/");
    String result = fileDialog.open();
    System.out.println("Selected file: " + result);
    switchFollowedFile(result);
  }

  private void switchFollowedFile(final String filePath) {
    if ((filePath == null) || filePath.isEmpty()) {
      return;
    }
    fileFollowingStop();
    fileFollowingStart(filePath);
  }

  private void fileFollowingStart(final String filePath) {
    clearDefaultView();
    createTableView();

    logStreamService = new LogStreamServiceImpl(filePath);
    logStreamService.registerCallback(this);
    logStreamService.start();
    setPartName(createTitleName(filePath));
  }

  private void clearDefaultView() {
    if (hasDefaultCompositeComponent()) {
      defaultCompositeView.dispose();
    }
  }

  private String createTitleName(String filePath) {
    int length = filePath.length();
    if (length > Constants.MAX_TITLE_LENGTH) {
      return filePath.substring(length - Constants.MAX_TITLE_LENGTH);
    } else {
      return filePath;
    }
  }

  private void fileFollowingStop() {
    clearTable();
    createDefaultView();
    if (logStreamService != null) {
      logStreamService.stop();
      logStreamService.unregisterCallback(this);
    }
    setPartName(Constants.DEFAULT_TITLE);
  }

  private void clearTable() {
    if (hasTable()) {
      table.getDisplay().syncExec(() -> {
        table.removeAll();
      } );
      table.dispose();
    }
  }

  private boolean hasTable() {
    return table != null && !table.isDisposed();
  }
  
  private boolean hasDefaultCompositeComponent() {
    return defaultCompositeView != null && !defaultCompositeView.isDisposed();
  }

}
