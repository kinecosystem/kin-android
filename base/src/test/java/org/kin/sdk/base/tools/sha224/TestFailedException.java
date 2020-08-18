package org.kin.sdk.base.tools.sha224;


public class TestFailedException
        extends RuntimeException {
    private TestResult _result;

    public TestFailedException(
            TestResult result) {
        _result = result;
    }

    public TestResult getResult() {
        return _result;
    }
}
