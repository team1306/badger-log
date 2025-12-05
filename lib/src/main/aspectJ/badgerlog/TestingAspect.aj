package badgerlog;

public aspect TestingAspect issingleton() {

    public static void Dashboard.displayMetrics() {
        System.out.println("Displaying metrics for: " + Dashboard.class.getSimpleName());
        System.out.println("Injected method executed successfully!");
    }

    /**
     * Add a field to track method invocations
     */
    private static int Dashboard.metricsCallCount = 0;

    /**
     * Add a getter for the call count
     */
    public static int Dashboard.getMetricsCallCount() {
        return metricsCallCount;
    }

    /**
     * Pointcut: intercept calls to the injected method
     */
    pointcut displayMetricsCall(Dashboard dashboard) :
            execution(void Dashboard.displayMetrics()) && this(dashboard);
    
    /**
     * Advice: increment counter before executing the injected method
     */
    before(Dashboard dashboard) : displayMetricsCall(dashboard) {
        Dashboard.metricsCallCount++;
    }

    /**
     * Add another utility method
     */
    public static String Dashboard.getStatus() {
        return "Dashboard is active";
    }
}
