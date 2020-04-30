package kin.backupandrestore.qr;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRFileUriHandlerImpl implements QRFileUriHandler {

    private static final String RELATIVE_PATH_FILENAME_QR_IMAGE = "/kinrecovery_qr_codes/backup_qr.png";
    private static final String AUTHORITY_FORMAT = "%s.KinRecoveryFileProvider";
    private final String AUTHORITY;
    private final Context context;

    public QRFileUriHandlerImpl(@NonNull Context context) {
        this.context = context;
        this.AUTHORITY = String.format(AUTHORITY_FORMAT, context.getPackageName());
    }

    @NonNull
    @Override
    public Bitmap loadFile(@NonNull Uri uri) throws IOException {
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
            if (bitmap != null) {
                return bitmap;
            }
        }
        throw new IOException("decoding file as bitmap failed.");
    }

    @NonNull
    @Override
    public Uri saveFile(@NonNull Bitmap bitmap) throws IOException {
        File file = getOrCreateSaveFile();
        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        stream.close();
        return FileProvider.getUriForFile(
                context,
                AUTHORITY,
                file);
    }

    @NonNull
    private File getOrCreateSaveFile() throws IOException {
        String filepath = context.getFilesDir().getAbsolutePath() + RELATIVE_PATH_FILENAME_QR_IMAGE;
        File file = new File(filepath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                boolean dirCreated = file.getParentFile().mkdir();
                if (!dirCreated) {
                    throw new IOException("Cannot create folder at target location.");
                }
            }
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Cannot create file at target location.");
            }
        }
        return file;
    }

}
