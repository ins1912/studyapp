package com.example.studyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;

import com.camerakit.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.camerakit.type.CameraSize;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;
//import jpegkit.Jpeg;
//import jpegkit.JpegImageView;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private CameraKitView cameraView;
    private Toolbar toolbar;

    private AppCompatTextView facingText;
    private AppCompatTextView flashText;
    private AppCompatTextView previewSizeText;
    private AppCompatTextView photoSizeText;

    private Button flashOnButton;
    private Button flashOffButton;

    private FloatingActionButton photoButton;

    private Button facingFrontButton;
    private Button facingBackButton;

    private Button permissionsButton;

    private JpegImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);

        facingText = findViewById(R.id.facingText);
        flashText = findViewById(R.id.flashText);
        previewSizeText = findViewById(R.id.previewSizeText);
        photoSizeText = findViewById(R.id.photoSizeText);

        photoButton = findViewById(R.id.photoButton);
        photoButton.setOnClickListener(photoOnClickListener);

        flashOnButton = findViewById(R.id.flashOnButton);
        flashOffButton = findViewById(R.id.flashOffButton);

        flashOnButton.setOnClickListener(flashOnOnClickListener);
        flashOffButton.setOnClickListener(flashOffOnClickListener);

        facingFrontButton = findViewById(R.id.facingFrontButton);
        facingBackButton = findViewById(R.id.facingBackButton);

        facingFrontButton.setOnClickListener(facingFrontOnClickListener);
        facingBackButton.setOnClickListener(facingBackOnClickListener);

        permissionsButton = findViewById(R.id.permissionsButton);
        permissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.requestPermissions(MainActivity.this);
            }
        });

        imageView = findViewById(R.id.imageView);

        cameraView.setPermissionsListener(new CameraKitView.PermissionsListener() {
            @Override
            public void onPermissionsSuccess() {
                permissionsButton.setVisibility(View.GONE);
            }

            @Override
            public void onPermissionsFailure() {
                permissionsButton.setVisibility(View.VISIBLE);
            }
        });

        cameraView.setCameraListener(new CameraKitView.CameraListener() {
            @Override
            public void onOpened() {
                Log.v("CameraKitView", "CameraListener: onOpened()");
            }

            @Override
            public void onClosed() {
                Log.v("CameraKitView", "CameraListener: onClosed()");
            }
        });

        cameraView.setPreviewListener(new CameraKitView.PreviewListener() {
            @Override
            public void onStart() {
                Log.v("CameraKitView", "PreviewListener: onStart()");
                updateInfoText();
            }

            @Override
            public void onStop() {
                Log.v("CameraKitView", "PreviewListener: onStop()");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        cameraView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_about) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.about_dialog_title)
                    .setMessage(R.string.about_dialog_message)
                    .setNeutralButton("Dismiss", null)
                    .show();

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#91B8CC"));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(Html.fromHtml("<b>Dismiss</b>"));
            return true;
        }

        if (item.getItemId() == R.id.main_menu_gallery) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivity(intent);
            return true;
        }

        return false;
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView view, final byte[] photo) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Jpeg jpeg = new Jpeg(photo);
                            imageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setJpeg(jpeg);
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    };

    private View.OnClickListener flashOnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cameraView.getFlash() != CameraKit.FLASH_ON) {
                cameraView.setFlash(CameraKit.FLASH_ON);
                updateInfoText();
            }
        }
    };

    private View.OnClickListener flashOffOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cameraView.getFlash() != CameraKit.FLASH_OFF) {
                cameraView.setFlash(CameraKit.FLASH_OFF);
                updateInfoText();
            }
        }
    };

    private View.OnClickListener facingFrontOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.setFacing(CameraKit.FACING_FRONT);
        }
    };

    private View.OnClickListener facingBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.setFacing(CameraKit.FACING_BACK);
        }
    };

    private void updateInfoText() {
        String facingValue = cameraView.getFacing() == CameraKit.FACING_BACK ? "BACK" : "FRONT";
        facingText.setText(Html.fromHtml("<b>Facing:</b> " + facingValue));

        String flashValue = "OFF";
        switch (cameraView.getFlash()) {
            case CameraKit.FLASH_OFF: {
                flashValue = "OFF";
                break;
            }

            case CameraKit.FLASH_ON: {
                flashValue = "ON";
                break;
            }

            case CameraKit.FLASH_AUTO: {
                flashValue = "AUTO";
                break;
            }

            case CameraKit.FLASH_TORCH: {
                flashValue = "TORCH";
                break;
            }
        }
        flashText.setText(Html.fromHtml("<b>Flash:</b> " + flashValue));

        CameraSize previewSize = cameraView.getPreviewResolution();
        if (previewSize != null) {
            previewSizeText.setText(Html.fromHtml(String.format("<b>Preview Resolution:</b> %d x %d", previewSize.getWidth(), previewSize.getHeight())));
        }

        CameraSize photoSize = cameraView.getPhotoResolution();
        if (photoSize != null) {
            photoSizeText.setText(Html.fromHtml(String.format("<b>Photo Resolution:</b> %d x %d", photoSize.getWidth(), photoSize.getHeight())));
        }
    }

}

















