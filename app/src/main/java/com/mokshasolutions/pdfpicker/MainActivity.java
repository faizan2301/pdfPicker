package com.mokshasolutions.pdfpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button pickPdfButton;
    private TextView selectedPdfTextView;
    private static final int PICK_PDF_FILE = 2;
    private static String TAG = "PDF DATA";
    Context context;
    public File imageFile, source, destination;
    Uri imageUri;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    FileUtils fileUtils;
    File storageDir=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        pickPdfButton = findViewById(R.id.pick_pdf_button);
        fileUtils=new FileUtils(context);

        selectedPdfTextView = findViewById(R.id.selected_pdf_text_view);
        FileUtils fileUtils = new FileUtils(MainActivity.this);

        pickPdfButton.setOnClickListener(v -> {
            openPdfPicker();
//            mGetContent.launch("application/pdf");
        });
//        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    try {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            if (data != null) {
//                                Uri pdfUri = data.getData();
////                                String path = getPath(pdfUri);
////                                Log.d("PDF DATA", path + " getPath");
//                                File file = new File(pdfUri.getPath());//create path from uri
//                                final String[] split = file.getPath().split(":");//split the path.
//                                String filePath = null;
//                                File file1 = null;
//
////                                file1 = new File(getPath(pdfUri));
//                                filePath = fileUtils.getPath(pdfUri);
//                                file1 = new File(filePath);
//
//                                Log.d("PDF DATA", "filePath " + filePath + " abs " + file1.getAbsolutePath());
//
//
//                                filePath = split[1];//assign it to a string(your choice).
//                                Log.d("PDF DATA", "Path " + pdfUri.getPath());
//                                Log.d("PDF DATA", "filePath2 " + filePath);
//                                String pdfName = getFileName(pdfUri);
//                                selectedPdfTextView.setText(pdfName);
//                                // Do something with the selected PDF file
//                            }
//
//                        } else {
//                            Toast.makeText(MainActivity.this, "No PDF selected", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("PDF DATA Exception", e.getMessage() + "");
//                    }
//                });
    }


    private void openPdfPicker() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
//            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//            imageFile = File.createTempFile(imageFileName, ".pdf", storageDir);
//            imageUri = FileProvider.getUriForFile(this, "com.mokshasolutions.pdfpicker.fileprovider", imageFile);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf").addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        pdfPickerLauncher.launch(intent);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICK_PDF_FILE);
//            startActivityForResult(intent, PICK_PDF_FILE);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        Log.d("PDF DATA", "Path2 " + result);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                try {
                    uri = data.getData();
                    String fileName = getFileName(uri);
                    String filePath = getFilePath(uri);
                    Log.d(TAG, "onActivityResult: fileName "+fileName);
                    Log.d(TAG, "onActivityResult: filePath "+filePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor=null;
        try {
            String[] projection = {MediaStore.Files.FileColumns.DATA};
             cursor = managedQuery(uri, projection, null, null, null);
            startManagingCursor(cursor);
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            cursor.moveToFirst();
            String path=cursor.getString(column_index);
            Log.d(TAG, "getPath: "+path);
            return path;
        } catch (Exception e) {
            Log.e("PDF DATA GET EXCEPTION", "" + e.getMessage());
            return "";
        }finally {
            assert cursor != null;
            cursor.close();
        }
    }

    public void dumpImageMetaData(Uri uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
//        String[] projection = {MediaStore.Files.FileColumns.RELATIVE_PATH};
        Cursor cursor;
        final String column = "_data";
        final String[] projection = {
                column
        };
        cursor = context.getContentResolver()
                .query(uri, projection, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null) {
                cursor.moveToFirst();
                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
//                @SuppressLint("Range") String displayName = cursor.getString(
//                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                final int index = cursor.getColumnIndexOrThrow(column);

                String path = cursor.getString(index);
                Log.d(TAG, "dumpImageMetaData: " + path);
//                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
//            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.getLastPathSegment();
//        }
//        return result;
//    }

    private String getFilePath(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getCacheDir(), getFileName(uri));
            copyInputStreamToFile(inputStream, file);
            Objects.requireNonNull(inputStream).close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void copyInputStreamToFile(InputStream inputStream, File file) {
        try {
            try (OutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = Objects.requireNonNull(inputStream).read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri uri) {
//                    try {
//                        Log.d(TAG, "onActivityResult: "+getPath(uri));
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    // Handle the returned Uri
//                }
//            });
}