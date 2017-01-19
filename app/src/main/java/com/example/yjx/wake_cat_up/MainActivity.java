package com.example.yjx.wake_cat_up;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yjx.cryptography.EncrypImpl;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "Wake_the_Cat_Up__";
    private static Bitmap bitmap1;
    private static String cover_path;
    private static String stego_path;

    /*------ google --- for recognize characters of image*/
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE_CAMERA = 2;
    //private static final String TESSBASE_PATH="/mnt/sdcard/tesseract/";//the road for mobils
    private static final String TESSBASE_PATH = "/storage/emulated/0/OCRcandelete/";//the road of Pad
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String IMAGE_PATH = "/mnt/sdcard/ocr.jpg";
    private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/" + DEFAULT_LANGUAGE
            + ".traineddata";
    /*recognize characters of image --------------*/


    /*-------Wang's code --- for camera*/
    private static final int RESULT_CAMERA_ONLY = 189; //照相
    private static final int RESULT_CUT_PHOTO = 666;    //截取
    private static final int RESULT_GET_PHOTO = 999;    //相册选择
    private static final int RESULT_GET_LOCATION = 778; //载体图片的路径
    private static final int RESULT_GETEmbed_LOCATION = 777; //载体图片的路径
    private static final String Value_Cover = "cover";
    private static final String Value_Stego = "stego";

    private ImageView imageview;
    private Button button_chose;//从相册选图
    private Button button_recog;//识别
    private Button button_camera;//照相机
    private Button button_coverImage; //截体图
    private Button button_embed;//嵌入操作--------------new
    private Button button_extract;//提取操作------------new
    private TextView textView;//要删掉的----------------new
    private EditText editText;
    private Uri imageUri;

    private boolean isEncry = true;
    /*---camera-------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resource = this.getBaseContext().getResources();
        this.bitmap1 = BitmapFactory.decodeResource(resource, R.drawable.characterimg);//init bitmap1

        button_chose = (Button) findViewById(R.id.image_library);
        button_recog = (Button) findViewById(R.id.recogonition);
        button_camera = (Button) findViewById(R.id.camera);
        button_coverImage = (Button) findViewById(R.id.coverImage);
        imageview = (ImageView) findViewById(R.id.image1);
        button_embed = (Button) findViewById(R.id.click_embed);//-------new
        button_extract = (Button) findViewById(R.id.click_extract);//---new
        textView = (TextView) findViewById(R.id.textview);//------------new


        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        imageview.setMaxWidth(screenWidth);
        imageview.setMaxHeight(screenWidth / 2);

        editText = (EditText) findViewById(R.id.editText);
//        eT.setKeyListener(null);

        String path = Environment.getExternalStorageDirectory().getPath(); //获取SDcard路径
        File file = new File(path + "/temp.jpg");
        imageUri = Uri.fromFile(file);

        button_chose.setOnClickListener(this);
        button_recog.setOnClickListener(this);
        button_camera.setOnClickListener(this);
        button_coverImage.setOnClickListener(this);
        button_embed.setOnClickListener(this);//-------new
        button_extract.setOnClickListener(this);//-----new
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.image_library:
                getPhoto(imageUri);
                break;
            case R.id.recogonition:
                testOcr();
                break;
            case R.id.camera:
                takePhoto(imageUri);
                break;
            case R.id.coverImage:
                String text = editText.getText().toString();
                String code = "";
                try {
                    if (isEncry) {
                        code = EncrypImpl.encryptAES("wangtianyi", text);
                        isEncry = false;
                    } else {
                        code = EncrypImpl.decryptAES("wangtianyi", text);
                        isEncry = true;
                    }
                    editText.setText(code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pickPhotoRlocation(Value_Cover);
                textView.setText(this.cover_path);
                break;
            case R.id.click_embed:
                //将字符串转换成0 1字符流
                String truestr = editText.getText().toString();
                StringEncode strEncode=new StringEncode();
                String zeroNone=strEncode.Str2Bit(truestr);//------------需要将这个字符串存放到一个文件中
                //------------------------------------------并把这个文件的地址赋给下面的字符串txtname1中就完成了。

                String txtname1="/storage/emulated/0/OCRcandelete/Txtfile/s.txt";
                String filename=this.cover_path;//picture file
                String outfilename = StegoImgPath();//for Pad

                JniTest jniTest=new JniTest();
                int i=jniTest.NumberFromC(txtname1,filename,outfilename);
                textView.setText(String.valueOf(i));
                break;
            case R.id.click_extract:
                pickPhotoRlocation(Value_Stego);
                break;
        }
    }

    /**
     * 拍照后返回结果，显示照片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_CAMERA_ONLY:
                takePhotoWithCut(imageUri);
                break;
            case RESULT_CUT_PHOTO:
                bitmap = data.getParcelableExtra("data");
                imageview.setImageBitmap(bitmap);
                this.bitmap1 = bitmap;
                break;
            case RESULT_GET_PHOTO:
                try {
                    imageUri = data.getData();
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    imageview.setImageBitmap(bitmap);
                    this.bitmap1 = bitmap;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case RESULT_GET_LOCATION:
                Uri originalUri = data.getData();        //获得图片的uri
                TruePath truepath=new TruePath();
                this.cover_path = truepath.getImageAbsolutePath(this,originalUri);
                break;
            case RESULT_GETEmbed_LOCATION:
                Uri originalUri2 = data.getData();        //获得图片的uri
                TruePath truepath2=new TruePath();
                this.stego_path = truepath2.getImageAbsolutePath(this,originalUri2);

                String txtname2 = "/storage/emulated/0/ForH/Messages/extracts.txt"; //output the extracted msg
                JniTest jniTest2=new JniTest();
                String secretmsg = jniTest2.extractMsg(txtname2,this.stego_path);

                //将0 1 字符流转换成真得字符串
                StringEncode strEncode2 = new StringEncode();
                String trueMsg=strEncode2.Bit2Str(secretmsg);
                textView.setText(trueMsg);
                break;

        }
    }
    /**
     * 生成载密图片的地址
     */
    private String StegoImgPath() {
        String before=this.cover_path.substring(0,1+this.cover_path.lastIndexOf('/'));

        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String time = simpleDate.format(now.getTime());

        Random random = new Random();
        String ran = String.valueOf(random.nextInt(1000000));

        String result=before+time+ran+".jpg";
        return result;
    }
    /**
     * 拍照
    */
    private void takePhoto(Uri imageUri) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, RESULT_CAMERA_ONLY);

    }

    /**
     * 寻找图片，返回图片的地址
     */
    private void pickPhotoRlocation(String value) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(value.equals(Value_Cover))
            startActivityForResult(intent, RESULT_GET_LOCATION);
        else
            startActivityForResult(intent, RESULT_GETEmbed_LOCATION);

    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 从相册获取照片
     *
     * @param imageUri
     */
    private void getPhoto(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, RESULT_GET_PHOTO);
    }

    /**
     * 裁减
     */
    private void takePhotoWithCut(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");

        //aspectX aspectY 是宽高比例
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);

        //outputX outputY是裁图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", false);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, RESULT_CUT_PHOTO);
    }


    protected void ocr() {
        //Bitmap bitmap= BitmapFactory.decodeFile(IMAGE_PATH);
        Bitmap bitmap = this.bitmap1;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        ImageView iv = (ImageView) findViewById(R.id.image1);
        iv.setImageBitmap(bitmap);

        Log.v(TAG, "before_the_baseAPI");
        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseAPI.setImage(bitmap);
        String recognitioned = baseAPI.getUTF8Text();
        baseAPI.end();

        Log.v(TAG, "OCR Result: " + recognitioned);

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(recognitioned);

    }

    public void testOcr() {
        mHandle.post(new Runnable() {
                         @Override
                         public void run() {
                             Log.d(TAG, "begin>>>>>>>");
                             ocr();
                             //test();
                         }
                     }

        );

    }

    ;
    private Handler mHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        }

        ;
    };

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //this device has a camera
            return true;
        } else {
            //this device has no camera
            return false;
        }
    }


}

