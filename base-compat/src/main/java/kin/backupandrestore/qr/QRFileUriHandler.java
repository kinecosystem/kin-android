package kin.backupandrestore.qr;


import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;

interface QRFileUriHandler {

    @NonNull
    Bitmap loadFile(@NonNull Uri uri) throws IOException;

    @NonNull
    Uri saveFile(@NonNull Bitmap image) throws IOException;
}