//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.media.ExifInterface;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.MediaStore;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AlertDialog;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.gun0912.tedpermission.PermissionListener;
//import com.gun0912.tedpermission.TedPermission;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import static android.os.Environment.DIRECTORY_PICTURES;
//
//
//
//
//
//
////public class MediaScanner {
////    private Context ctxt;
////    private String file_Path;
////    private MediaScannerConnection mMediaScanner;
////    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;
////
////
////
////    public static MediaScanner newInstance(Context context)
////    {
////        return new MediaScanner (context);
////    }
////
////    private MediaScanner (Context context) {
////
////        ctxt = context;
////
////    }
////
////    public void mediaScanning(final String path) {
////        if (mMediaScanner == null) {
////            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {
////                @Override public void onMediaScannerConnected() {
////                    mMediaScanner.scanFile(file_Path, null);
////                }
////
////                @Override public void onScanCompleted(String path, Uri uri) {
////                    System.out.println("::::MediaScan Success::::");
////
////                    mMediaScanner.disconnect();
////                }
////            };
////
////            mMediaScanner = new MediaScannerConnection(mContext, mMediaScannerClient);
////        }
////
////        mPath = path;
////
////        mMediaScanner.connect();
////    }
////}
//
//
//
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int MY_PERMISSION_CAMERA = 1111;
//    private static final int REQUEST_TAKE_PHOTO = 2222;
//    private static final int REQUEST_TAKE_ALBUM = 3333;
//    private static final int REQUEST_IMAGE_CROP = 4444;
//
//
//
//
//
//
//
//
//
//    Button btn_capture, btn_album;
//    ImageView iv_view;
//
//    String mCurrentPhotoPath;
//
//    Uri imageUri;
//    Uri photoURI, albumURI;
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        btn_capture = findViewById(R.id.btn_capture);
//        btn_album = findViewById(R.id.btn_album);
//        iv_view = findViewById(R.id.iv_view);
//
//        btn_capture.setOnClickListener(v -> captureCamera());
//
//        btn_album.setOnClickListener(v -> getAlbum());
//
//        checkPermission();
//    }
//
//    @SuppressLint("QueryPermissionsNeeded")
//    private void captureCamera(){
//        String state = Environment.getExternalStorageState();
//        // 외장 메모리 검사
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException ex) {
//                    Log.e("captureCamera Error", ex.toString());
//                }
//                if (photoFile != null) {
//                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
//
//                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
//                    imageUri = providerURI;
//
//                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
//
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                }
//            }
//        } else {
//            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public File createImageFile() throws IOException {
//        // Create an image file name
//        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + ".jpg";
//        File imageFile = null;
//        File storageDir = new File(Environment.getExternalStorageState() + "/Pictures", "ins");
//
//        if (!storageDir.exists()) {
//            Log.i("mCurrentPhotoPath1", storageDir.toString());
//            storageDir.mkdirs();
//        }
//
//
//        boolean isDirectoryCreated=storageDir.exists();
//        if (!isDirectoryCreated) {
//            isDirectoryCreated= storageDir.mkdir();
//        }
//        if(isDirectoryCreated) {
//            // do something
//        }
//
//
//
//
//        imageFile = new File(storageDir, imageFileName);
//        mCurrentPhotoPath = imageFile.getAbsolutePath();
//
//        return imageFile;
//    }
//
//
//    private void getAlbum(){
//        Log.i("getAlbum", "Call");
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
//    }
//
//
//
////    private MediaScanner ms = MediaScanner.newInstance(MainActivity.this);
////
////    {
////
////        try {
////            // TODO : 미디어 스캔
////            ms.mediaScanning(mCurrentPhotoPath);
////        } catch (Exception e) {
////            e.printStackTrace();
////            Log.d("MediaScan", "ERROR" + e);
////        } finally {
////        }
////    }
//
//
//
/////////////////여기가 안돼서 지식인 질문
//    private void galleryAddPic(){
//        Log.i("galleryAddPic", "Call");
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
////        File f = new File(mCurrentPhotoPath);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = FileProvider.getUriForFile(this, getPackageName(), f);
//        MediaScannerConnection.scanFile(this, new String[]{f.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//            public void onScanCompleted(String path, Uri contentUri) {
//                m_FirstCaptureURI = contentUri;
//                Log.e("Main", m_FirstCaptureURI.toString());
//            }
//        });
////        Uri contentUri = FileProvider.getUriForFile(this, getPackageName(), f);
////        mediaScanIntent.setData(contentUri);
////        sendBroadcast(mediaScanIntent);
//        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
//    }
//
//    //File f = new File(mCurrentPhotoPath);
//    //   MediaScannerConnection.scanFile(this,
//    //            new String[]{f.toString()},
//    //        null, null);
//
//
//
//    // 카메라 전용 크랍
////    public void cropImage(){
////        Log.i("cropImage", "Call");
////        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);
////
////        Intent cropIntent = new Intent("com.android.camera.action.CROP");
////
////        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
////        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////        cropIntent.setDataAndType(photoURI, "image/*");
////        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
////        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
////        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
////        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
////        cropIntent.putExtra("scale", true);
////        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
////        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
////    }
//
////    public void cropImage() {
////        this.grantUriPermission("com.android.camera", photoURI,
////                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
////        Intent intent = new Intent("com.android.camera.action.CROP");
////        intent.setDataAndType(photoURI, "image/*");
////
////        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
////        grantUriPermission(list.get(0).activityInfo.packageName, photoURI,
////                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
////        int size = list.size();
////        if (size == 0) {
////            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
////            return;
////        } else {
////            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////            intent.putExtra("crop", "true");
////            intent.putExtra("aspectX", 4);
////            intent.putExtra("aspectY", 3);
////            intent.putExtra("scale", true);
////            File croppedFileName = null;
////            try {
////                croppedFileName = createImageFile();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
////            File tempFile = new File(folder.toString(), croppedFileName.getName());
////
////            photoURI = FileProvider.getUriForFile(SelectPhotoDialogActivity.this,
////                    "com.example.test.provider", tempFile);
////
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////
////
////            intent.putExtra("return-data", false);
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
////            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행
////
////            Intent i = new Intent(intent);
////            ResolveInfo res = list.get(0);
////            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////            grantUriPermission(res.activityInfo.packageName, photoURI,
////                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
////
////            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
////            startActivityForResult(i, CROP_FROM_CAMERA);
////
////
////        }
////
////    }
//
//    public void cropSingleImage(Uri photoUriPath){
//        Log.i("cropSingleImage", "Call");
//        Log.i("cropSingleImage", "photoUriPath : " + photoUriPath);
//
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//
//        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법, addFlags로도 에러 나서 setFlags
//        // 누가 버전 처리방법임
//        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        cropIntent.setDataAndType(photoUriPath, "image/*");
//        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
//        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
//        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
//        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
//        Log.i("cropSingleImage", "photoUriPath22 : " + photoUriPath);
//
//
//        cropIntent.putExtra("scale", true);
//        cropIntent.putExtra("output", photoUriPath); // 크랍된 이미지를 해당 경로에 저장
//
//
////          왜 activityInfo가 안되냐.........
////         같은 photoUriPath에 저장하려면 아래가 있어야함
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(cropIntent, 0);
//        grantUriPermission(list.get(0).activityInfo.packageName, photoUriPath,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//
//
//        Intent i = new Intent(cropIntent);
//        ResolveInfo res = (ResolveInfo) list.get(0);
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        grantUriPermission(res.activityInfo.packageName, photoUriPath,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//
//        startActivityForResult(i, REQUEST_IMAGE_CROP);
//    }
//
//
//
//
//
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_TAKE_PHOTO:
//                if (resultCode == Activity.RESULT_OK) {
//                    try {
//                        Log.i("REQUEST_TAKE_PHOTO", "OK");
//                        galleryAddPic();
//
//                        iv_view.setImageURI(imageUri);
//                    } catch (Exception e) {
//                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case REQUEST_TAKE_ALBUM:
//                if (resultCode == Activity.RESULT_OK) {
//
//                    if (data.getData() != null) {
//                        try {
//                            File albumFile = null;
//                            albumFile = createImageFile();
//                            photoURI = data.getData();
//                            albumURI = FileProvider.getUriForFile(this,
//                                    "com.example.studyapp.fileprovider", albumFile);
//                            cropSingleImage(photoURI);
//                        } catch (Exception e) {
//                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
//                        }
//                    }
//                }
//                break;
//
//            case REQUEST_IMAGE_CROP:
//                if (resultCode == Activity.RESULT_OK) {
//
//                    galleryAddPic();
//                    iv_view.setImageURI(albumURI);
//                }
//                break;
//        }
//    }
//
//
//
//
//
//
//    private void checkPermission(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
//            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
//
//            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                new AlertDialog.Builder(this)
//                        .setTitle("알림")
//                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
//                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                intent.setData(Uri.parse("package:" + getPackageName()));
//                                startActivity(intent);
//                            }
//                        })
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                finish();
//                            }
//                        })
//                        .setCancelable(false)
//                        .create()
//                        .show();
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSION_CAMERA:
//                for (int i = 0; i < grantResults.length; i++) {
//                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
//                    if (grantResults[i] < 0) {
//                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//                // 허용했다면 이 부분에서..
//
//                break;
//        }
//    }
//}
