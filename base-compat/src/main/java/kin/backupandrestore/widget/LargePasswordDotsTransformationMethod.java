package kin.backupandrestore.widget;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class LargePasswordDotsTransformationMethod extends PasswordTransformationMethod {

    private static LargePasswordDotsTransformationMethod instance;

    public static LargePasswordDotsTransformationMethod getInstance() {
        if (instance == null) {
            synchronized (LargePasswordDotsTransformationMethod.class) {
                if (instance == null) {
                    instance = new LargePasswordDotsTransformationMethod();
                }
            }
        }
        return instance;
    }

    private LargePasswordDotsTransformationMethod() {

    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {

        private CharSequence source;

        public PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

        public char charAt(int index) {
            return '●'; // large dot unicode char (https://unicode-table.com/en/2B24/)
        }

        public int length() {
            return source.length();
        }

        public CharSequence subSequence(int start, int end) {
            return source.subSequence(start, end);
        }
    }
}
