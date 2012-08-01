package commons.callbacks.files;

import commons.callbacks.metadata.GlobalCallback;
import commons.callbacks.metadata.ObjectCallback;

public class TestGlobalCallbacksCaller {

    private static final TestGlobalCallbacksCaller instance = new TestGlobalCallbacksCaller();

    public static TestGlobalCallbacksCaller getInstance() {
        return instance;
    }

    @GlobalCallback(AbstractCallback.class)
    public static String sayStaticHello(String str) {
        return str;
    }

    @ObjectCallback(AbstractCallback.class)
    @GlobalCallback(AbstractCallback.class)
    public String sayHello(String str) {
        return str;
    }
}
