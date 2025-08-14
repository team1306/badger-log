package badgerlog;

public aspect TestAspect {

    before() : set (DashboardConfig *) {
        System.out.println("this is a test aspect");
    }
}
