package com.mokshasolutions.pdfpicker;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String TAG = "PDF DATA";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    Context context;
    public File imageFile, source, destination;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    FileUtils fileUtils;
    File   photoFile =null;
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
//            openPdfPicker();
            dispatchTakePictureIntent();
//            mGetContent.launch("application/pdf");
        });
        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri pdfUri = data.getData();
                                Log.d(TAG, "onCreate: "+pdfUri.getPath());
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "No PDF selected", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PDF DATA Exception", e.getMessage() + "");
                    }
                });
    }
    private void dispatchTakePictureIntent() {
        try {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                return;
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//                        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName+".jpg");
                imageUri = FileProvider.getUriForFile(this, "com.mokshasolutions.pdfpicker.fileprovider", imageFile);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//             photoFile = createImageFile();
//            if (photoFile != null) {
//                imageUri = FileProvider.getUriForFile(
//                        this,
//                        "com.mokshasolutions.pdfpicker.fileprovider",
//                        photoFile
//                );
//
//                // Pass the URI to the camera intent
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            } else {
//                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
//            }
//            pdfPickerLauncher.launch(takePictureIntent);
        }catch (Exception e){
           e.printStackTrace();
        }

    }

    private File createImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
            if(requestCode==REQUEST_IMAGE_CAPTURE&& resultCode == Activity.RESULT_OK){
                Log.d(TAG, "onActivityResult: ");
                if (data != null) {
                    Uri uri=data.getData();

                    if (uri != null) {
                        Log.d(TAG, "onActivityResult:uri "+uri.getPath());
                    }
                    if(imageUri!=null){
                        Log.d(TAG, "onActivityResult:imageUri "+imageUri.getPath());
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
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