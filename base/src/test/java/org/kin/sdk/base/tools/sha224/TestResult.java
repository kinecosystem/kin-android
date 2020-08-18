package org.kin.sdk.base.tools.sha224;

public interface TestResult
{
    public boolean isSuccessful();

    public Throwable getException();

    public String toString();
}
