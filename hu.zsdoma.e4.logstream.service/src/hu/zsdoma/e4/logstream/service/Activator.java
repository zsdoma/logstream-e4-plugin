package hu.zsdoma.e4.logstream.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.util.tracker.ServiceTracker;


public class Activator implements BundleActivator, ServiceListener {

	private ServiceTracker logStreamServiceTracker;
	private BundleContext fContext;
	private LogStreamService logStreamService;

	@Override
	public void start(BundleContext context) throws Exception {
		fContext = context;

		logStreamService = new LogStreamServiceImpl("/tmp/test.log");
		logStreamService.start();

		context.registerService(LogStreamService.class.getName(),
				logStreamService, null);

		logStreamServiceTracker = new ServiceTracker(context,
				LogStreamService.class.getName(), null);
		logStreamServiceTracker.open();

		fContext.addServiceListener(
				this,
				String.format("(objectclass=%s)",
						LogStreamService.class.getName()));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		logStreamServiceTracker.close();
		logStreamServiceTracker = null;
		logStreamService.stop();
		logStreamService = null;
		fContext = null;
	}

	@Override
	public void serviceChanged(ServiceEvent ev) {
		System.out.println(ev.getType());
		// ServiceReference sr = ev.getServiceReference();
		// switch (ev.getType()) {
		// case ServiceEvent.REGISTERED: {
		// // Dictionary dictionary = (Dictionary) fContext.getService(sr);
		// // service.registerDictionary(dictionary);
		// LogStreamCallback logStreamCallback = (LogStreamCallback) fContext
		// .getService(sr);
		// logStreamService.registerCallback(logStreamCallback);
		// }
		// break;
		// case ServiceEvent.UNREGISTERING: {
		// LogStreamCallback logStreamCallback = (LogStreamCallback) fContext
		// .getService(sr);
		// logStreamService.unregisterCallback(logStreamCallback);
		// }
		// break;
		// }
	}

}
