package com.mokshasolutions.pdfpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button pickPdfButton;
    private TextView selectedPdfTextView;
    private static final int PICK_PDF_FILE = 2;
    private static String TAG = "PDF DATA";
    Context context;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        pickPdfButton = findViewById(R.id.pick_pdf_button);
        selectedPdfTextView = findViewById(R.id.selected_pdf_text_view);
        FileUtils fileUtils = new FileUtils(MainActivity.this);
        pickPdfButton.setOnClickListener(v -> {
            openPdfPicker();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf").addCategory(Intent.CATEGORY_OPENABLE);
//        pdfPickerLauncher.launch(intent);
        startActivityForResult(intent, PICK_PDF_FILE);
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
                    Log.d("PDF DATA", "uri " + uri.getPath());
                    File file = new File(uri.getPath());
                    String path = getPath(uri);
                    dumpImageMetaData(uri);
                    Log.d("PDF DATA", "file1 " + file.getPath());
                    Log.d("PDF DATA", "path " + path);
//                    Log.d("PDF DATA", "path " + path);
                    // Perform operations on the document using its URI.
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String getPath(Uri uri) {
        try {
            String path = null;
            final String column = "_data";
            final String[] projection = {
                    column, MediaStore.Images.Media.DATA
            };
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

            if (cursor == null) {
                Log.d("PDF DATA Path1", "");
                path = uri.getPath();
            } else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(projection[1]);
                path = cursor.getString(column_index);
                Log.d("PDF DATA Path2", path + "");
                cursor.close();
            }

            return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
//            String[] projection = {MediaStore.Files.FileColumns.DATA};
//            Cursor cursor = MainActivity.this.getContentResolver().query(uri, projection, null, null, null);
//
//            if (cursor != null) {
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
//                cursor.moveToFirst();
//                String filePath = cursor.getString(column_index);
//                cursor.close();
//                return filePath;
//            }
//
//            Log.d("PDF DATA", "" + uri.getPath());
//            return uri.getPath();
        } catch (Exception e) {
            Log.e("PDF DATA GET EXCEPTION", "" + e.getMessage());
            return "";
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
}